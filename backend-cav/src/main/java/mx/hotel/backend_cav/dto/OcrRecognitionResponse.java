package mx.hotel.backend_cav.dto;

public record OcrRecognitionResponse(
        Integer idOcr,
        boolean detectada,
        String placa,
        Double confianzaDetectada,
        String regionDetectada,
        Integer tiempoProcesamientoMs,
        String mensaje) {
}