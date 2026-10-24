package mx.hotel.backend_cav.dto;

import jakarta.validation.constraints.Size;

public record SalidaRequest(@Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres") String observaciones) {
}