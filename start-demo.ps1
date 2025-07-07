# Script de démarrage pour Windows PowerShell
# Équivalent de start-demo.sh pour les utilisateurs Windows

param(
    [switch]$Clean,
    [switch]$Help
)

# Couleurs pour les messages
$Green = [System.ConsoleColor]::Green
$Red = [System.ConsoleColor]::Red
$Yellow = [System.ConsoleColor]::Yellow
$Blue = [System.ConsoleColor]::Blue

function Write-ColorText {
    param([string]$Text, [System.ConsoleColor]$Color = [System.ConsoleColor]::White)
    $originalColor = [Console]::ForegroundColor
    [Console]::ForegroundColor = $Color
    Write-Host $Text
    [Console]::ForegroundColor = $originalColor
}

function Show-Help {
    Write-ColorText "🚀 Script de démarrage de l'application de Facturation" $Blue
    Write-ColorText "========================================================" $Blue
    Write-Host ""
    Write-Host "Usage:"
    Write-Host "  .\start-demo.ps1          - Démarre l'application"
    Write-Host "  .\start-demo.ps1 -Clean   - Nettoie et démarre l'application"
    Write-Host "  .\start-demo.ps1 -Help    - Affiche cette aide"
    Write-Host ""
    Write-Host "Prérequis:"
    Write-Host "  - Docker Desktop pour Windows"
    Write-Host "  - PowerShell 5.0+ (inclus dans Windows 10/11)"
    exit 0
}

if ($Help) {
    Show-Help
}

Write-ColorText "🚀 Démarrage de l'application de Facturation Dockerisée" $Blue
Write-ColorText "========================================================" $Blue

# Vérifier que Docker est installé et lancé
Write-ColorText "🔍 Vérification de Docker..." $Yellow

try {
    $dockerVersion = docker --version 2>$null
    if (-not $dockerVersion) {
        throw "Docker non trouvé"
    }
    Write-ColorText "✅ Docker détecté: $dockerVersion" $Green
} catch {
    Write-ColorText "❌ Docker n'est pas installé ou n'est pas dans le PATH." $Red
    Write-ColorText "   Veuillez installer Docker Desktop: https://www.docker.com/products/docker-desktop" $Red
    exit 1
}

try {
    docker info 2>$null | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker non démarré"
    }
    Write-ColorText "✅ Docker est démarré et accessible" $Green
} catch {
    Write-ColorText "❌ Docker n'est pas démarré. Veuillez lancer Docker Desktop." $Red
    exit 1
}

# Nettoyage des anciens conteneurs (optionnel)
if ($Clean) {
    Write-ColorText "🧹 Nettoyage des anciens conteneurs..." $Yellow
    docker-compose down --volumes --remove-orphans 2>$null
} else {
    $response = Read-Host "Voulez-vous nettoyer les anciens conteneurs ? (y/N)"
    if ($response -match "^[Yy]") {
        Write-ColorText "🧹 Nettoyage des anciens conteneurs..." $Yellow
        docker-compose down --volumes --remove-orphans
    }
}

# Construction et démarrage
Write-ColorText "🔨 Construction et démarrage des services..." $Yellow
$startTime = Get-Date

try {
    docker-compose up --build -d
    if ($LASTEXITCODE -ne 0) {
        throw "Erreur lors du démarrage"
    }
} catch {
    Write-ColorText "❌ Erreur lors du démarrage des services." $Red
    Write-ColorText "   Vérifiez les logs avec: docker-compose logs" $Red
    exit 1
}

# Attendre que l'application soit prête
Write-ColorText "⏳ Attente du démarrage de l'application..." $Yellow
$timeout = 120
$counter = 0
$healthUrl = "http://localhost:8080/actuator/health"

while ($counter -lt $timeout) {
    try {
        $response = Invoke-WebRequest -Uri $healthUrl -TimeoutSec 2 -UseBasicParsing 2>$null
        if ($response.StatusCode -eq 200) {
            Write-ColorText "✅ Application prête !" $Green
            break
        }
    } catch {
        # Continue à attendre
    }
    
    Write-Host "... attente ($counter/$timeout secondes)"
    Start-Sleep -Seconds 5
    $counter += 5
}

if ($counter -ge $timeout) {
    Write-ColorText "❌ Timeout - L'application met trop de temps à démarrer" $Red
    Write-ColorText "   Vérifiez les logs avec : docker-compose logs -f" $Red
    exit 1
}

$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds

Write-ColorText "" $Green
Write-ColorText "🎉 Application démarrée avec succès en $($duration.ToString('F1')) secondes !" $Green
Write-ColorText "========================================================" $Green
Write-ColorText "🌐 Application web    : http://localhost:8080" $Green
Write-ColorText "🗃️  Base de données   : localhost:5432" $Green
Write-ColorText "📊 PgAdmin (debug)    : http://localhost:5050 (avec profile debug)" $Green
Write-ColorText "❤️  Health check      : http://localhost:8080/actuator/health" $Green
Write-ColorText "" $Green
Write-ColorText "📝 Comptes de test :" $Yellow
Write-ColorText "   - Voir les fixtures dans fixtures_new.sql" $Yellow
Write-ColorText "" $Green
Write-ColorText "⚡ Commandes utiles :" $Yellow
Write-ColorText "   - Voir les logs      : docker-compose logs -f" $Yellow
Write-ColorText "   - Arrêter           : docker-compose down" $Yellow
Write-ColorText "   - Redémarrer        : docker-compose restart" $Yellow
Write-ColorText "   - Mode debug        : docker-compose -f docker-compose.yml -f docker-compose.dev.yml up" $Yellow
Write-ColorText "" $Green

# Proposer d'ouvrir le navigateur
$openBrowser = Read-Host "Voulez-vous ouvrir l'application dans le navigateur ? (Y/n)"
if ($openBrowser -notmatch "^[Nn]") {
    Write-ColorText "🌐 Ouverture du navigateur..." $Green
    Start-Process "http://localhost:8080"
}

Write-ColorText "✨ Prêt pour la démonstration !" $Green
