import main

class DummyMinio:
    def __init__(self, data): self.data = data
    def get_pdf_bytes(self, b, k): return self.data

class DummyRabbit:
    def __init__(self): self.sent = []
    def publish(self, q, payload): self.sent.append((q, payload))

def test_pipeline(monkeypatch):
    main.minio = DummyMinio(b"%PDF mock")
    main.rabbit = DummyRabbit()
    monkeypatch.setattr(main, "ocr_pdf", lambda b, dpi=0, lang="": "FAKE_TEXT")
    evt = {"id": 7, "bucket": "pdfs", "objectKey": "file.pdf"}
    main.handle_event(evt)
    assert main.rabbit.sent[0][1]["id"] == 7
    assert main.rabbit.sent[0][1]["text"] == "FAKE_TEXT"