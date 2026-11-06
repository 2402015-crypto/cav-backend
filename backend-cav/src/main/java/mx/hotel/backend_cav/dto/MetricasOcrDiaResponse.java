package mx.hotel.backend_cav.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MetricasOcrDiaResponse(LocalDate fecha, int imagenesProcesadas, int detectadas,
        int fallidas, int correccionesManuales, BigDecimal precision, Integer tiempoPromedio) {
}
