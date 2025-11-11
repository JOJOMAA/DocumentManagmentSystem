import logging, requests, json
from config import GENAI_API_KEY, GENAI_MODEL, GENAI_URL, GENAI_TIMEOUT

log = logging.getLogger("genai-client")

def summarize_text(text: str, max_output_tokens: int = 512) -> str:  #summarize as string
    if not GENAI_API_KEY:
        raise RuntimeError("GENAI_API_KEY not configured")

    endpoint = f"{GENAI_URL}/models/{GENAI_MODEL}:generateContent?key={GENAI_API_KEY}"  #baut REquest URL
    payload = {             #JSON ANfrage
        "contents": [{
            "parts": [
                {"text": "Summarize the following document for a business user. "
                         "Write 5-8 bullet points and a one-sentence executive summary at the top.\n\n"},
                {"text": text}
            ]
        }],
        "generationConfig": {
            "temperature": 0.2,     #sachlicher Text
            "maxOutputTokens": max_output_tokens
        }
    }

    try:
        resp = requests.post(endpoint, json=payload, timeout=GENAI_TIMEOUT)  #POST ANfrage an endpoint
    except requests.RequestException as e:
        raise RuntimeError(f"GenAI request failed: {e}") from e

    if resp.status_code >= 400:
        try:
            err = resp.json()
        except Exception:
            err = resp.text
        raise RuntimeError(f"GenAI HTTP {resp.status_code}: {err}")

    data = resp.json()  #Umwandeln JSON in Python dictionary
    try:
        parts = data["candidates"][0]["content"]["parts"]
        summary = "".join(p.get("text","") for p in parts).strip()  #Zusammenfassung extrahieren + Whiotespace entfernen
        if not summary:
            raise KeyError("empty summary")
        return summary
    except Exception as e:
        raise RuntimeError(f"Unexpected GenAI response shape: {json.dumps(data)[:800]}") from e
