package mx.hotel.backend_cav.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardAdminResponse(
        LocalDate fecha,
        Resumen resumen,
        List<PuntoSerie> entradas,
        List<PuntoSerie> ocr,
        List<Ranking> proveedores,
        List<Ranking> transportadoras) {

    public record Resumen(
            long registros,
            long vehiculosActivos,
            long entradas,
            long salidas,
            long empresasTransportadoras,
            BigDecimal precisionOcr,
            BigDecimal registrosPorUsuario) {
    }

    public record PuntoSerie(String etiqueta, BigDecimal valor) {
    }

    public record Ranking(String nombre, long accesos) {
    }
}
