# SF Guild Analytics

SF Guild Analytics ist ein selbst gehostetes, Open-Source-Webtool für Gildenleiter in Shakes & Fidget.
Es ermöglicht das strukturierte Erfassen von Spieler-Stats, speichert diese über Zeit und visualisiert Fortschritt, Trends und prozentuale Verbesserungen in übersichtlichen Dashboards.

**Fokus: Progress sichtbar machen, nicht nur Zahlen auflisten.**

---

## Motivation

Aktuelle SF-Community-Tools zeigen überwiegend statische Tabellen (Momentaufnahmen). Was fehlt, ist ein Tool, das beantwortet:

- Wie stark hat sich ein Spieler verbessert?
- Wer macht konstant Fortschritt – und wer stagniert?
- Wie entwickelt sich die Gilde insgesamt über Wochen/Monate?
- Wer trägt wirklich zum Wachstum bei?

SF Guild Analytics schließt genau diese Lücke.

---

## Features

### Gilden & Spieler
- Gilden anlegen (Name, Server, Fraktion)
- Spieler anlegen (Name, Klasse, Level, Eintrittsdatum)
- Rollen & Rechte: Admin, Gilden-Lead, Member (Read-only)

### Datenerfassung
- Manuelle Eingabe von Spieler-Stats
- Zeitbasierte Snapshots (z. B. täglich / wöchentlich)
- CSV-Import (optional)
- Historisierung aller Daten (keine Überschreibung)

### Grafische Auswertungen
- Zeitverlauf-Diagramme pro Spieler
- Prozentuale Verbesserung (z. B. +15 % Stärke in 14 Tagen)
- Vergleich Spieler ↔ Spieler
- Vergleich Spieler ↔ Gilden-Durchschnitt
- Ranglisten nach Wachstum & Aktivität

### Analyse & KPIs
- Fortschritts-Score pro Spieler
- Aktivitäts-Score
- Durchschnittswerte der Gilde
- Trend-Indikatoren (steigend / stagnierend / fallend)
- Optional: Warnungen bei fehlendem Fortschritt

### Web & Zugriff
- Web-Dashboard (Desktop & Mobile)
- Private & öffentliche Ansichten
- Read-only Links für Mitglieder
- Keine externe Cloud notwendig

---

## Beispiel-Daten (Snapshot)

Ein Snapshot speichert u. a.:

| Feld | Beschreibung |
|------|--------------|
| Datum | Zeitpunkt der Erfassung |
| Level | Spielerlevel |
| Stärke / Geschick / Intelligenz | Primärattribute |
| Ausdauer | Ausdauerwert |
| Lebenspunkte | HP |
| Ehrenhalle | Rang / Punkte |
| Dungeon-Fortschritt | Prozentualer Fortschritt |
| Festung | Level & Arbeiter |
| Gold / Pilze | Optional |
| Aktivitätsmarker | Letzter Login / Aktivität |

Alle Snapshots werden versioniert gespeichert und dienen als Basis für alle Analysen.

---

## Architektur

```
Browser (Vue 3 Frontend)
        ↓
   REST API (:41414)
        ↓
Backend (Spring Boot)
        ↓
 PostgreSQL (:5433)
```

---

## Tech-Stack

| Schicht | Technologie |
|---------|-------------|
| Frontend | Vue 3, Vite, TailwindCSS, Pinia, Vue Router |
| Backend | Java 17, Spring Boot 3, Spring Security, JPA/Hibernate |
| Authentifizierung | JWT (JJWT) |
| Datenbank | PostgreSQL 15 |
| Deployment | Docker & Docker Compose |

---

## Projektstruktur

```
sf-guild-analytics/
├── backend/          # Spring Boot Anwendung
├── frontend/         # Vue 3 + Vite Anwendung
├── docker-compose.yml
└── README.md
```

---

## Installation & Start

### Voraussetzungen

- [Docker & Docker Compose](https://docs.docker.com/get-docker/) (für alle Varianten)
- [Java 17](https://adoptium.net/) + [Maven](https://maven.apache.org/) (nur für lokale Backend-Entwicklung)
- [Node.js 18+](https://nodejs.org/) + npm (nur für lokale Frontend-Entwicklung)

---

### Option A – Alles per Docker Compose starten (empfohlen)

```bash
git clone https://github.com/RedConcrete/sf-guild-analytics.git
cd sf-guild-analytics
docker compose up -d
```

Danach sind die Dienste unter folgenden Adressen erreichbar:

| Dienst | URL |
|--------|-----|
| Frontend (Vue 3) | http://localhost:5173 |
| Backend API | http://localhost:41414 |
| PostgreSQL | localhost:5433 |

Logs aller Dienste ansehen:
```bash
docker compose logs -f
```

Dienste stoppen:
```bash
docker compose down
```

---

### Option B – Lokale Entwicklung

Diese Variante eignet sich, wenn du am Backend oder Frontend entwickeln möchtest.

**1. Datenbank starten (Docker)**

```bash
docker compose up -d db
```

Die PostgreSQL-Instanz ist danach unter `localhost:5433` erreichbar.

**2. Backend starten**

```bash
cd backend
mvn spring-boot:run
```

Das Backend startet unter: `http://localhost:41414`

> **Hinweis:** Beim ersten Start legt Hibernate die Tabellen automatisch an (`ddl-auto: update`).
> Ein Admin-User muss manuell in die `users`-Tabelle eingetragen werden (BCrypt-Hash für das Passwort).

**3. Frontend starten**

```bash
cd frontend
npm install
npm run dev
```

Das Frontend startet unter: `http://localhost:5173`

---

### Datenbank zurücksetzen

```bash
docker compose down -v   # Löscht auch das Daten-Volume
docker compose up -d db
```

---

## API-Endpunkte (Auszug)

| Methode | Endpunkt | Beschreibung |
|---------|----------|--------------|
| POST | `/api/auth/login` | Login, gibt JWT zurück |

Beispiel-Login:
```bash
curl -X POST http://localhost:41414/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "yourpassword"}'
```

---

## Datenschutz & Fair Use

- Keine automatisierte Datenerfassung aus dem Spiel
- Keine Verbindung zu offiziellen SF-Servern
- Alle Daten werden manuell oder freiwillig eingegeben
- Vollständig self-hosted – volle Datenkontrolle beim Betreiber

---

## Roadmap (Auszug)

- [ ] Basis-Dashboard
- [ ] Spieler-Zeitverlauf-Charts
- [ ] Prozent- & Trend-Berechnung
- [ ] CSV-Import
- [ ] Rollen & Rechte
- [ ] Export (PNG / CSV)
- [ ] Discord-Integration (optional)

---

## Contributing

Contributions sind ausdrücklich willkommen:

- Feature-Vorschläge
- Bug-Reports
- UI-Verbesserungen
- Dokumentation

Bitte nutze [Issues](https://github.com/RedConcrete/sf-guild-analytics/issues) & Pull Requests.

---

## Lizenz

Dieses Projekt steht unter der **MIT-Lizenz** – siehe [LICENSE](LICENSE).

---

## Community

Dieses Projekt ist von der SF-Community für die SF-Community gedacht.
Feedback, Ideen und Diskussionen sind ausdrücklich erwünscht.
