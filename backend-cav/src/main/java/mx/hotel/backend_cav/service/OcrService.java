package mx.hotel.backend_cav.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.hotel.backend_cav.dto.OcrRecognitionResponse;
import mx.hotel.backend_cav.entity.OcrHistorial;
import mx.hotel.backend_cav.exception.BadRequestException;
import mx.hotel.backend_cav.repository.OcrHistorialRepository;

@Service
public class OcrService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OcrService.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final OcrHistorialRepository historialRepository;
    private final ApplicationTimeService timeService;

    public OcrService(
            @Value("${app.ocr.base-url:http://localhost:5000}") String baseUrl,
            @Value("${app.ocr.connect-timeout-ms:2000}") long connectTimeoutMs,
            OcrHistorialRepository historialRepository,
            ApplicationTimeService timeService) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
        this.historialRepository = historialRepository;
        this.timeService = timeService;
    }

    public OcrRecognitionResponse reconocer(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("La imagen de la placa es obligatoria");
        }
        try {
            String contentType = image.getContentType() == null
                    ? MediaType.IMAGE_JPEG_VALUE
                    : image.getContentType();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/ocr/recognize"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", contentType)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(image.getBytes()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOGGER.error("El servicio OCR respondió con HTTP {}: {}", response.statusCode(), response.body());
                throw new BadRequestException("El servicio OCR rechazó la imagen");
            }

            RemoteResponse remote = objectMapper.readValue(response.body(), RemoteResponse.class);
            if (remote == null) {
                throw new BadRequestException("El servicio OCR no devolvió respuesta");
            }
            OcrHistorial historial = new OcrHistorial();
            historial.setPlacaDetectada(remote.placa());
            historial.setConfianzaDetectada(remote.confianzaDetectada() == null
                    ? null
                    : BigDecimal.valueOf(remote.confianzaDetectada()).setScale(2, RoundingMode.HALF_UP));
            historial.setRegionDetectada(remote.regionDetectada());
            historial.setTiempoProcesamientoMs(remote.tiempoProcesamientoMs());
            historial.setResultadoFinal(Boolean.TRUE.equals(remote.detectada()) ? "DETECTADA" : "NO_DETECTADA");
            historial.setFecha(timeService.now());
            historialRepository.save(historial);
            return new OcrRecognitionResponse(historial.getId(), Boolean.TRUE.equals(remote.detectada()),
                    remote.placa(), remote.confianzaDetectada(), remote.regionDetectada(),
                    remote.tiempoProcesamientoMs(), remote.mensaje());
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception exception) {
            LOGGER.error("Error procesando la captura OCR", exception);
            throw new BadRequestException("No fue posible comunicarse con el servicio OCR");
        }
    }

    private record RemoteResponse(Boolean detectada, String placa, Double confianzaDetectada,
            String regionDetectada, Integer tiempoProcesamientoMs, String mensaje) { }
}
