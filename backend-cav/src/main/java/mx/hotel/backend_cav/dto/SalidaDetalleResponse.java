package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SalidaDetalleResponse(
        SalidaLookupResponse detalle,
        List<VisitaHistorialResponse> historial,
        long totalVisitas) {

    public record VisitaHistorialResponse(LocalDateTime entrada, LocalDateTime salida, String permanencia) { }
}