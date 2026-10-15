from fast_alpr import ALPR  # type: ignore[import-not-found]
import cv2  # type: ignore[import-not-found]
import os
import json
from datetime import datetime

# =====================================
# CONFIGURACION
# =====================================

alpr = ALPR()

os.makedirs(
    "Historiales",
    exist_ok=True
)

os.makedirs(
    "Candidatos_FastALPR",
    exist_ok=True
)

# =====================================
# METRICAS
# =====================================

total_imagenes = 0
detectadas = 0
no_detectadas = 0

reporte = []

# =====================================
# PROCESAR IMAGENES
# =====================================

for archivo in os.listdir("imagenes"):

    if not archivo.lower().endswith(
        (".jpg", ".jpeg", ".png")
    ):
        continue

    total_imagenes += 1

    ruta = os.path.join(
        "imagenes",
        archivo
    )

    print("\n" + "="*60)
    print("Procesando:", archivo)

    img = cv2.imread(ruta)

    resultados = alpr.predict(ruta)

    if not resultados:

        print("❌ No detectada")

        reporte.append(
            f"{archivo} -> NO DETECTADA"
        )

        no_detectadas += 1

        continue

    mejor = resultados[0]

    detectadas += 1

    x1 = mejor.detection.bounding_box.x1
    y1 = mejor.detection.bounding_box.y1
    x2 = mejor.detection.bounding_box.x2
    y2 = mejor.detection.bounding_box.y2

    placa = mejor.ocr.text

    confianza_det = (
        mejor.detection.confidence
    )

    confianza_region = (
        mejor.ocr.region_confidence
    )

    region = mejor.ocr.region

    print(
        f"✅ {placa}"
    )

    # =====================================
    # ROI
    # =====================================

    roi = img[
        y1:y2,
        x1:x2
    ]

    roi_path = os.path.join(
        "Historiales",
        f"ROI_{archivo}"
    )

    cv2.imwrite(
        roi_path,
        roi
    )

    # =====================================
    # IMAGEN ANOTADA
    # =====================================

    anotada = img.copy()

    cv2.rectangle(
        anotada,
        (x1, y1),
        (x2, y2),
        (0, 255, 0),
        3
    )

    cv2.putText(
        anotada,
        placa,
        (x1, y1-10),
        cv2.FONT_HERSHEY_SIMPLEX,
        1,
        (0,255,0),
        2
    )

    salida = os.path.join(
        "Candidatos_FastALPR",
        f"DETECTADA_{archivo}"
    )

    cv2.imwrite(
        salida,
        anotada
    )

    # =====================================
    # REPORTE
    # =====================================

    reporte.append(

        f"""
Imagen: {archivo}
Placa: {placa}
Confianza deteccion: {confianza_det:.4f}
Region: {region}
Confianza region: {confianza_region:.4f}
Coordenadas: ({x1},{y1}) ({x2},{y2})
"""
    )

# =====================================
# REPORTE TXT
# =====================================

fecha = datetime.now().strftime(
    "%Y-%m-%d %H:%M:%S"
)

with open(

    os.path.join(
        "Historiales",
        "reporte_ocr.txt"
    ),

    "w",

    encoding="utf-8"

) as f:

    f.write(
        "REPORTE FASTALPR\n\n"
    )

    f.write(
        f"Fecha: {fecha}\n\n"
    )

    for linea in reporte:

        f.write(linea)
        f.write("\n")

# =====================================
# METRICAS
# =====================================

precision = 0

if total_imagenes > 0:

    precision = (
        detectadas /
        total_imagenes
    ) * 100

metricas = {

    "fecha": fecha,

    "imagenes_procesadas":
        total_imagenes,

    "detectadas":
        detectadas,

    "no_detectadas":
        no_detectadas,

    "precision":
        round(precision, 2)

}

with open(

    os.path.join(
        "Historiales",
        "metricas.json"
    ),

    "w",

    encoding="utf-8"

) as f:

    json.dump(
        metricas,
        f,
        indent=4,
        ensure_ascii=False
    )

print("\n" + "="*60)
print("Proceso terminado")
print(
    f"Detectadas: "
    f"{detectadas}/{total_imagenes}"
)
print(
    f"Precisión: "
    f"{precision:.2f}%"
)