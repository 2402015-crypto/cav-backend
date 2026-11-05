import logging
import re
import tempfile
import time
from pathlib import Path

import cv2
from fast_alpr import ALPR
from fastapi import FastAPI, Request

app = FastAPI(title="CAV OCR Service", version="1.0.0")
alpr = ALPR()
LOGGER = logging.getLogger(__name__)


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
    temporary_directory = None
    try:
        temporary_directory = tempfile.mkdtemp(prefix="cav-ocr-")
        temporary_path = str(Path(temporary_directory) / f"capture{suffix}")
        Path(temporary_path).write_bytes(contents)
        frame = cv2.imread(temporary_path)
        if frame is None:
            return {"detectada": False, "mensaje": "La imagen no pudo ser leída."}

        height, width = frame.shape[:2]
        LOGGER.info("Captura OCR recibida: %sx%s, %s bytes", width, height, len(contents))
        candidate_paths = [temporary_path]
        upscaled = cv2.resize(frame, None, fx=2, fy=2, interpolation=cv2.INTER_CUBIC)
        upscaled_path = str(Path(temporary_directory) / f"capture-upscaled{suffix}")
        cv2.imwrite(upscaled_path, upscaled, [cv2.IMWRITE_JPEG_QUALITY, 95])
        candidate_paths.append(upscaled_path)

        sharpened = cv2.detailEnhance(upscaled, sigma_s=10, sigma_r=0.15)
        enhanced_path = str(Path(temporary_directory) / f"capture-enhanced{suffix}")
        cv2.imwrite(enhanced_path, sharpened, [cv2.IMWRITE_JPEG_QUALITY, 95])
        candidate_paths.append(enhanced_path)

        all_results = []
        for candidate_path in candidate_paths:
            all_results.extend(alpr.predict(candidate_path))
        LOGGER.info("Resultados OCR: %s en %s variantes", len(all_results), len(candidate_paths))
        if not all_results:
            return {
                "detectada": False,
                "mensaje": "No se detectó una placa en la imagen.",
                "tiempoProcesamientoMs": round((time.perf_counter() - started) * 1000),
            }

        best = next(
            (
                result
                for result in all_results
                if normalize_plate(getattr(getattr(result, "ocr", None), "text", None))
            ),
            None,
        )
        if best is None:
            return {
                "detectada": False,
                "mensaje": "La placa no pudo ser interpretada.",
                "tiempoProcesamientoMs": round((time.perf_counter() - started) * 1000),
            }

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
        if temporary_directory:
            temporary_path_object = Path(temporary_directory)
            for path in temporary_path_object.iterdir():
                path.unlink()
            temporary_path_object.rmdir()
