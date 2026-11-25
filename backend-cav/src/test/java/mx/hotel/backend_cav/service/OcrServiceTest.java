package mx.hotel.backend_cav.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import mx.hotel.backend_cav.entity.OcrHistorial;
import mx.hotel.backend_cav.exception.BadRequestException;

class OcrServiceTest {

    @Test
    void savesFailedAttemptWhenOcrServiceRejectsRequest() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/ocr/recognize", exchange -> {
            exchange.getRequestBody().readAllBytes();
            byte[] body = "{\"message\":\"inference failed\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(503, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            OcrHistorialService historialService = mock(OcrHistorialService.class);
            when(historialService.guardar(any(OcrHistorial.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            OcrService service = new OcrService(
                    "http://127.0.0.1:" + server.getAddress().getPort(),
                    1000,
                    historialService,
                    new ApplicationTimeService("UTC"));
            MockMultipartFile image = new MockMultipartFile(
                    "image", "capture.jpg", "image/jpeg", new byte[] { 1, 2, 3 });

            assertThrows(BadRequestException.class, () -> service.reconocer(image));

            org.mockito.ArgumentCaptor<OcrHistorial> historialCaptor =
                    org.mockito.ArgumentCaptor.forClass(OcrHistorial.class);
            verify(historialService).guardar(historialCaptor.capture());
            assertEquals("ERROR", historialCaptor.getValue().getResultadoFinal());
        } finally {
            server.stop(0);
        }
    }
}
