# Deployment Guide — SF Guild Analytics

Diese Anleitung beschreibt das vollständige Setup für einen produktionsreifen,
öffentlich erreichbaren VPS-Server mit HTTPS, Firewall und Docker.

---

## Inhaltsverzeichnis

1. [Architektur-Übersicht](#1-architektur-übersicht)
2. [Voraussetzungen](#2-voraussetzungen)
3. [VPS kaufen & Domain einrichten](#3-vps-kaufen--domain-einrichten)
4. [VPS absichern](#4-vps-absichern)
   - [SSH-Key einrichten](#41-ssh-key-einrichten)
   - [Deploy-User anlegen](#42-deploy-user-anlegen)
   - [SSH-Passwort-Login deaktivieren](#43-ssh-passwort-login-deaktivieren)
5. [Firewall einrichten (UFW)](#5-firewall-einrichten-ufw)
6. [Docker installieren](#6-docker-installieren)
7. [Projekt auf den VPS deployen](#7-projekt-auf-den-vps-deployen)
8. [Umgebungsvariablen konfigurieren](#8-umgebungsvariablen-konfigurieren)
9. [Domain in nginx eintragen](#9-domain-in-nginx-eintragen)
10. [SSL-Zertifikat ausstellen (Let's Encrypt)](#10-ssl-zertifikat-ausstellen-lets-encrypt)
11. [Anwendung starten](#11-anwendung-starten)
12. [Automatische Zertifikatserneuerung](#12-automatische-zertifikatserneuerung)
13. [Updates deployen](#13-updates-deployen)
14. [Logs & Monitoring](#14-logs--monitoring)
15. [Troubleshooting](#15-troubleshooting)
16. [Sicherheits-Checkliste](#16-sicherheits-checkliste)

---

## 1. Architektur-Übersicht

```
Internet (Port 80 / 443)
          │
    ┌─────▼──────────────────────────────────┐
    │  nginx  (SSL, Rate-Limiting, Headers)  │  ← Einziger öffentlicher Einstiegspunkt
    └─────┬─────────────────┬───────────────┘
          │                 │
     /api/*            alles andere
     /uploads/*             │
          │                 ▼
    ┌─────▼──────┐   ┌──────────────┐
    │  Backend   │   │   Frontend   │   ← Vue.js als statische Dateien
    │ Spring Boot│   │   (nginx)    │
    └─────┬──────┘   └──────────────┘
          │
    ┌─────▼──────┐
    │  Postgres  │   ← Nur intern erreichbar
    └────────────┘
```

**Wichtig:** Datenbank und Backend haben kein `ports:`-Mapping im Production-Compose.
Sie sind ausschließlich über das interne Docker-Netzwerk erreichbar — nie direkt vom Internet.

---

## 2. Voraussetzungen

| Was | Beschreibung |
|-----|-------------|
| VPS | Linux-Server mit Ubuntu 24.04 LTS (mind. 1 vCPU, 2 GB RAM, 20 GB SSD) |
| Domain | Eine eigene Domain, z.B. `meine-gilde.de` |
| SSH-Client | Terminal mit SSH (Linux/macOS: eingebaut, Windows: PowerShell / PuTTY) |
| Git | Auf dem VPS installiert (`apt install git`) |

---

## 3. VPS kaufen & Domain einrichten

### Empfohlene VPS-Anbieter

| Anbieter | Einstiegsplan | Besonderheit |
|----------|--------------|--------------|
| [Hetzner Cloud](https://www.hetzner.com/cloud) | ~4 €/Monat | Deutscher Anbieter, sehr günstig |
| [DigitalOcean](https://www.digitalocean.com) | ~6 $/Monat | Einfache Oberfläche |
| [Netcup](https://www.netcup.de) | ~3 €/Monat | Günstig, DE-Datacenter |

### Domain auf VPS zeigen lassen

Nach dem VPS-Kauf die öffentliche IP-Adresse notieren (z.B. `123.456.789.0`).
Beim Domain-Anbieter einen **A-Record** setzen:

```
Typ     Name    Wert
A       @       123.456.789.0
A       www     123.456.789.0
```

> DNS-Änderungen brauchen bis zu 24 Stunden — in der Praxis meist unter 30 Minuten.
> Prüfen mit: `nslookup meine-domain.de`

---

## 4. VPS absichern

### 4.1 SSH-Key einrichten

**Auf dem lokalen PC** (nicht auf dem VPS):

```bash
# SSH-Key generieren (Ed25519 ist sicherer als RSA)
ssh-keygen -t ed25519 -C "sf-guild-deploy"
# Speicherort bestätigen: ~/.ssh/id_ed25519
# Passphrase setzen (empfohlen)
```

Den Public Key beim VPS-Anbieter hinterlegen:
- **Hetzner:** Beim Server-Erstellen unter "SSH-Keys" eintragen
- **Alternativ nach Erstellung:**
  ```bash
  ssh-copy-id -i ~/.ssh/id_ed25519.pub root@EURE_VPS_IP
  ```

Erster Login testen:
```bash
ssh root@EURE_VPS_IP
```

### 4.2 Deploy-User anlegen

Als `root` auf dem VPS:

```bash
# Neuen Benutzer anlegen (niemals dauerhaft als root arbeiten)
adduser deploy
# Passwort setzen wenn gefragt

# Sudo-Rechte geben
usermod -aG sudo deploy

# SSH-Key vom root-User übernehmen
rsync --archive --chown=deploy:deploy ~/.ssh /home/deploy/
```

Ab jetzt immer als `deploy` einloggen:
```bash
ssh deploy@EURE_VPS_IP
```

### 4.3 SSH-Passwort-Login deaktivieren

```bash
sudo nano /etc/ssh/sshd_config
```

Diese Werte setzen (vorhandene Zeilen ändern, nicht doppelt eintragen):
```
PasswordAuthentication no
PubkeyAuthentication yes
PermitRootLogin no
```

```bash
# SSH-Dienst neu starten
sudo systemctl restart sshd
```

> **Achtung:** Erst prüfen, dass der Login mit Key funktioniert — erst dann Passwort-Login deaktivieren.
> Sonst sperrt man sich aus.

---

## 5. Firewall einrichten (UFW)

```bash
# Alles blockieren, nur benötigte Ports öffnen
sudo ufw default deny incoming
sudo ufw default allow outgoing

# SSH — unbedingt vor dem Aktivieren freigeben!
sudo ufw allow 22/tcp

# HTTP und HTTPS für die Webanwendung
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Firewall aktivieren
sudo ufw enable

# Status prüfen
sudo ufw status
```

Erwartete Ausgabe:
```
Status: active

To                         Action      From
--                         ------      ----
22/tcp                     ALLOW       Anywhere
80/tcp                     ALLOW       Anywhere
443/tcp                    ALLOW       Anywhere
```

> Ports 5433 (Postgres) und 41414 (Backend) werden **nicht** geöffnet.
> Diese sind nur intern über das Docker-Netzwerk erreichbar.

---

## 6. Docker installieren

```bash
# Offizielle Docker-Installationsanleitung für Ubuntu
curl -fsSL https://get.docker.com | sh

# Deploy-User zur Docker-Gruppe hinzufügen
sudo usermod -aG docker deploy

# Neu einloggen, damit die Gruppe wirksam wird
exit
ssh deploy@EURE_VPS_IP

# Installation prüfen
docker --version
docker compose version
```

---

## 7. Projekt auf den VPS deployen

```bash
# Als deploy-User auf dem VPS
git clone https://github.com/RedConcrete/sf-guild-analytics.git
cd sf-guild-analytics
```

---

## 8. Umgebungsvariablen konfigurieren

```bash
# Template kopieren
cp .env.example .env

# Datei bearbeiten
nano .env
```

Starke Passwörter und Secrets direkt auf dem VPS generieren:
```bash
# Datenbankpasswort generieren
openssl rand -base64 32

# JWT-Secret generieren (mind. 64 Zeichen)
openssl rand -base64 64
```

Die `.env`-Datei ausfüllen:
```env
DB_NAME=sf_guild_analytics
DB_USER=sfguild
DB_PASSWORD=<Ausgabe von openssl rand -base64 32>
JWT_SECRET=<Ausgabe von openssl rand -base64 64>
DOMAIN=meine-domain.de
```

> Die `.env`-Datei ist in `.gitignore` eingetragen und wird nie ins Repository committed.

---

## 9. Domain in nginx eintragen

`YOUR_DOMAIN` in der nginx-Konfiguration durch die echte Domain ersetzen:

```bash
# Alle Vorkommen von YOUR_DOMAIN ersetzen
sed -i 's/YOUR_DOMAIN/meine-domain.de/g' nginx/nginx.conf

# Prüfen ob alle ersetzt wurden
grep "YOUR_DOMAIN" nginx/nginx.conf
# → Keine Ausgabe = alles ersetzt
```

---

## 10. SSL-Zertifikat ausstellen (Let's Encrypt)

Das Zertifikat muss **vor** dem ersten vollständigen Start ausgestellt werden.
Dazu muss nginx zunächst nur auf Port 80 erreichbar sein.

### Schritt 1: nginx temporär nur auf HTTP starten

Den HTTPS-Server-Block in `nginx/nginx.conf` vorübergehend auskommentieren
(alles ab `# ── HTTPS Hauptserver`):

```bash
nano nginx/nginx.conf
# Den zweiten server { ... }-Block mit # auskommentieren
```

### Schritt 2: Certbot-Verzeichnisse anlegen

```bash
mkdir -p certbot/conf certbot/www
```

### Schritt 3: nginx starten (nur HTTP)

```bash
docker compose -f docker-compose.prod.yml up -d nginx
```

### Schritt 4: Zertifikat ausstellen

```bash
docker compose -f docker-compose.prod.yml --profile certbot run --rm certbot \
  certonly --webroot \
  --webroot-path=/var/www/certbot \
  --email eure@email.com \
  --agree-tos \
  --no-eff-email \
  -d meine-domain.de \
  -d www.meine-domain.de
```

Erfolgsmeldung:
```
Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/meine-domain.de/fullchain.pem
```

### Schritt 5: HTTPS-Block wieder aktivieren

```bash
nano nginx/nginx.conf
# Auskommentierte Zeilen wieder einkommentieren
```

---

## 11. Anwendung starten

```bash
cd sf-guild-analytics

# Alle Container bauen und starten
docker compose -f docker-compose.prod.yml up -d --build

# Status prüfen (alle sollten "Up" zeigen)
docker compose -f docker-compose.prod.yml ps
```

Erwartete Ausgabe:
```
NAME                      STATUS          PORTS
sf_guild_nginx            Up              0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp
sf_guild_frontend         Up
sf_guild_backend          Up
sf_guild_db               Up (healthy)
```

Anwendung im Browser öffnen: `https://meine-domain.de`

Standard-Login (sofort ändern!):
- Benutzername: `admin`
- Passwort: `admin123`

---

## 12. Automatische Zertifikatserneuerung

Let's Encrypt-Zertifikate laufen nach 90 Tagen ab. Automatische Erneuerung einrichten:

```bash
# Crontab bearbeiten
crontab -e
```

Folgende Zeile einfügen (jeden Montag um 3 Uhr morgens):
```
0 3 * * 1 cd /home/deploy/sf-guild-analytics && docker compose -f docker-compose.prod.yml --profile certbot run --rm certbot renew --quiet && docker compose -f docker-compose.prod.yml restart nginx >> /home/deploy/certbot-renew.log 2>&1
```

Manuelle Erneuerung (zum Testen):
```bash
docker compose -f docker-compose.prod.yml --profile certbot run --rm certbot renew --dry-run
```

---

## 13. Updates deployen

```bash
cd /home/deploy/sf-guild-analytics

# Neuesten Code holen
git pull

# Container neu bauen und starten (kurze Downtime ~30 Sekunden)
docker compose -f docker-compose.prod.yml up -d --build

# Alte, ungenutzte Images aufräumen
docker image prune -f
```

---

## 14. Logs & Monitoring

```bash
# Alle Container-Logs live verfolgen
docker compose -f docker-compose.prod.yml logs -f

# Nur Backend-Logs
docker compose -f docker-compose.prod.yml logs -f backend

# Nur nginx-Logs
docker compose -f docker-compose.prod.yml logs -f nginx

# Letzte 100 Zeilen eines Services
docker compose -f docker-compose.prod.yml logs --tail=100 backend

# Ressourcenverbrauch der Container
docker stats
```

---

## 15. Troubleshooting

### Container startet nicht

```bash
# Detaillierte Fehlermeldung anzeigen
docker compose -f docker-compose.prod.yml logs backend
docker compose -f docker-compose.prod.yml logs nginx
```

### nginx zeigt "502 Bad Gateway"

Backend ist noch nicht bereit. Kurz warten und prüfen:
```bash
docker compose -f docker-compose.prod.yml ps
# backend sollte "(healthy)" zeigen
```

### SSL-Zertifikat-Fehler

```bash
# Zertifikat-Status prüfen
docker compose -f docker-compose.prod.yml --profile certbot run --rm certbot certificates

# Zertifikat manuell erneuern
docker compose -f docker-compose.prod.yml --profile certbot run --rm certbot renew
docker compose -f docker-compose.prod.yml restart nginx
```

### Datenbank-Verbindungsfehler

```bash
# Prüfen ob DB läuft und healthy ist
docker compose -f docker-compose.prod.yml ps db

# Direkt in die Datenbank schauen
docker exec -it sf_guild_db psql -U sfguild -d sf_guild_analytics
```

### Alles neu starten

```bash
docker compose -f docker-compose.prod.yml down
docker compose -f docker-compose.prod.yml up -d --build
```

### Komplettes Reset (Vorsicht: löscht alle Daten!)

```bash
docker compose -f docker-compose.prod.yml down -v
docker compose -f docker-compose.prod.yml up -d --build
```

---

## 16. Sicherheits-Checkliste

Vor dem ersten Go-Live abhaken:

- [ ] SSH-Passwort-Login deaktiviert (`PasswordAuthentication no`)
- [ ] Root-Login deaktiviert (`PermitRootLogin no`)
- [ ] UFW-Firewall aktiv (nur Ports 22, 80, 443 offen)
- [ ] `.env` befüllt mit starken, zufällig generierten Passwörtern
- [ ] `.env` ist **nicht** im Git-Repository (`git status` prüfen)
- [ ] `JWT_SECRET` ist mind. 64 Zeichen lang und zufällig generiert
- [ ] `DB_PASSWORD` ist stark und zufällig generiert
- [ ] HTTPS funktioniert (`https://meine-domain.de` im Browser)
- [ ] HTTP leitet auf HTTPS um (`http://meine-domain.de` → Redirect)
- [ ] Standard-Admin-Passwort `admin123` geändert
- [ ] Zertifikat-Erneuerung per Cron eingerichtet

---

## Dateiübersicht

```
sf-guild-analytics/
├── backend/                    Spring Boot Backend
├── frontend/                   Vue.js Frontend
│   ├── Dockerfile              Produktions-Build (Multi-Stage)
│   └── nginx-frontend.conf     nginx-Konfiguration für SPA-Routing
├── nginx/
│   └── nginx.conf              Reverse Proxy, SSL, Rate-Limiting
├── certbot/                    Let's Encrypt (wird beim ersten Start erstellt)
│   ├── conf/                   Zertifikate (nicht committen)
│   └── www/                    ACME-Challenge
├── docker-compose.yml          Entwicklungsumgebung
├── docker-compose.prod.yml     Produktionsumgebung
├── .env.example                Vorlage für Umgebungsvariablen
├── .env                        Echte Secrets (niemals committen, in .gitignore)
├── .gitignore                  Schützt .env und certbot/ vor versehentlichem Commit
├── README.md                   Projektbeschreibung
└── DEPLOYMENT.md               Diese Datei
```
