import os


GENAI_API_KEY = os.getenv("GENAI_API_KEY", "")
GENAI_MODEL   = os.getenv("GENAI_MODEL", "gemini-1.5-flash-latest")
GENAI_URL     = os.getenv("GENAI_URL", "https://generativelanguage.googleapis.com/v1beta")
GENAI_TIMEOUT = int(os.getenv("GENAI_TIMEOUT", "20"))  # seconds

REST_BASE_URL = os.getenv("REST_BASE_URL", "http://rest:8000")

RABBIT_HOST = os.getenv("RABBIT_HOST", "rabbitmq")
RABBIT_USER = os.getenv("RABBIT_USER", "user")
RABBIT_PASS = os.getenv("RABBIT_PASS", "1234")
RESULT_QUEUE = os.getenv("RESULT_QUEUE", "RESULT_QUEUE")
ERROR_QUEUE = os.getenv("ERROR_QUEUE", "ERROR_QUEUE")