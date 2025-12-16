# Paperless Document Management System

Ein modernes Dokumentenmanagementsystem (DMS), das auf einer Microservices-Architektur basiert. Das System ermöglicht das Hochladen, Speichern, Durchsuchen und Analysieren von PDF-Dokumenten mithilfe von OCR (Optical Character Recognition) und Generativer KI (Google Gemini).

## Features

* **Dokumenten-Upload**: Einfacher Upload von PDF-Dateien über das Web-Frontend.
* **Volltextsuche**: Integrierte OCR-Texterkennung (Tesseract) ermöglicht das Durchsuchen von gescannten Dokumenten via ElasticSearch.
* **KI-Zusammenfassung**: Automatische Inhaltszusammenfassung jedes Dokuments durch Google Gemini AI.
* **Batch-Processing**: Automatisierte Verarbeitung von Zugriffsprotokollen (XML) für Statistiken.
* **Skalierbare Architektur**: Vollständig containerisiert mit Docker und orchestriert über RabbitMQ.

## Tech Stack

* **Frontend**: Angular (Material Design)
* **Backend**: Spring Boot (Java 21)
* **Datenbanken**: PostgreSQL (Metadaten), ElasticSearch (Suchindex), MinIO (S3-kompatibler Objektspeicher)
* **Messaging**: RabbitMQ
* **Worker Services**: Python (OCR mit Tesseract, GenAI mit Google Gemini)
* **Batch Service**: Spring Boot (Scheduled Tasks)

## Voraussetzungen

* Docker
* Google Gemini API Key

## ⚙️ Installation & Start

1.  **Repository klonen**
    ```bash
    git clone https://github.com/JOJOMAA/DocumentManagmentSystem
    cd DocumentManagmentSystem
    ```

2.  **Umgebungsvariablen konfigurieren**
    Erstelle eine `.env` Datei im Hauptverzeichnis (neben `docker-compose.yaml`) und füge deinen API-Key ein:
    ```env
    GEMINI_API_KEY=dein_google_api_key_hier
    GEMINI_MODEL=gemini-2.5-flash
    ```

3.  **Anwendung starten**
    Starte die Docker Container (im verzeichnis von docker-compose.yaml):
    ```bash
    docker-compose up -d --build
    ```

## Zugriff auf Dienste

Nach dem Start sind die Dienste unter folgenden Adressen erreichbar:

| Service | URL | Beschreibung | Credentials (User/Pass) |
| :--- | :--- | :--- | :--- |
| **Frontend** | `http://localhost:4200` | Web-Oberfläche | - |
| **Backend API** | `http://localhost:8081` | REST API | - |
| **MinIO Console** | `http://localhost:9001` | S3 Browser | `minioadmin` / `minioadmin123` |
| **RabbitMQ** | `http://localhost:15672` | Message Queue UI | `user` / `1234` |
| **PostgreSQL** | `localhost:5433` | Datenbank | `admin` / `123` |

## Architektur

Das System folgt einem ereignisgesteuerten Ansatz:
1.  Frontend lädt PDF an Backend.
2.  Backend speichert Datei in MinIO und Metadaten in PostgreSQL.
3.  Backend sendet Nachricht an RabbitMQ (`OCR_QUEUE`).
4.  **OCR-Worker** liest Nachricht, holt PDF, extrahiert Text und speichert ihn in ElasticSearch.
5.  Nach OCR wird eine Nachricht an die `GENAI_REQUEST_QUEUE` gesendet.
6.  **GenAI-Worker** generiert eine Zusammenfassung und speichert das Ergebnis.