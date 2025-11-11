FROM python:3.11-slim

WORKDIR /app

COPY . .

RUN pip install --no-cache-dir -r requirements.txt

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

CMD ["python", "-u", "genai_worker.py"]
