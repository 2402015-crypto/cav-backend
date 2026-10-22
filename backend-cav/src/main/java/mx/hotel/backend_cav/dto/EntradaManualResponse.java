package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

public record EntradaManualResponse(Integer idRegistro, String placa, LocalDateTime fechaEntrada) {
}