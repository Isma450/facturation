@echo off
setlocal enabledelayedexpansion

REM Script de démarrage pour Windows Batch
REM Équivalent de start-demo.sh pour les utilisateurs Windows

echo.
echo 🚀 Démarrage de l'application de Facturation Dockerisée
echo ==================================================

REM Vérifier que Docker est installé
echo 🔍 Vérification de Docker...
docker --version >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Docker n'est pas installé ou n'est pas dans le PATH.
    echo    Veuillez installer Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Vérifier que Docker est démarré
docker info >nul 2>&1
if !errorlevel! neq 0 (
    echo ❌ Docker n'est pas démarré. Veuillez lancer Docker Desktop.
    pause
    exit /b 1
)

echo ✅ Docker détecté et accessible

REM Nettoyage des anciens conteneurs (optionnel)
set /p clean="Voulez-vous nettoyer les anciens conteneurs ? (y/N): "
if /i "!clean!"=="y" (
    echo 🧹 Nettoyage des anciens conteneurs...
    docker-compose down --volumes --remove-orphans
)

REM Construction et démarrage
echo 🔨 Construction et démarrage des services...
docker-compose up --build -d
if !errorlevel! neq 0 (
    echo ❌ Erreur lors du démarrage des services.
    echo    Vérifiez les logs avec: docker-compose logs
    pause
    exit /b 1
)

REM Attendre que l'application soit prête
echo ⏳ Attente du démarrage de l'application...
set timeout=120
set counter=0

:wait_loop
if !counter! geq !timeout! (
    echo ❌ Timeout - L'application met trop de temps à démarrer
    echo    Vérifiez les logs avec : docker-compose logs -f
    pause
    exit /b 1
)

REM Tester si l'application répond
curl -f http://localhost:8080/actuator/health >nul 2>&1
if !errorlevel! equ 0 (
    echo ✅ Application prête !
    goto app_ready
)

echo ... attente (!counter!/!timeout! secondes)
timeout /t 5 /nobreak >nul
set /a counter+=5
goto wait_loop

:app_ready
echo.
echo 🎉 Application démarrée avec succès !
echo ==================================================
echo 🌐 Application web    : http://localhost:8080
echo 🗃️  Base de données   : localhost:5432
echo 📊 PgAdmin (debug)    : http://localhost:5050 (avec profile debug)
echo ❤️  Health check      : http://localhost:8080/actuator/health
echo.
echo 📝 Comptes de test :
echo    - Voir les fixtures dans fixtures_new.sql
echo.
echo ⚡ Commandes utiles :
echo    - Voir les logs      : docker-compose logs -f
echo    - Arrêter           : docker-compose down
echo    - Redémarrer        : docker-compose restart
echo    - Mode debug        : docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
echo.

REM Proposer d'ouvrir le navigateur
set /p browser="Voulez-vous ouvrir l'application dans le navigateur ? (Y/n): "
if /i not "!browser!"=="n" (
    echo 🌐 Ouverture du navigateur...
    start http://localhost:8080
)

echo ✨ Prêt pour la démonstration !
pause
