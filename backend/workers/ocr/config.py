import os
RABBIT_HOST = os.getenv("RABBIT_HOST", "rabbitmq")
RABBIT_USER = os.getenv("RABBIT_USER", "user")
RABBIT_PASS = os.getenv("RABBIT_PASS", "1234")
OCR_QUEUE    = os.getenv("OCR_QUEUE", "OCR_QUEUE")
RESULT_QUEUE = os.getenv("RESULT_QUEUE", "RESULT_QUEUE")

MINIO_URL    = os.getenv("MINIO_URL", "http://minio:9000")
MINIO_ACCESS = os.getenv("MINIO_ACCESS_KEY", "minioadmin")
MINIO_SECRET = os.getenv("MINIO_SECRET_KEY", "minioadmin123")

TESS_LANG = os.getenv("TESS_LANG", "deu+eng")
TESS_DPI  = int(os.getenv("TESS_DPI", "300"))