import json, pika

class Rabbit:
    def __init__(self, host, user, password):
        creds = pika.PlainCredentials(user, password)
        params = pika.ConnectionParameters(host=host, credentials=creds)
        self.conn = pika.BlockingConnection(params)
        self.ch = self.conn.channel()

    def _queue(self, name):
        self.ch.queue_declare(queue=name, durable=True)

    def publish(self, queue, payload: dict):
        self._queue(queue)
        self.ch.basic_publish(
            exchange="",
            routing_key=queue,
            body=json.dumps(payload).encode("utf-8"),
            properties=pika.BasicProperties(
                content_type="application/json",
                delivery_mode=2
            )
        )

    def consume(self, queue, handler):
        self._queue(queue)
        def _cb(ch, method, properties, body):
            try:
                msg = json.loads(body)
            except Exception:
                msg = {}
            handler(msg)
            ch.basic_ack(method.delivery_tag)
        self.ch.basic_qos(prefetch_count=1)
        self.ch.basic_consume(queue=queue, on_message_callback=_cb)
        self.ch.start_consuming()