package mx.hotel.backend_cav.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntradaManualRequest(
        @NotBlank(message = "El tipo de registro es obligatorio") String tipoRegistro,
        @NotBlank(message = "El nombre completo es obligatorio") @Size(max = 150, message = "El nombre completo no puede exceder 150 caracteres") String nombreCompleto,
        @Size(max = 150, message = "El área no puede exceder 150 caracteres") String area,
        @NotBlank(message = "La marca del vehículo es obligatoria") @Size(max = 100, message = "La marca no puede exceder 100 caracteres") String marca,
        @NotBlank(message = "La placa del vehículo es obligatoria") @Size(max = 30, message = "La placa no puede exceder 30 caracteres") String placa,
        @NotBlank(message = "El color del vehículo es obligatorio") @Size(max = 100, message = "El color no puede exceder 100 caracteres") String color,
        @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres") String observaciones) {
}