SF Guild Analytics

SF Guild Analytics ist ein selbst gehostetes, Open-Source-Webtool für Gildenleiter in Shakes & Fidget.
Es ermöglicht das strukturierte Erfassen von Spieler-Stats, speichert diese über Zeit und visualisiert Fortschritt, Trends und prozentuale Verbesserungen in übersichtlichen Dashboards.

Fokus: Progress sichtbar machen, nicht nur Zahlen auflisten.

🚀 Motivation

Aktuelle SF-Community-Tools zeigen überwiegend statische Tabellen (Momentaufnahmen).
Was fehlt, ist ein Tool, das beantwortet:

Wie stark hat sich ein Spieler verbessert?

Wer macht konstant Fortschritt – und wer stagniert?

Wie entwickelt sich die Gilde insgesamt über Wochen/Monate?

Wer trägt wirklich zum Wachstum bei?

SF Guild Analytics schließt genau diese Lücke.

✨ Features
👥 Gilden & Spieler

Gilden anlegen (Name, Server, Fraktion)

Spieler anlegen (Name, Klasse, Level, Eintrittsdatum)

Rollen & Rechte:

Admin

Gilden-Lead

Member (Read-only)

📥 Datenerfassung

Manuelle Eingabe von Spieler-Stats

Zeitbasierte Snapshots (z. B. täglich / wöchentlich)

CSV-Import (optional)

Historisierung aller Daten (keine Überschreibung)

📊 Grafische Auswertungen (Kernfeature)

Zeitverlauf-Diagramme pro Spieler

Prozentuale Verbesserung (z. B. +15 % Stärke in 14 Tagen)

Vergleich Spieler ↔ Spieler

Vergleich Spieler ↔ Gilden-Durchschnitt

Ranglisten nach Wachstum & Aktivität

🧠 Analyse & KPIs

Fortschritts-Score pro Spieler

Aktivitäts-Score

Durchschnittswerte der Gilde

Trend-Indikatoren (steigend / stagnierend / fallend)

Optional: Warnungen bei fehlendem Fortschritt

🌐 Web & Zugriff

Web-Dashboard (Desktop & Mobile)

Private & öffentliche Ansichten

Read-only Links für Mitglieder

Keine externe Cloud notwendig

🧩 Beispiel-Daten (Snapshot)

Ein Snapshot speichert u. a.:

Datum

Level

Stärke / Geschick / Intelligenz

Ausdauer

Lebenspunkte

Ehrenhalle

Dungeon-Fortschritt (%)

Festung (Level, Arbeiter)

Gold / Pilze (optional)

Aktivitätsmarker

Alle Snapshots werden versioniert gespeichert und dienen als Basis für Analysen.

🏗 Architektur (High Level)
Browser (Frontend)
   ↓
REST API
   ↓
Backend (Business Logic)
   ↓
PostgreSQL (Snapshots & Historie)

🧰 Tech-Stack
Frontend

React + Vite

Chart.js oder Apache ECharts

TailwindCSS

Mobile-first Design

Backend

Node.js (NestJS) oder Python (FastAPI)

REST API

JWT-basierte Authentifizierung

Datenbank

PostgreSQL

Zeitreihen-freundliches Schema

Deployment

Docker & Docker Compose

.env-basierte Konfiguration

Reverse Proxy optional (Nginx / Traefik)

📦 Projektstruktur
sf-guild-analytics/
├─ backend/
├─ frontend/
├─ docker/
├─ docs/
│  ├─ api.md
│  ├─ roadmap.md
│  └─ screenshots/
├─ docker-compose.yml
└─ README.md

🔒 Datenschutz & Fair Use

Keine automatisierte Datenerfassung aus dem Spiel

Keine Verbindung zu offiziellen SF-Servern

Alle Daten werden manuell oder freiwillig eingegeben

Vollständig self-hosted → volle Datenkontrolle

🛠 Installation (Kurzfassung)
git clone https://github.com/<org>/sf-guild-analytics.git
cd sf-guild-analytics
cp .env.example .env
docker compose up -d


Danach ist das Dashboard unter http://localhost erreichbar.

🛣 Roadmap (Auszug)

 Basis-Dashboard

 Spieler-Zeitverlauf-Charts

 Prozent- & Trend-Berechnung

 CSV-Import

 Rollen & Rechte

 Export (PNG / CSV)

 Discord-Integration (optional)

🤝 Contributing

Contributions sind ausdrücklich willkommen:

Feature-Vorschläge

Bug-Reports

UI-Verbesserungen

Dokumentation

Bitte nutze Issues & Pull Requests.

📄 Lizenz

Dieses Projekt ist Open Source.
Lizenz: MIT (oder AGPL – je nach Community-Entscheid)

❤️ Community

Dieses Projekt ist von der SF-Community für die SF-Community gedacht.
Feedback, Ideen und Diskussionen sind ausdrücklich erwünscht.
