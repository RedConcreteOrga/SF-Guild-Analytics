# SF-Guild-Analytics
SF Guild Analytics ist ein selbst gehostetes, Open-Source-Web-Tool für Gildenleiter. Spieler-Stats werden regelmäßig erfasst (manuell oder semi-automatisch), über Zeit gespeichert und grafisch ausgewertet, inklusive Fortschritt, Prozent-Verbesserungen und Vergleich innerhalb der Gilde.
✅ 1️⃣ Feature-Liste (Roadmap-fähig)
👤 Spieler & Gilden

Gilden anlegen (Name, Server, Fraktion)

Spieler anlegen (Name, Klasse, Level, Eintrittsdatum)

Rollen: Admin / Gildenlead / Member

Mehrere Gilden pro Instanz möglich

📥 Datenerfassung

Manuelle Eingabe von Stats (Formular)

Zeitbasierte Snapshots (z. B. täglich / wöchentlich)

Optional: CSV-Import

Historisierung aller Werte (kein Überschreiben!)

📊 Grafische Auswertungen (Kernfeature)

Zeitverlauf-Charts pro Spieler

Prozentuale Verbesserung (z. B. „+18 % Stärke in 14 Tagen“)

Vergleich Spieler ↔ Gilde

Ranglisten (Top-Fortschritt, Aktivität, Wachstum)

🧠 Analyse & KPIs

Aktivitäts-Score (wer liefert Progress?)

Wachstumsgeschwindigkeit

Durchschnittswerte der Gilde

Warnungen bei Stagnation (optional)

🌐 Web & Sharing

Web-Dashboard

Öffentliche / private Ansichten

Read-only Links für Mitglieder

Mobile-freundlich

⚙️ Self-Hosting & Open Source

Docker-Setup

Keine externe Abhängigkeit

Volle Datenkontrolle

🧠 2️⃣ Konzept-Entwurf (Was tut das Tool?)
🔁 Datenfluss
Spieler trägt Stats ein
→ Snapshot wird gespeichert
→ Tool berechnet Differenzen
→ Graphen + KPIs werden aktualisiert

🗂 Beispiel-Spieler-Snapshot

Datum

Level

Stärke / Geschick / Intelligenz

Ausdauer

Lebenspunkte

Ehrenhalle

Dungeon-Fortschritt (%)

Festung (Level, Arbeiter)

Gold / Pilze (optional)

Aktivitätsmarker (Login, Beitrag)

📈 Berechnungen

Absolute Differenz (Δ)

Prozentuale Veränderung

Durchschnitt / Median

Trend (↑ ↓ →)

🖼 3️⃣ Beispiel-Screens & Diagramm-Templates
📊 Spieler-Dashboard
4

Enthält:

Level-Kurve

Attribut-Wachstum

KPI-Cards:

+% Gesamtstärke

Aktivität

Platz in der Gilde

🏰 Gilden-Übersicht
4

Enthält:

Gilden-Durchschnitt

Ranking nach Wachstum

Aktivitäts-Heatmap

Fortschritt gesamt

🔍 Vergleich Spieler ↔ Spieler

Enthält:

Radar-Charts

Balken-Vergleiche

Trend-Indikatoren

🧰 4️⃣ Tech-Stack-Vorschlag (bewährt & simpel)
🖥 Frontend

React + Vite

Chart.js oder ECharts

TailwindCSS

Mobile-first

🔙 Backend

Node.js (NestJS) oder Python (FastAPI)

REST-API

Auth via JWT

🗄 Datenbank

PostgreSQL

Zeitreihen-fähig

Saubere Relationsstruktur

🐳 Deployment

Docker & Docker Compose

.env-Konfiguration

Reverse Proxy optional (Traefik / Nginx)

🔓 Lizenz

MIT oder AGPL (Community-freundlich)

📦 Ordnerstruktur (GitHub-ready)
sf-guild-analytics/
├─ backend/
├─ frontend/
├─ docker/
├─ docs/
│  ├─ screenshots/
│  ├─ api.md
│  └─ roadmap.md
├─ README.md
└─ docker-compose.yml

🎯 Warum das ein starkes Community-Projekt wäre

✔️ Gibt es aktuell nicht
✔️ Hoher Mehrwert für Gilden-Leads
✔️ Self-Hosted → kein Drama mit SF-Regeln
✔️ Erweiterbar (Discord-Bot, Exports, etc.)
✔️ Perfekt für Open Source
