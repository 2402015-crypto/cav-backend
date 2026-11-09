package mx.hotel.backend_cav.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardOperativoResponse(
        LocalDate fecha,
        long registros,
        long entradas,
        long salidas,
        long activos,
        List<MovimientoAccesoResponse> movimientos) {
}
