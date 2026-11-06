package mx.hotel.backend_cav.dto;

import java.math.BigDecimal;

public record MetricasOcrResponse(int imagenesProcesadas, int detectadas, int fallidas,
        int correccionesManuales, BigDecimal precision, Integer tiempoPromedio) {
}
