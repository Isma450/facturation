## **Vue d'ensemble du Dockerfile**

Votre Dockerfile est divisé en **2 stages** (étapes) :

1. **Stage 1 (builder)** : Construction de l'application
2. **Stage 2 (runtime)** : Exécution de l'application

---

## **Stage 1 : Construction (builder)**

```dockerfile
# Stage de build avec Maven et JDK complet
FROM maven:3.9.4-openjdk-21 AS builder
```

- **FROM** : Utilise l'image officielle Maven avec OpenJDK 21
- **AS builder** : Donne le nom "builder" à ce stage
- **Pourquoi cette image ?** : Contient Maven + JDK complet nécessaire pour compiler

```dockerfile
# Installer dumb-init pour la gestion des signaux
RUN apk add --no-cache dumb-init
```

- **dumb-init** : Outil qui gère proprement les signaux Unix (SIGTERM, SIGINT)
- **Pourquoi ?** : Java dans Docker ne gère pas bien les signaux d'arrêt sans cet outil

```dockerfile
WORKDIR /app
```

- Définit `/app` comme répertoire de travail dans le conteneur

```dockerfile
# Copier le pom.xml et télécharger les dépendances (optimisation du cache)
COPY pom.xml .
RUN mvn dependency:go-offline
```

- **Optimisation cache Docker** : Copie d'abord seulement le pom.xml
- **mvn dependency:go-offline** : Télécharge toutes les dépendances
- **Avantage** : Si seul le code change (pas les dépendances), cette étape est mise en cache

```dockerfile
# Copier le code source et construire l'application
COPY src ./src
RUN mvn clean package -DskipTests
```

- Copie le code source Java
- **mvn clean package** : Compile et crée le fichier JAR
- **-DskipTests** : Ignore les tests pour accélérer le build

---

## **Stage 2 : Runtime (production)**

```dockerfile
# Image légère pour la production avec seulement le JRE
FROM openjdk:21-jre-alpine
```

- **Image Alpine** : Distribution Linux ultra-légère (~5MB vs ~200MB)
- **JRE seulement** : Pas besoin du JDK complet pour exécuter
- **Résultat** : Image finale beaucoup plus petite

```dockerfile
# Copier dumb-init depuis le stage builder
COPY --from=builder /usr/bin/dumb-init /usr/bin/dumb-init
```

- Récupère dumb-init depuis le stage précédent
- **--from=builder** : Référence le stage "builder"

```dockerfile
# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
```

- **Sécurité** : Crée un utilisateur non-privilégié
- **Pourquoi ?** : Évite d'exécuter Java en tant que root (risque de sécurité)

```dockerfile
# Créer les répertoires nécessaires
RUN mkdir -p /app /app/pdf && \
    chown -R appuser:appgroup /app
```

- Crée les répertoires pour l'application et les PDFs
- **chown** : Donne la propriété à l'utilisateur `appuser`

```dockerfile
# Changer vers l'utilisateur non-root
USER appuser
WORKDIR /app
```

- Bascule vers l'utilisateur non-privilégié
- Définit le répertoire de travail

```dockerfile
# Copier le JAR depuis le stage de build
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar
```

- **--from=builder** : Copie le JAR construit dans le stage 1
- **--chown** : Assure les bonnes permissions
- **\*.jar** : Prend n'importe quel fichier JAR dans target/

```dockerfile
# Exposer le port
EXPOSE 8080
```

- **Documentation** : Indique que l'application écoute sur le port 8080
- **Note** : N'ouvre pas réellement le port, c'est fait dans docker-compose.yml

```dockerfile
# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=docker
```

- **JAVA_OPTS** : Configure la mémoire Java (512MB max, 256MB initial)
- **SPRING_PROFILES_ACTIVE** : Active le profil "docker" de Spring Boot

```dockerfile
# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
```

- **Health check** : Vérifie que l'application répond
- **--interval=30s** : Vérifie toutes les 30 secondes
- **--start-period=40s** : Attend 40s avant les premiers checks
- **--retries=3** : 3 tentatives avant de marquer comme unhealthy
- **wget --spider** : Fait une requête HTTP sans télécharger le contenu

```dockerfile
# Point d'entrée avec dumb-init pour gestion des signaux
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

- **ENTRYPOINT** : Utilise dumb-init pour gérer les signaux
- **CMD** : Commande finale pour lancer Java avec le JAR
- **sh -c** : Permet l'expansion des variables d'environnement

---

## **Avantages de cette approche :**

### **1. Image optimisée :**

- **Stage 1** : ~800MB (Maven + JDK + dépendances)
- **Stage 2** : ~200MB (JRE Alpine + JAR)
- **Résultat** : 75% de réduction de taille

### **2. Sécurité :**

- Utilisateur non-root
- Image Alpine (moins de vulnérabilités)
- Gestion propre des signaux

### **3. Performance :**

- Cache Docker optimisé
- Health checks automatiques
- Mémoire Java configurée

### **4. Production-ready :**

- Logs appropriés
- Arrêt propre avec dumb-init
- Configuration externalisée

docker volume ls
docker compose logs
docker network ls
docker inspect
