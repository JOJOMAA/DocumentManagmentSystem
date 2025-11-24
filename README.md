## Paperless Project
# Setup

**Umgebungsvariablen prüfen** <br>
Stelle sicher, dass die Datei `.env` im gleichen Verzeichnis wie die `docker-compose.yaml` liegt. Sie muss folgende Werte enthalten (bereits konfiguriert):
* `GEMINI_API_KEY`
* `GEMINI_MODEL`

**Docker-Container starten**
```bash
    docker-compose up -d --build
