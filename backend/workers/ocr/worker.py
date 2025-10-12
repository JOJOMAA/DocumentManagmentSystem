import json, os, pika

host = os.getenv("RABBIT_HOST", "rabbitmq")
user = os.getenv("RABBIT_USER", "paperless")
pw   = os.getenv("RABBIT_PASS", "paperless")
ocr_q = os.getenv("OCR_QUEUE", "OCR_QUEUE")
result_q = os.getenv("RESULT_QUEUE", "RESULT_QUEUE")

creds = pika.PlainCredentials(user, pw)
params = pika.ConnectionParameters(host=host, credentials=creds)
conn = pika.BlockingConnection(params)
ch = conn.channel()
ch.queue_declare(queue=ocr_q, durable=True)
ch.queue_declare(queue=result_q, durable=True)

print(f"[worker] listening on {ocr_q}")

def handle(ch_, method, properties, body):
    try:
        msg = json.loads(body)
    except Exception:
        msg = {"raw": body.decode("utf-8", errors="ignore")}
    print(f"[worker] got: {msg}")
    # pretend we did OCR; publish placeholder result
    result = {"documentId": msg.get("id"), "status": "RECEIVED"}
    ch.basic_publish("", result_q, json.dumps(result).encode("utf-8"))
    ch.basic_ack(delivery_tag=method.delivery_tag)

ch.basic_qos(prefetch_count=1)
ch.basic_consume(queue=ocr_q, on_message_callback=handle)
ch.start_consuming()
