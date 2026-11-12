package mx.hotel.backend_cav.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditoriaExportRequest(@NotBlank String descripcion) {
}
