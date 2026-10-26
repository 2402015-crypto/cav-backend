package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record SalidaActivaResponse(Integer idRegistro, String placa, String conductor, String relacionado, String tipoVisitante, LocalDateTime fechaEntrada) {
}