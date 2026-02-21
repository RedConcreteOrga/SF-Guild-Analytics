#!/bin/bash

# ─── Verwendung ───────────────────────────────────────────────────────────────
# ./restart.sh          → Entwicklung (Standard)
# ./restart.sh dev      → Entwicklung
# ./restart.sh prod     → Produktion

set -e  # Abbrechen bei Fehler

ENV=${1:-dev}

# ─── Umgebung auswählen ───────────────────────────────────────────────────────
case "$ENV" in
  dev)
    COMPOSE_FILE="docker-compose.yml"
    BACKEND_URL="http://localhost:41414"
    FRONTEND_URL="http://localhost:5173"
    ;;
  prod)
    COMPOSE_FILE="docker-compose.prod.yml"
    # Domain aus .env lesen falls vorhanden
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
    echo "Verwendung: $0 [dev|prod]"
    exit 1
    ;;
esac

COMPOSE="docker compose -f $COMPOSE_FILE"

echo "╔══════════════════════════════════════════╗"
echo "║   SF Guild Analytics — Restart ($ENV)   ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# ─── Dev: nur Backend neu bauen (Frontend läuft als Dev-Server mit Hot-Reload) ─
if [ "$ENV" = "dev" ]; then
  echo "==> Stoppe Backend..."
  $COMPOSE stop backend

  echo "==> Baue Backend neu..."
  $COMPOSE build backend

  echo "==> Starte Backend..."
  $COMPOSE up -d backend

# ─── Prod: Frontend + Backend neu bauen ────────────────────────────────────────
else
  echo "==> Stoppe Frontend und Backend..."
  $COMPOSE stop frontend backend

  echo "==> Baue Frontend und Backend neu..."
  $COMPOSE build frontend backend

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
echo "✓ Fertig!"
echo ""
echo "  Frontend:  $FRONTEND_URL"
echo "  Backend:   ${BACKEND_URL}/api"
echo ""

if [ "$ENV" = "prod" ]; then
  echo "  Logs:      docker compose -f $COMPOSE_FILE logs -f"
else
  echo "  Logs:      docker compose logs -f backend"
fi

echo ""
