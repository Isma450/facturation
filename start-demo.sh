#!/bin/bash

# Script de démarrage complet pour l'évaluation
echo "🚀 Démarrage de l'application de Facturation Dockerisée"
echo "=================================================="

# Vérifier que Docker est installé et lancé
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé. Veuillez l'installer d'abord."
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "❌ Docker n'est pas démarré. Veuillez le démarrer."
    exit 1
fi

# Nettoyage des anciens conteneurs (optionnel)
read -p "Voulez-vous nettoyer les anciens conteneurs ? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧹 Nettoyage des anciens conteneurs..."
    docker compose down --volumes --remove-orphans
fi

# Construction et démarrage
echo "🔨 Construction et démarrage des services..."
docker compose up --build -d

# Attendre que l'application soit prête
echo "⏳ Attente du démarrage de l'application..."
timeout=120
counter=0

while [ $counter -lt $timeout ]; do
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        echo "✅ Application prête !"
        break
    fi
    echo "... attente ($counter/$timeout secondes)"
    sleep 5
    counter=$((counter + 5))
done

if [ $counter -ge $timeout ]; then
    echo "❌ Timeout - L'application met trop de temps à démarrer"
    echo "Vérifiez les logs avec : docker-compose logs -f"
    exit 1
fi

# Afficher les informations de connexion
echo ""
echo "🎉 Application démarrée avec succès !"
echo "=================================================="
echo "🌐 Application web    : http://localhost:8080"
echo "🗃️  Base de données   : localhost:5432"
echo "📊 PgAdmin (debug)    : http://localhost:5050 (avec profile debug)"
echo "❤️  Health check      : http://localhost:8080/actuator/health"
echo ""
echo "📝 Comptes de test :"
echo "   - Voir les fixtures dans fixtures_new.sql"
echo ""
echo "⚡ Commandes utiles :"
echo "   - Voir les logs      : docker compose logs -f"
echo "   - Arrêter           : docker compose down"
echo "   - Redémarrer        : docker compose restart"
echo "   - Mode debug        : docker compose -f docker-compose.yml -f docker-compose.dev.yml up"
echo ""

# Ouvrir automatiquement le navigateur (macOS)
if command -v open &> /dev/null; then
    echo "🌐 Ouverture du navigateur..."
    sleep 2
    open http://localhost:8080
fi

echo "✨ Prêt pour la démonstration !"
