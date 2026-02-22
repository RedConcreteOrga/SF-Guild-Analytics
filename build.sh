#!/bin/bash

# ─── Verwendung ───────────────────────────────────────────────────────────────
# ./build.sh               → Entwicklung (Standard)
# ./build.sh dev           → Entwicklung (nur Backend neu bauen)
# ./build.sh prod          → Produktion (Frontend + Backend neu bauen)
# ./build.sh prod --clean  → Produktion ohne Docker-Cache (bei hartnäckigen Fehlern)

set -e  # Abbrechen bei Fehler

ENV=${1:-dev}
CLEAN=${2:-""}

# ─── Umgebung auswählen ───────────────────────────────────────────────────────
case "$ENV" in
  dev)
    COMPOSE_FILE="docker-compose.yml"
    BACKEND_URL="http://localhost:41414"
    FRONTEND_URL="http://localhost:5173"
    ;;
  prod)
    COMPOSE_FILE="docker-compose.prod.yml"
    if [ -f .env ]; then
      source .env
      BACKEND_URL="https://${DOMAIN:-localhost}"
      FRONTEND_URL="https://${DOMAIN:-localhost}"
    else
      echo "FEHLER: .env-Datei nicht gefunden."
      echo "        cp .env.example .env  und dann ausfüllen."
      exit 1
    fi
    ;;
  *)
    echo "Unbekannte Umgebung: '$ENV'"
    echo "Verwendung: $0 [dev|prod] [--clean]"
    exit 1
    ;;
esac

COMPOSE="docker compose -f $COMPOSE_FILE"
BUILD_FLAGS=""
if [ "$CLEAN" = "--clean" ]; then
  BUILD_FLAGS="--no-cache"
fi

echo "╔═══════════════════════════════════════════════════════╗"
if [ "$CLEAN" = "--clean" ]; then
echo "║   SF Guild Analytics — Build ($ENV, clean)          ║"
else
echo "║   SF Guild Analytics — Build ($ENV)                 ║"
fi
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

# ─── Dev: Backend + Frontend ──────────────────────────────────────────────────
if [ "$ENV" = "dev" ]; then
  echo "==> Stoppe Frontend und Backend..."
  $COMPOSE stop frontend backend

  echo "==> Baue Frontend und Backend neu..."
  $COMPOSE build $BUILD_FLAGS frontend backend

  echo "==> Starte Frontend und Backend..."
  $COMPOSE up -d frontend backend

# ─── Prod: Frontend + Backend ─────────────────────────────────────────────────
else
  echo "==> Stoppe Frontend und Backend..."
  $COMPOSE stop frontend backend

  if [ "$CLEAN" = "--clean" ]; then
    echo "==> Entferne alte Images..."
    docker image rm sf-guild-analytics-frontend sf-guild-analytics-backend 2>/dev/null || true
  fi

  echo "==> Baue Frontend und Backend neu..."
  $COMPOSE build $BUILD_FLAGS frontend backend

  echo "==> Starte Frontend und Backend..."
  $COMPOSE up -d frontend backend
fi

# ─── Warten bis Backend bereit ist ────────────────────────────────────────────
echo ""
echo "==> Warte auf Backend..."
ATTEMPTS=0
MAX_ATTEMPTS=30

until curl -s "${BACKEND_URL}/api/guilds" > /dev/null 2>&1; do
  ATTEMPTS=$((ATTEMPTS + 1))
  if [ $ATTEMPTS -ge $MAX_ATTEMPTS ]; then
    echo ""
    echo "FEHLER: Backend nach ${MAX_ATTEMPTS} Versuchen nicht erreichbar."
    echo "        Logs prüfen: $COMPOSE logs backend"
    exit 1
  fi
  sleep 2
  echo "    Warte... (${ATTEMPTS}/${MAX_ATTEMPTS})"
done

# ─── Fertig ───────────────────────────────────────────────────────────────────
echo ""
echo "Fertig!"
echo ""
echo "  Frontend:  $FRONTEND_URL"
echo "  Backend:   ${BACKEND_URL}/api"
echo ""
if [ "$ENV" = "prod" ]; then
  echo "  Logs:      docker compose -f $COMPOSE_FILE logs -f"
else
  echo "  Logs:      docker compose logs -f"
fi
echo ""
