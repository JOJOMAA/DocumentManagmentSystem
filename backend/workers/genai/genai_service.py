import logging
import requests

from config import GEMINI_API_KEY, GEMINI_MODEL

log = logging.getLogger("genai-service")

GEMINI_ENDPOINT = (
    f"https://generativelanguage.googleapis.com/v1beta/{GEMINI_MODEL}:generateContent"
)


def summarize_text(text: str) -> str:
    if not GEMINI_API_KEY or GEMINI_API_KEY == "CHANGE_ME":
        log.error("GEMINI_API_KEY is not set!")
        raise RuntimeError("GEMINI_API_KEY not configured")

    payload = {
        "contents": [
            {
                "parts": [
                    {
                        "text": (
                                "Summarize the following document text in a concise, "
                                "human-readable way:\n\n" + text
                        )
                    }
                ]
            }
        ]
    }

    params = {"key": GEMINI_API_KEY}

    resp = requests.post(GEMINI_ENDPOINT, params=params, json=payload, timeout=60)
    resp.raise_for_status()
    data = resp.json()

    try:
        candidates = data.get("candidates", [])
        first = candidates[0]
        parts = first["content"]["parts"]
        summary = "".join(part.get("text", "") for part in parts)
        return summary.strip()
    except Exception as e:
        log.exception("Failed to parse Gemini response: %s", e)
        raise
