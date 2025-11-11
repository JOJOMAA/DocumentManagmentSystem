import logging, time, requests
from typing import Dict
from rabbit import Rabbit 
from config import (
    RABBIT_HOST, RABBIT_USER, RABBIT_PASS,
    RESULT_QUEUE, ERROR_QUEUE, REST_BASE_URL
)
from genai_client import summarize_text

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s [%(name)s] %(message)s"
)
log = logging.getLogger("genai-worker")

rabbit = Rabbit(RABBIT_HOST, RABBIT_USER, RABBIT_PASS)

def _post_summary(doc_id: str, summary: str) -> None:
    url = f"{REST_BASE_URL}/documents/{doc_id}/summary"
    try:
        resp = requests.post(url, json={"summary": summary}, timeout=10)
        resp.raise_for_status()
    except requests.RequestException as e:
        raise RuntimeError(f"REST request failed: {e}") from e

def _report_error(stage: str, evt: Dict, err: str) -> None:
    try:
        rabbit.publish(ERROR_QUEUE, {
            "stage": stage,
            "event": evt,
            "error": err
        })
    except Exception:
        log.exception("Failed to publish error to %s", ERROR_QUEUE)

def handle_event(evt: Dict) -> None:
    """
    Consumes OCR results from RESULT_QUEUE:
      { "id": <doc_id>, "text": <extracted_text> }
    Summarizes via Gemini and POSTs to REST to persist.
    """
    doc_id = evt.get("id")
    text   = evt.get("text")

    if not doc_id or not isinstance(text, str):
        log.warning("Invalid OCR result skipped: %s", evt)
        return

    log.info("Summarizing id=%s (chars=%d)", doc_id, len(text))

    for attempt in range(1, 4):
        try:
            summary = summarize_text(text)
            _post_summary(doc_id, summary)
            log.info("Summary stored for id=%s (len=%d)", doc_id, len(summary))
            return
        except Exception as e:
            err = f"[attempt {attempt}/3] {e}"
            log.warning("Transient failure id=%s: %s", doc_id, err)
            if attempt == 3:
                log.error("Giving up id=%s after 3 attempts", doc_id)
                _report_error("genai-worker", {"id": doc_id}, str(e))
            else:
                time.sleep(2 ** attempt)

def main() -> None:
    log.info("Started. Waiting on %s", RESULT_QUEUE)
    rabbit.consume(RESULT_QUEUE, handle_event)

if __name__ == "__main__":
    main()
