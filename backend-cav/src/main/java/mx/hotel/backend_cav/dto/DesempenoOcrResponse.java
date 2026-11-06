package mx.hotel.backend_cav.dto;

import java.math.BigDecimal;

public record DesempenoOcrResponse(BigDecimal precisionActual, BigDecimal promedioCorrecciones,
        Integer tiempoPromedio) {
}
