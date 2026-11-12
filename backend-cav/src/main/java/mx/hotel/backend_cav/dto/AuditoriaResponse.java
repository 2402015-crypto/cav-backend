package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record AuditoriaResponse(
        Integer id,
        Integer idRegistro,
        String usuario,
        String accion,
        String descripcion,
        String placa,
        LocalDateTime fecha) {
}
