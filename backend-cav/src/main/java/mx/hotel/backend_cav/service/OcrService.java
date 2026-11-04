package mx.hotel.backend_cav.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import mx.hotel.backend_cav.dto.OcrRecognitionResponse;
import mx.hotel.backend_cav.entity.OcrHistorial;
import mx.hotel.backend_cav.exception.BadRequestException;
import mx.hotel.backend_cav.repository.OcrHistorialRepository;

@Service
public class OcrService {

    private final RestClient restClient;
    private final OcrHistorialRepository historialRepository;
    private final ApplicationTimeService timeService;

    public OcrService(
            @Value("${app.ocr.base-url:http://localhost:5000}") String baseUrl,
            OcrHistorialRepository historialRepository,
            ApplicationTimeService timeService) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.historialRepository = historialRepository;
        this.timeService = timeService;
    }

    public OcrRecognitionResponse reconocer(MultipartFile image) {
        if (image == null || image.isEmpty()) throw new BadRequestException("La imagen de la placa es obligatoria");
        try {
            ByteArrayResource resource = new ByteArrayResource(image.getBytes()) {
                @Override public String getFilename() { return image.getOriginalFilename() == null ? "capture.jpg" : image.getOriginalFilename(); }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", resource);
            RemoteResponse remote = restClient.post().uri("/v1/ocr/recognize")
                    .contentType(MediaType.MULTIPART_FORM_DATA).body(body).retrieve().body(RemoteResponse.class);
            if (remote == null) throw new BadRequestException("El servicio OCR no devolvió respuesta");
            OcrHistorial historial = new OcrHistorial();
            historial.setPlacaDetectada(remote.placa());
            historial.setConfianzaDetectada(remote.confianzaDetectada() == null ? null : BigDecimal.valueOf(remote.confianzaDetectada()));
            historial.setRegionDetectada(remote.regionDetectada());
            historial.setTiempoProcesamientoMs(remote.tiempoProcesamientoMs());
            historial.setResultadoFinal(Boolean.TRUE.equals(remote.detectada()) ? "DETECTADA" : "NO_DETECTADA");
            historial.setFecha(timeService.now());
            historialRepository.save(historial);
            return new OcrRecognitionResponse(historial.getId(), Boolean.TRUE.equals(remote.detectada()), remote.placa(), remote.confianzaDetectada(), remote.regionDetectada(), remote.tiempoProcesamientoMs(), remote.mensaje());
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BadRequestException("No fue posible comunicarse con el servicio OCR");
        }
    }

    private record RemoteResponse(Boolean detectada, String placa, Double confianzaDetectada,
            String regionDetectada, Integer tiempoProcesamientoMs, String mensaje) { }
}