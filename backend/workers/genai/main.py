import logging

from config import (
    RABBIT_HOST,
    RABBIT_USER,
    RABBIT_PASS,
    RESULT_QUEUE,
    SUMMARY_QUEUE,
)
from rabbit import Rabbit
from genai_service import summarize_text

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("genai-worker")

rabbit = Rabbit(RABBIT_HOST, RABBIT_USER, RABBIT_PASS)


def handle_event(evt: dict):
    doc_id = evt.get("documentId")
    text = evt.get("text")

    if not doc_id or not text:
        log.warning("Invalid OCR result event skipped: %s", evt)
        return

    log.info("Summarizing document id=%s (chars=%d)", doc_id, len(text))

    try:
        summary = summarize_text(text)
    except Exception as e:
        log.exception("GenAI summarization failed for id=%s: %s", doc_id, e)
        # (option: publish error status instead of summary)
        return

    result = {
        "id": doc_id,
        "summary": summary,
    }

    rabbit.publish(SUMMARY_QUEUE, result)
    log.info("Published summary for id=%s (chars=%d)", doc_id, len(summary))


def main():
    log.info("GenAI worker started. Waiting on %s", RESULT_QUEUE)
    rabbit.consume(RESULT_QUEUE, handle_event)


if __name__ == "__main__":
    main()
