# Stage 1: Build de l'application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Télécharger les dépendances (mise en cache Docker)
RUN mvn dependency:resolve

# Copier le code source
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime optimisé
FROM eclipse-temurin:21-jre-alpine AS runtime

# Installer dumb-init pour gestion propre des signaux
RUN apk add --no-cache dumb-init

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Créer les répertoires nécessaires
RUN mkdir -p /app /app/pdf && \
    chown -R appuser:appgroup /app

# Changer vers l'utilisateur non-root
USER appuser

WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Point d'entrée avec dumb-init pour gestion des signaux
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
