package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record HistorialAccesoResponse(
        Integer idRegistro,
        String placa,
        String conductor,
        String relacionado,
        String tipoVisitante,
        String empresa,
        String transportadora,
        LocalDateTime fechaEntrada,
        LocalDateTime fechaSalida,
        String observaciones,
        long permanenciaMinutos,
        String permanencia) {
}
