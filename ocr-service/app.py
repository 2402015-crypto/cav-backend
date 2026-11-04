import os
import re
import tempfile
import time
from pathlib import Path

import cv2
from fast_alpr import ALPR
from fastapi import FastAPI, Request

app = FastAPI(title="CAV OCR Service", version="1.0.0")
alpr = ALPR()


def normalize_plate(value: str | None) -> str | None:
    if not value:
        return None
    normalized = re.sub(r"[^A-Z0-9-]", "", value.upper())
    return normalized or None


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/v1/ocr/recognize")
async def recognize(request: Request) -> dict:
    started = time.perf_counter()
    content_type = request.headers.get("content-type", "image/jpeg").split(";", 1)[0].strip().lower()
    suffix = ".png" if content_type == "image/png" else ".jpg"
    contents = await request.body()
    if not contents:
        return {"detectada": False, "mensaje": "La imagen de la placa está vacía."}
    temporary_path = None
    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as temporary:
            temporary.write(contents)
            temporary_path = temporary.name

        frame = cv2.imread(temporary_path)
        if frame is None:
            return {"detectada": False, "mensaje": "La imagen no pudo ser leída."}

        results = alpr.predict(temporary_path)
        if not results:
            return {
                "detectada": False,
                "mensaje": "No se detectó una placa en la imagen.",
                "tiempoProcesamientoMs": round((time.perf_counter() - started) * 1000),
            }

        best = results[0]
        plate = normalize_plate(getattr(getattr(best, "ocr", None), "text", None))
        if not plate:
            return {"detectada": False, "mensaje": "La placa no pudo ser interpretada."}

        detection = getattr(best, "detection", None)
        ocr = getattr(best, "ocr", None)
        return {
            "detectada": True,
            "placa": plate,
            "confianzaDetectada": getattr(detection, "confidence", None),
            "regionDetectada": getattr(ocr, "region", None),
            "tiempoProcesamientoMs": round((time.perf_counter() - started) * 1000),
        }
    finally:
        if temporary_path:
            os.unlink(temporary_path)
