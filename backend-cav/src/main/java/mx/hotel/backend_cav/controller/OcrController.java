package mx.hotel.backend_cav.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.CorreccionesUsuarioResponse;
import mx.hotel.backend_cav.dto.DesempenoOcrResponse;
import mx.hotel.backend_cav.dto.MetricasOcrDiaResponse;
import mx.hotel.backend_cav.dto.MetricasOcrResponse;
import mx.hotel.backend_cav.dto.OcrRecognitionResponse;
import mx.hotel.backend_cav.service.OcrService;
import mx.hotel.backend_cav.service.MetricasOcrService;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService service;
    private final MetricasOcrService metricasService;

    public OcrController(OcrService service, MetricasOcrService metricasService) {
        this.service = service;
        this.metricasService = metricasService;
    }

    /** Procesa una captura de placa y guarda el intento OCR pendiente de vinculación. */
    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OcrRecognitionResponse>> reconocer(@RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(ApiResponse.ok("Captura OCR procesada", service.reconocer(image)));
    }

    /** Devuelve las métricas OCR acumuladas del día actual. */
    @GetMapping("/metricas/hoy")
    public ResponseEntity<ApiResponse<MetricasOcrResponse>> hoy() {
        return ResponseEntity.ok(ApiResponse.ok("Métricas OCR del día", metricasService.hoy()));
    }

    /** Devuelve la serie diaria de métricas de los últimos treinta días. */
    @GetMapping("/metricas/ultimos-30-dias")
    public ResponseEntity<ApiResponse<java.util.List<MetricasOcrDiaResponse>>> ultimos30Dias() {
        return ResponseEntity.ok(ApiResponse.ok("Histórico de métricas OCR", metricasService.ultimos30Dias()));
    }

    /** Devuelve los indicadores actuales de desempeño OCR. */
    @GetMapping("/metricas/desempeno")
    public ResponseEntity<ApiResponse<DesempenoOcrResponse>> desempeno() {
        return ResponseEntity.ok(ApiResponse.ok("Desempeño OCR", metricasService.desempeno()));
    }

    /** Devuelve las correcciones manuales agrupadas por usuario en los últimos treinta días. */
    @GetMapping("/metricas/correcciones-usuario")
    public ResponseEntity<ApiResponse<java.util.List<CorreccionesUsuarioResponse>>> correccionesPorUsuario() {
        return ResponseEntity.ok(ApiResponse.ok("Correcciones OCR por usuario", metricasService.correccionesPorUsuario()));
    }
}