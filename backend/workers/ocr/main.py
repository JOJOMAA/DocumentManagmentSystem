import logging
from config import (RABBIT_HOST, RABBIT_USER, RABBIT_PASS,
                    OCR_QUEUE, RESULT_QUEUE,
                    MINIO_URL, MINIO_ACCESS, MINIO_SECRET,
                    TESS_LANG, TESS_DPI)
from rabbit import Rabbit
from minio_client import MinioWrapper
from ocr_service import ocr_pdf

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ocr-worker")

rabbit = Rabbit(RABBIT_HOST, RABBIT_USER, RABBIT_PASS)
minio = MinioWrapper(MINIO_URL, MINIO_ACCESS, MINIO_SECRET)

def handle_event(evt: dict):
    doc_id = evt.get("id")
    bucket = evt.get("bucket")
    key = evt.get("objectKey")
    if not (doc_id and bucket and key):
        log.warning("Invalid event skipped: %s", evt)
        return
    log.info("Processing id=%s key=%s bucket=%s", doc_id, key, bucket)
    pdf_bytes = minio.get_pdf_bytes(bucket, key)
    text = ocr_pdf(pdf_bytes, dpi=TESS_DPI, lang=TESS_LANG)
    rabbit.publish(RESULT_QUEUE, {"id": doc_id, "text": text})
    log.info("Published OCR result id=%s chars=%d", doc_id, len(text))

def main():
    log.info("Started. Waiting on %s", OCR_QUEUE)
    rabbit.consume(OCR_QUEUE, handle_event)

if __name__ == "__main__":
    main()