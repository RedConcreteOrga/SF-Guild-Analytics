#!/bin/bash

echo "==> Stoppe Frontend und Backend..."
docker compose stop frontend backend

echo "==> Baue Backend neu..."
docker compose build backend

echo "==> Starte Backend und Frontend..."
docker compose up -d backend frontend

echo "==> Warte auf Backend..."
until curl -s http://localhost:41414/api/guilds > /dev/null 2>&1; do
  sleep 2
  echo "    Backend noch nicht bereit, warte..."
done

echo ""
echo "Fertig!"
echo "  Frontend: http://localhost:5173"
echo "  Backend:  http://localhost:41414"
