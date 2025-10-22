import pytesseract
from pdf2image import convert_from_bytes

def ocr_pdf(pdf_bytes: bytes, dpi: int = 300, lang: str = "deu+eng") -> str:
    images = convert_from_bytes(pdf_bytes, dpi=dpi)
    parts = []
    for img in images:
        parts.append(pytesseract.image_to_string(img, lang=lang))
    return "\n".join(parts).strip()