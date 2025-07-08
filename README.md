# Application de Facturation Dockerisée

> Application web de gestion de facturation développée en Spring Boot avec PostgreSQL, entièrement dockerisée pour une mise en œuvre simplifiée.

## Déma## Sécurité

### Mesures de sécurité implémentées :

- Utilisateur non-root dans les conteneurs
- Images Al## Résolution de Problèmesine (surface d'attaque réduite)
- Secrets via variables d'environnement
- Réseau isolé pour les services
- Health checks avec timeout
- Gestion propre des signaux (dumb-init)de

### Linux / macOS

Pour lancer l'application en une seule commande :

```bash
./start-demo.sh
```

### Windows

**PowerShell (Recommandé)** :

```powershell
.\start-demo.ps1
```

**Command Prompt** :

```cmd
start-demo.bat
```

### Manuel (tous OS)

```bash
docker-compose up --build
```

L'application sera accessible sur : **http://localhost:8080**

## Prérequis

### Linux / macOS

- Docker Engine 20.10+
- Docker Compose V2
- 2 GB de RAM libre
- Ports 8080 et 5432 disponibles

### Windows

- **Docker Desktop pour Windows** ([Télécharger](https://www.docker.com/products/docker-desktop))
- **Windows 10/11** avec WSL2 activé
- **PowerShell 5.0+** (inclus dans Windows 10/11)
- 2 GB de RAM libre
- Ports 8080 et 5432 disponibles

### Configuration Docker Desktop Windows

1. Installer Docker Desktop
2. Activer WSL2 integration
3. S'assurer que Docker est démarré (icône dans la barre des tâches)

## Configuration Environnement

### Fichier .env requis

Avant de lancer l'application, vous devez créer un fichier `.env` à la racine du projet si ce n'est pas déjà fait.

Le fichier `.env` doit contenir les variables suivantes :

```env
# Base de données PostgreSQL
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD

# Configuration Spring Boot
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME

# Configuration JPA
SPRING_JPA_HIBERNATE_DDL_AUTO
SPRING_JPA_SHOW_SQL
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL

# Configuration application
SPRING_THYMELEAF_CACHE
SPRING_WEB_LOCALE
SPRING_WEB_LOCALE_RESOLVER
SERVER_PORT

# Profil Spring actif
SPRING_PROFILES_ACTIVE

# Options JVM
JAVA_OPTS
```

Note : Le fichier `.env` est dans le `.gitignore` pour des raisons de sécurité.

## Architecture Dockerisée

### Services

| Service           | Description                   | Port | Image                  |
| ----------------- | ----------------------------- | ---- | ---------------------- |
| `facturation-app` | Application Spring Boot       | 8080 | Custom (multi-stage)   |
| `postgres-db`     | Base de données PostgreSQL    | 5432 | postgres:15-alpine     |
| `pgadmin`         | Interface d'administration DB | 5050 | dpage/pgadmin4 (debug) |

### Réseaux

- **facturation-network** : Réseau bridge isolé pour la communication inter-services

### Volumes

- **postgres_data** : Persistance des données PostgreSQL
- **pdf_storage** : Stockage des factures PDF générées
- **pgadmin_data** : Configuration PgAdmin

## Configuration Multi-Environnement

### Développement

```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

### Production

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up
```

## Fonctionnalités de l'Application

### Pages Disponibles

- **Authentification** : `/login`, `/register`
- **Tableau de bord** : `/`
- **Gestion entreprises** : `/entreprises`
- **Facturation** : `/facturation`
- **Bilans** : `/bilan`
- **Prestations** : `/prestation`

### Base de Données

- **PostgreSQL 15** avec données de test automatiquement chargées
- **Utilisateur** : `facturation_user`
- **Base** : `financial_db`
- **Fixtures** : Chargées via `fixtures_new.sql`

## Optimisations Docker

### Dockerfile Multi-Stage

- **Stage 1** : Build avec Maven (optimisation cache)
- **Stage 2** : Runtime avec JRE Alpine (image minimale)
- **Sécurité** : Utilisateur non-root, dumb-init
- **Taille** : ~200MB (vs 800MB+ avec JDK complet)

### Health Checks

```yaml
# Application
healthcheck:
  test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3

# PostgreSQL
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U facturation_user"]
  interval: 10s
  timeout: 5s
  retries: 5
```

## Sécurité

### Mesures Implémentées

- Utilisateur non-root dans les conteneurs
- Images Alpine (surface d'attaque réduite)
- Secrets via variables d'environnement
- Réseau isolé pour les services
- Health checks avec timeout
- Gestion propre des signaux (dumb-init)

### Recommandations Production

```bash
# Scanner les vulnérabilités
docker scout cves facturation-app:latest

# Rotation des secrets
docker secret create db_password_v2 password.txt
```

## Monitoring & Debugging

### Logs

```bash
# Tous les services
docker-compose logs -f

# Service spécifique
docker-compose logs -f facturation-app

# Avec timestamps
docker-compose logs -f -t
```

### Health Monitoring

```bash
# Status des services
docker-compose ps

# Health check manuel
curl http://localhost:8080/actuator/health

# Métriques système
docker stats
```

### Debug Mode

Le mode développement active :

- Port debug Java (5005)
- Logs détaillés
- PgAdmin automatiquement lancé
- Hot reload activé

## Partage Multi-Plateforme

### Distribution de l'Application

Pour partager votre application avec des utilisateurs Windows :

1. **Partager tout le dossier** contenant :

   - Les fichiers Docker (`Dockerfile`, `docker-compose.yml`)
   - Les scripts de démarrage (`start-demo.sh`, `start-demo.ps1`, `start-demo.bat`)
   - Le code source complet

2. **Instructions pour l'utilisateur Windows** :

   ```powershell
   # 1. Installer Docker Desktop
   # 2. Extraire le dossier de l'application
   # 3. Ouvrir PowerShell dans le dossier
   # 4. Exécuter :
   .\start-demo.ps1
   ```

3. **Alternative : Archive ZIP** prête à l'emploi avec :
   - README avec instructions Windows
   - Scripts de démarrage pour tous les OS
   - Tous les fichiers nécessaires

### Distribution via Git

```bash
# L'utilisateur Windows clone le repo
git clone <votre-repo>
cd facturation

# Et exécute selon son OS :
.\start-demo.ps1    # PowerShell
# ou
start-demo.bat      # Command Prompt
```

## Résolution de Problèmes

### Problèmes Courants

**Port déjà utilisé**

```bash
# Vérifier les ports
lsof -i :8080
# Ou modifier le port dans docker-compose.yml
```

**Conteneur qui ne démarre pas**

```bash
# Vérifier les logs
docker-compose logs facturation-app

# Forcer la reconstruction
docker-compose up --build --force-recreate
```

**Base de données inaccessible**

```bash
# Vérifier la santé de PostgreSQL
docker-compose exec postgres-db pg_isready -U facturation_user

# Recréer le volume
docker-compose down -v
docker-compose up
```

### Commandes de Maintenance

```bash
# Nettoyage complet
docker-compose down --volumes --remove-orphans
docker system prune -f

# Sauvegarde de la DB
docker-compose exec postgres-db pg_dump -U facturation_user financial_db > backup.sql

# Restauration
docker-compose exec -T postgres-db psql -U facturation_user financial_db < backup.sql
```

## Structure du Projet

```
facturation/
├── Docker
│   ├── Dockerfile                  # Build multi-stage optimisé
│   ├── .dockerignore              # Exclusions Docker
│   ├── docker-compose.yml         # Orchestration principale
│   ├── docker-compose.dev.yml     # Override développement
│   └── docker-compose.prod.yml    # Override production
├── Scripts
│   ├── start-demo.sh              # Démarrage automatisé (Linux/macOS)
│   ├── start-demo.ps1             # Démarrage automatisé (Windows PowerShell)
│   └── start-demo.bat             # Démarrage automatisé (Windows CMD)
├── Application
│   ├── src/                       # Code source Spring Boot
│   ├── pom.xml                    # Dépendances Maven
│   └── fixtures_new.sql           # Données de test
└── Documentation
    └── README.md                  # Ce fichier
```

## Équipe

- **Développement** : ismail450
- **Dockerisation** : Ismail450
- **Documentation** : Ismail450

## Licence

Projet d'évaluation - Formation Conteneurisation & Orchestration
