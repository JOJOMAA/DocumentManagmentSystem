import logging, requests, json, math
from typing import Optional
from config import GENAI_API_KEY, GENAI_MODEL, GENAI_URL, GENAI_TIMEOUT

log = logging.getLogger("genai-client")

def summarize_text(text: str, max_output_tokens: int = 512) -> str:
    """
    Calls Google Gemini generateContent to produce a concise summary.
    Uses a single prompt; if text is extremely large you can pre-trim or chunk upstream.
    """
    if not GENAI_API_KEY:
        raise RuntimeError("GENAI_API_KEY not configured")

    endpoint = f"{GENAI_URL}/models/{GENAI_MODEL}:generateContent?key={GENAI_API_KEY}"
    payload = {
        "contents": [{
            "parts": [
                {"text": "Summarize the following document for a business user. "
                         "Write 5-8 bullet points and a one-sentence executive summary at the top.\n\n"},
                {"text": text}
            ]
        }],
        "generationConfig": {
            "temperature": 0.2,
            "maxOutputTokens": max_output_tokens
        }
    }

    try:
        resp = requests.post(endpoint, json=payload, timeout=GENAI_TIMEOUT)
    except requests.RequestException as e:
        raise RuntimeError(f"GenAI request failed: {e}") from e

    if resp.status_code >= 400:
        # Try to surface helpful details from Google response
        try:
            err = resp.json()
        except Exception:
            err = resp.text
        raise RuntimeError(f"GenAI HTTP {resp.status_code}: {err}")

    data = resp.json()
    # Extract text from candidates
    try:
        parts = data["candidates"][0]["content"]["parts"]
        summary = "".join(p.get("text","") for p in parts).strip()
        if not summary:
            raise KeyError("empty summary")
        return summary
    except Exception as e:
        raise RuntimeError(f"Unexpected GenAI response shape: {json.dumps(data)[:800]}") from e
