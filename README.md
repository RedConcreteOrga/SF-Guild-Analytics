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

### Entwicklung (lokal)

```
Browser (Vue 3, Port 5173)
        │
   Vite Dev Server
        │  HTTP
   REST API (Port 41414)
        │
  Spring Boot Backend
        │
   PostgreSQL (Port 5433)
```

### Produktion (VPS)

```
Internet (Port 80 / 443)
        │
   nginx (SSL, Rate-Limiting, Security-Headers)
        │
   ┌────┴─────────────────┐
   │                      │
 /api/*               alles andere
 /uploads/*               │
   │                      │
Spring Boot          Vue 3 (statische
Backend              Dateien via nginx)
   │
PostgreSQL
(nur intern erreichbar)
```

In der Produktion ist ausschließlich nginx öffentlich erreichbar.
Backend und Datenbank kommunizieren nur über das interne Docker-Netzwerk.

---

## Tech-Stack

| Schicht | Technologie |
|---------|-------------|
| Frontend | Vue 3, Vite, TailwindCSS, Pinia, Vue Router, Axios |
| Backend | Java 17, Spring Boot 3.2, Spring Security, JPA/Hibernate |
| Authentifizierung | JWT (JJWT 0.11), BCrypt (Stärke 12) |
| Validierung | Bean Validation (jakarta.validation) |
| Datenbank | PostgreSQL 15 |
| Proxy / SSL | nginx, Let's Encrypt (Certbot) |
| Logging | Logback (profil-basiert, kein Passwort-Logging) |
| Deployment | Docker & Docker Compose |

---

## Sicherheit

Die Anwendung implementiert folgende Sicherheitsmaßnahmen:

| Maßnahme | Umsetzung |
|----------|-----------|
| Passwort-Hashing | BCrypt mit Stärke 12 |
| Transport-Verschlüsselung | HTTPS via Let's Encrypt (Produktion) |
| Authentifizierung | JWT, stateless, 24h Ablaufzeit |
| Brute-Force-Schutz | nginx Rate-Limiting (5 Login-Versuche/min pro IP) |
| Security-Headers | HSTS, X-Frame-Options, CSP, Referrer-Policy |
| Input-Validierung | Bean Validation auf allen DTOs |
| Kein Passwort-Logging | `toString()` maskiert, Logback konfiguriert |
| Secrets | Umgebungsvariablen, nie im Code oder Repository |
| Netzwerk-Isolation | DB und Backend ohne öffentliche Ports (Produktion) |

---

## Projektstruktur

```
sf-guild-analytics/
├── backend/
│   ├── src/main/java/com/sf/guildanalytics/
│   │   ├── config/
│   │   │   ├── DataInitializer.java      Standard-Admin beim ersten Start
│   │   │   └── WebConfig.java            Upload-Konfiguration
│   │   ├── controller/
│   │   │   ├── AuthController.java       Login-Endpoint
│   │   │   ├── GuildController.java
│   │   │   ├── PlayerController.java
│   │   │   ├── SnapshotController.java
│   │   │   ├── UploadController.java
│   │   │   └── UserController.java
│   │   ├── dto/
│   │   │   ├── LoginRequest.java         Validiert, toString() maskiert Passwort
│   │   │   ├── JwtResponse.java
│   │   │   └── UserDTO.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   ├── Guild.java
│   │   │   ├── Player.java
│   │   │   └── PlayerSnapshot.java
│   │   ├── security/
│   │   │   ├── SecurityConfig.java       BCrypt, JWT-Filter, CORS, Security-Headers
│   │   │   ├── JwtUtils.java             Token-Generierung & Validierung
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   └── service/
│   │       ├── UserService.java          Passwort-Encoding beim Anlegen
│   │       ├── GuildService.java
│   │       ├── PlayerService.java
│   │       └── SnapshotService.java
│   ├── src/main/resources/
│   │   ├── application.yml               Profil-basierte Konfiguration
│   │   └── logback-spring.xml            Logging (kein Passwort in Logs)
│   └── Dockerfile                        Multi-Stage Build
│
├── frontend/
│   ├── src/
│   │   ├── api/axios.js                  Axios mit JWT-Interceptor
│   │   ├── stores/auth.js                Pinia Auth-Store
│   │   ├── router/index.js               Routen mit Auth-Guards
│   │   └── views/
│   │       ├── LoginView.vue
│   │       └── ...
│   ├── Dockerfile                        Multi-Stage: Build + nginx
│   └── nginx-frontend.conf               SPA-Routing für Vue Router
│
├── nginx/
│   └── nginx.conf                        Reverse Proxy, SSL, Rate-Limiting
│
├── docker-compose.yml                    Entwicklungsumgebung (lokal)
├── docker-compose.prod.yml               Produktionsumgebung (VPS)
├── .env.example                          Vorlage für Umgebungsvariablen
├── .env                                  Secrets (in .gitignore, nie committen)
├── .gitignore                            Schützt .env und Zertifikate
├── README.md                             Diese Datei
└── DEPLOYMENT.md                         VPS-Deployment-Anleitung
```

---

## Deployment (Produktionsserver)

Vollständige Schritt-für-Schritt-Anleitung zum Hosten auf einem VPS mit HTTPS, Firewall und Docker:

**[DEPLOYMENT.md](DEPLOYMENT.md)**

---

## Installation & Start (Lokal / Entwicklung)

### Voraussetzungen

- [Docker & Docker Compose](https://docs.docker.com/get-docker/)
- [Java 17](https://adoptium.net/) + [Maven](https://maven.apache.org/) (nur für Backend-Entwicklung ohne Docker)
- [Node.js 20+](https://nodejs.org/) + npm (nur für Frontend-Entwicklung ohne Docker)

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

Standard-Login beim ersten Start:
- Benutzername: `admin`
- Passwort: `admin123`

> Das Standard-Passwort nach dem ersten Login sofort über die Benutzerverwaltung ändern.

Logs aller Dienste ansehen:
```bash
docker compose logs -f
```

Dienste stoppen:
```bash
docker compose down
```

---

### Option B – Lokale Entwicklung (ohne Docker für Backend/Frontend)

Diese Variante eignet sich, wenn du aktiv am Code entwickelst und Hot-Reload brauchst.

**1. Datenbank starten (Docker)**

```bash
docker compose up -d db
```

Die PostgreSQL-Instanz ist danach unter `localhost:5433` erreichbar.

**2. Backend starten**

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Das Backend startet unter `http://localhost:41414`.
Beim ersten Start legt Hibernate alle Tabellen an und der Standard-Admin-User wird automatisch erstellt.

**3. Frontend starten**

```bash
cd frontend
npm install
npm run dev
```

Das Frontend startet unter `http://localhost:5173`.

---

### Datenbank zurücksetzen

```bash
docker compose down -v   # Löscht auch das Daten-Volume
docker compose up -d db
```

---

## API-Endpunkte (Auszug)

| Methode | Endpunkt | Auth | Beschreibung |
|---------|----------|------|--------------|
| POST | `/api/auth/login` | Nein | Login, gibt JWT zurück |
| GET | `/api/guilds` | Nein | Alle Gilden abrufen |
| GET | `/api/players` | Nein | Alle Spieler abrufen |
| GET | `/api/snapshots` | Nein | Snapshots abrufen |
| GET | `/api/users` | JWT | Benutzerliste (Admin) |
| POST | `/api/users` | JWT | Benutzer anlegen (Admin) |

Beispiel-Login:
```bash
curl -X POST http://localhost:41414/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Antwort:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "...",
    "username": "admin",
    "role": "ADMIN"
  }
}
```

Authentifizierter Request:
```bash
curl http://localhost:41414/api/users \
  -H "Authorization: Bearer <token>"
```

---

## Datenschutz & Fair Use

- Keine automatisierte Datenerfassung aus dem Spiel
- Keine Verbindung zu offiziellen SF-Servern
- Alle Daten werden manuell oder freiwillig eingegeben
- Vollständig self-hosted – volle Datenkontrolle beim Betreiber

---

## Roadmap (Auszug)

- [x] Basis-Authentifizierung (JWT, BCrypt, Rollen)
- [x] Gilden & Spieler verwalten
- [x] Snapshots erfassen
- [x] Datei-Upload
- [x] Produktions-Deployment (Docker, nginx, HTTPS)
- [ ] Spieler-Zeitverlauf-Charts
- [ ] Prozent- & Trend-Berechnung
- [ ] CSV-Import
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
