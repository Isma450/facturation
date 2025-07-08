# Questions pour la soutenance du projet Facturation

## 1. Questions Docker & Conteneurs

### **"Que faites-vous si un conteneur ne démarre pas ?"**

- Vérifier les logs : `docker compose logs nom-du-service`
- Vérifier le statut : `docker compose ps`
- Vérifier les health checks : `docker inspect nom-conteneur`
- Vérifier les ports disponibles : `netstat -tulpn | grep 8080`
- Reconstruire l'image : `docker compose build --no-cache`

### **"Comment diagnostiquez-vous un problème de connexion entre conteneurs ?"**

- Vérifier le réseau Docker : `docker network ls`
- Tester la connectivité : `docker exec -it facturation-spring ping postgres-db`
- Vérifier les variables d'environnement : `docker exec -it facturation-spring env`
- Contrôler la configuration dans docker-compose.yml

### **"Que faire si PostgreSQL refuse les connexions ?"**

- Vérifier que PostgreSQL est démarré : `docker compose logs postgres-db`
- Vérifier les credentials dans le fichier .env
- Tester la connexion directe : `docker exec -it postgres-db psql -U facturation_user -d facturation_db`
- Vérifier les health checks PostgreSQL

## 2. Questions Configuration & Environnement

### **"Comment gérez-vous les différents environnements (dev/prod) ?"**

- Profils Spring Boot : `application.properties` vs `application-docker.properties`
- Variables d'environnement via fichier .env
- Docker Compose overrides : `docker-compose.override.yml`
- Configuration spécifique par environnement

### **"Que faire si les variables d'environnement ne sont pas prises en compte ?"**

- Vérifier la syntaxe dans le fichier .env (pas d'espaces autour du =)
- Vérifier que le fichier .env est au bon endroit
- Redémarrer les conteneurs : `docker compose down && docker compose up`
- Vérifier dans les logs que les variables sont correctes

### **"Comment résolvez-vous les problèmes de persistance des données ?"**

- Vérifier les volumes : `docker volume ls`
- Vérifier les permissions de fichiers dans les conteneurs
- Nettoyer les volumes si nécessaire : `docker compose down -v`
- Vérifier les chemins de montage dans docker-compose.yml

## 3. Questions Application Spring Boot

### **"Que faire si l'application Spring Boot ne démarre pas ?"**

- Vérifier les logs d'erreur : `docker compose logs facturation-app`
- Vérifier la configuration de la base de données
- Vérifier que les tables sont créées : `spring.jpa.hibernate.ddl-auto=update`
- Vérifier l'ordre de démarrage avec `depends_on`

### **"Comment debuggez-vous les erreurs SQL ?"**

- Activer les logs SQL : `spring.jpa.show-sql=true`
- Vérifier les données dans data.sql
- Vérifier l'ordre d'exécution avec `spring.jpa.defer-datasource-initialization=true`
- Tester les requêtes directement dans pgAdmin

### **"Que faire si la génération de PDF échoue ?"**

- Vérifier les permissions du répertoire /app/pdf
- Vérifier les logs d'erreur de l'application
- Vérifier que le volume pdf_storage est monté
- Tester avec un PDF simple pour isoler le problème

## 4. Questions Performance & Optimisation

### **"Comment optimisez-vous les temps de démarrage ?"**

- Multi-stage builds pour des images plus légères
- Cache Maven pour éviter de retélécharger les dépendances
- Health checks avec des délais adaptés
- Réglage des paramètres JVM : `JAVA_OPTS="-Xmx512m -Xms256m"`

### **"Que faire si l'application consomme trop de mémoire ?"**

- Ajuster les paramètres JVM dans JAVA_OPTS
- Monitorer avec `docker stats`
- Vérifier les fuites mémoire dans les logs
- Limiter les ressources dans docker-compose.yml

### **"Comment gérez-vous les logs en production ?"**

- Centralisation avec `docker compose logs`
- Rotation des logs avec logrotate
- Monitoring avec des outils comme ELK Stack
- Niveaux de log configurables via Spring Boot

## 5. Questions Sécurité

### **"Comment sécurisez-vous les mots de passe ?"**

- Utilisation de variables d'environnement
- Fichier .env non versionné (dans .gitignore)
- Docker secrets en production
- Chiffrement des mots de passe dans la base

### **"Que faire si un port est déjà utilisé ?"**

- Identifier le processus : `lsof -i :8080`
- Arrêter le processus ou changer le port
- Modifier les ports dans docker-compose.yml
- Vérifier les conflits avec d'autres applications

## 6. Questions Déploiement & Maintenance

### **"Comment mettez-vous à jour l'application ?"**

- Arrêt propre : `docker compose down`
- Mise à jour du code source
- Reconstruction : `docker compose build --no-cache`
- Redémarrage : `docker compose up -d`

### **"Comment sauvegardez-vous les données ?"**

- Backup du volume PostgreSQL
- Export SQL : `docker exec postgres-db pg_dump -U user db > backup.sql`
- Sauvegarde des PDFs du volume pdf_storage
- Stratégie de backup automatisée

### **"Que faire en cas de corruption de base de données ?"**

- Vérifier les logs PostgreSQL
- Restaurer depuis un backup
- Recréer la base si nécessaire
- Vérifier l'intégrité des données avec des requêtes SQL

## 7. Questions Monitoring & Debugging

### **"Comment monitorer la santé de l'application ?"**

- Utilisation des health checks Docker
- Endpoint Spring Boot Actuator `/actuator/health`
- Monitoring des métriques système
- Alertes automatisées

### **"Comment debuggez-vous un problème de performance ?"**

- Profiling avec des outils Java (JProfiler, VisualVM)
- Monitoring des requêtes SQL lentes
- Analyse des logs d'accès
- Métriques Docker : `docker stats`
