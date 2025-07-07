# 📦 Distribution de l'Application de Facturation

## 🎯 Pour partager cette application avec d'autres utilisateurs

### 📋 Ce qu'il faut partager :

**Tous les fichiers de ce dossier**, incluant :

- ✅ Fichiers Docker (`Dockerfile`, `docker-compose*.yml`)
- ✅ Scripts de démarrage (`start-demo.*`)
- ✅ Code source (`src/`, `pom.xml`)
- ✅ Documentation (`README.md`, `WINDOWS.md`)
- ✅ Données de test (`fixtures_new.sql`)

### 🚀 Instructions pour l'utilisateur final :

#### 🐧 **Linux / macOS**

```bash
./start-demo.sh
```

#### 🪟 **Windows**

- **PowerShell** : `.\start-demo.ps1`
- **Command Prompt** : `start-demo.bat`

### 📦 Méthodes de distribution :

1. **Archive ZIP/TAR** : Compresser tout le dossier
2. **Git Repository** : Pousser sur GitHub/GitLab
3. **Partage réseau** : Copier le dossier complet
4. **USB/CD** : Copier tous les fichiers

### ⚠️ Important :

- L'utilisateur doit avoir **Docker** installé
- Sur Windows : **Docker Desktop** requis
- Tous les scripts sont inclus pour tous les OS

### 🆘 Support :

- Voir `README.md` pour les instructions complètes
- Voir `WINDOWS.md` pour les spécificités Windows
- Problèmes courants et solutions inclus

## ✅ Test de distribution :

1. Copier tout le dossier sur un autre ordinateur
2. Installer Docker
3. Exécuter le script approprié pour l'OS
4. Ouvrir http://localhost:8080

🎉 **Prêt à distribuer !**
