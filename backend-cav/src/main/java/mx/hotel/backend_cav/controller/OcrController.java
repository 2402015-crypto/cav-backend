package mx.hotel.backend_cav.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.OcrRecognitionResponse;
import mx.hotel.backend_cav.service.OcrService;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService service;

    public OcrController(OcrService service) { this.service = service; }

    /** Procesa una captura de placa y guarda el intento OCR pendiente de vinculación. */
    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OcrRecognitionResponse>> reconocer(@RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(ApiResponse.ok("Captura OCR procesada", service.reconocer(image)));
    }
}