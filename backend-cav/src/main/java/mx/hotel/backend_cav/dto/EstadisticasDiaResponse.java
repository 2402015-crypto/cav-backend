package mx.hotel.backend_cav.dto;

import java.time.LocalDate;

public record EstadisticasDiaResponse(LocalDate fecha, long registros, long entradas, long salidas) {
}