package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record MovimientoAccesoResponse(
        Integer idRegistro,
        String placa,
        String conductor,
        String tipoVisitante,
        String empresa,
        String transportadora,
        LocalDateTime fecha,
        boolean salida) {
}
