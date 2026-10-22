package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record EntradaLookupResponse(
        boolean vehiculoRegistrado,
        String placa,
        String nombreCompleto,
        String area,
        String marca,
        String color,
        LocalDateTime fechaUltimoRegistro) {
}