# Instructions pour Windows

## Prérequis

1. **Docker Desktop pour Windows**

   - Télécharger : https://www.docker.com/products/docker-desktop
   - Installer et redémarrer Windows
   - S'assurer que WSL2 est activé

2. **Vérifier l'installation**
   - Docker Desktop doit être démarré (icône dans la barre des tâches)
   - Ouvrir PowerShell ou Command Prompt
   - Taper : `docker --version`

## Démarrage de l'Application

### Option 1 : PowerShell (Recommandé)

1. Ouvrir **PowerShell** en tant qu'administrateur
2. Naviguer vers le dossier de l'application :
   ```powershell
   cd C:\chemin\vers\facturation
   ```
3. Exécuter le script :
   ```powershell
   .\start-demo.ps1
   ```

### Option 2 : Command Prompt

1. Ouvrir **Invite de commandes** (cmd)
2. Naviguer vers le dossier :
   ```cmd
   cd C:\chemin\vers\facturation
   ```
3. Exécuter :
   ```cmd
   start-demo.bat
   ```

### Option 3 : Manuel

1. Ouvrir PowerShell/CMD dans le dossier
2. Exécuter :
   ```cmd
   docker-compose up --build
   ```

## Accès à l'Application

Une fois démarrée, l'application sera accessible sur :

- **Interface Web** : http://localhost:8080
- **Base de données** : localhost:5432
- **PgAdmin** (debug) : http://localhost:5050

## Problèmes Courants Windows

### Docker Desktop ne démarre pas

- Redémarrer Windows
- Vérifier que la virtualisation est activée dans le BIOS
- Activer WSL2 : `wsl --install` dans PowerShell admin

### Port déjà utilisé

```powershell
# Voir qui utilise le port 8080
netstat -ano | findstr :8080
# Tuer le processus (remplacer PID)
taskkill /F /PID <numero_du_processus>
```

### Erreur de permissions

- Exécuter PowerShell/CMD en tant qu'administrateur
- Vérifier que Docker Desktop a les permissions nécessaires

### Scripts PowerShell bloqués

```powershell
# Autoriser l'exécution des scripts (une seule fois)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## Arrêter l'Application

```cmd
docker-compose down
```

## Support

Si vous rencontrez des problèmes :

1. Vérifier que Docker Desktop est bien démarré
2. Consulter les logs : `docker-compose logs -f`
3. Redémarrer Docker Desktop
4. Redémarrer l'ordinateur si nécessaire

## Test Rapide

Pour vérifier que tout fonctionne :

1. Démarrer l'application avec un des scripts
2. Ouvrir http://localhost:8080 dans le navigateur
3. Vous devriez voir la page de connexion de l'application

**Succès !** L'application est prête à être utilisée.
