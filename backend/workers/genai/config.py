import os

# RabbitMQ
RABBIT_HOST = os.getenv("RABBIT_HOST", "rabbitmq")
RABBIT_USER = os.getenv("RABBIT_USER", "user")
RABBIT_PASS = os.getenv("RABBIT_PASS", "1234")

RESULT_QUEUE  = os.getenv("RESULT_QUEUE", "RESULT_QUEUE")
SUMMARY_QUEUE = os.getenv("SUMMARY_QUEUE", "SUMMARY_QUEUE")

# Gemini / GenAI config
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "CHANGE_ME")
GEMINI_MODEL   = os.getenv("GEMINI_MODEL", "models/gemini-1.5-flash")
