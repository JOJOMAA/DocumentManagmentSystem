# ---------- Base Image ----------
FROM python:3.11-slim

# ---------- Working Directory ----------
WORKDIR /app

# ---------- Copy Files ----------
COPY . .

# ---------- Install Dependencies ----------
RUN pip install --no-cache-dir -r requirements.txt

# ---------- Environment Variables ----------
# (These can be overridden in docker-compose)
ENV PYTHONUNBUFFERED=1
ENV RABBIT_HOST=rabbitmq
ENV RABBIT_USER=user
ENV RABBIT_PASS=1234
ENV RESULT_QUEUE=RESULT_QUEUE
ENV ERROR_QUEUE=ERROR_QUEUE
ENV REST_BASE_URL=http://rest:8000
ENV GENAI_MODEL=gemini-1.5-flash-latest
ENV GENAI_URL=https://generativelanguage.googleapis.com/v1beta
ENV GENAI_TIMEOUT=20

# ---------- Entrypoint ----------
CMD ["python", "-u", "genai_worker.py"]
