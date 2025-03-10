# DICOM Processor

**Un outil Java pour surveiller un répertoire, lire et traiter des fichiers DICOM, tout en modifiant les métadonnées et ajoutant des annotations spécifiques.**

---

## Technologies utilisées

- **Java 17** – Langage principal
- **dcm4che** – Librairie pour lire et manipuler les fichiers DICOM
- **Maven** – Gestionnaire de dépendances et build
- **Docker** – Conteneurisation de l’application
- **Git / GitHub** – Gestion de version
- **Log4j** – Logger
- **h2** – Base de données

---

##  Pré-requis

- **Java 17** installé
- **Maven 3.x**
- **Docker**
- **Git**

---

## Installation

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/kevpdev/dicom-processor.git  
   cd dicom-processor  
   
2. Configurer les variables d’environnement :
   ```bash
   export DICOM_WATCHER_INPUT_PATH=/chemin/vers/input
   export DICOM_WATCHER_PROCESSED_PATH=/chemin/vers/processed
   export DICOM_WATCHER_FAILED_PATH=/chemin/vers/failed
   export DICOM_WATCHER_LOGO_PATH=/chemin/vers/logo
   export DICOM_WATCHER_ARCHIVE_PATH=/chemin/vers/archive




 

