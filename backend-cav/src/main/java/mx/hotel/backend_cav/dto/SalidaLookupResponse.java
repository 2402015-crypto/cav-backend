package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record SalidaLookupResponse(
        Integer idRegistro,
        String placa,
        String conductor,
        String tipoVisitante,
        String empresa,
        String transportadora,
        String marca,
        String color,
        LocalDateTime fechaEntrada,
        LocalDateTime fechaSalida,
        String observaciones,
        long permanenciaMinutos,
        String permanencia,
        String area,
        String villa,
        Integer numeroPax,
        String numeroUnidad,
        String destino) {
}