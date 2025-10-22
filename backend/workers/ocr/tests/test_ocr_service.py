import io
from reportlab.pdfgen import canvas
from ocr_service import ocr_pdf

def make_pdf(line: str) -> bytes:
    buf = io.BytesIO()
    c = canvas.Canvas(buf)
    c.setFont("Helvetica", 14)
    c.drawString(72, 720, line)
    c.showPage()
    c.save()
    return buf.getvalue()

def test_ocr_simple():
    pdf = make_pdf("Hello OCR 123")
    out = ocr_pdf(pdf, dpi=200, lang="eng")
    assert "Hello OCR 123" in out