#!/bin/bash
# Build-Script für Proxmox VE (AppArmor-Workaround)
# Artifacts werden in privilegierten Containern gebaut, dann Images erstellt
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== 0/4 Neuesten Code holen ==="
git pull

echo "=== 1/4 Backend JAR bauen ==="
docker run --rm \
  --security-opt apparmor=unconfined \
  -v "$SCRIPT_DIR/backend/src:/home/app/src" \
  -v "$SCRIPT_DIR/backend/pom.xml:/home/app/pom.xml" \
  -v "$SCRIPT_DIR/backend/target:/home/app/target" \
  maven:3.9.6-eclipse-temurin-17 \
  sh -c "rm -rf /home/app/target/* && mvn -f /home/app/pom.xml package -DskipTests"

echo "=== 2/4 Frontend dist bauen ==="
docker run --rm \
  --security-opt apparmor=unconfined \
  -v "$SCRIPT_DIR/frontend:/app" \
  -w /app \
  -e VITE_API_BASE_URL=/api \
  node:20-alpine \
  sh -c "npm ci && npm run build"

echo "=== 3/4 Docker Images erstellen ==="
# Backend Image (nur Runtime, JAR liegt schon vor)
docker build -t sf-guild-analytics-backend -f "$SCRIPT_DIR/backend/Dockerfile.runtime" "$SCRIPT_DIR/backend"

# Frontend Image (nur Runtime, dist liegt schon vor)
docker build -t sf-guild-analytics-frontend -f "$SCRIPT_DIR/frontend/Dockerfile.runtime" "$SCRIPT_DIR/frontend"

echo "=== 4/4 Container starten ==="
docker compose -f "$SCRIPT_DIR/docker-compose.prod.yml" up -d

echo ""
echo "=== Fertig! ==="
docker compose -f "$SCRIPT_DIR/docker-compose.prod.yml" ps
