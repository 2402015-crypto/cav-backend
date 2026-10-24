package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntradaManualRequest(
        @NotBlank(message = "El tipo de registro es obligatorio") String tipoRegistro,
        @NotBlank(message = "El nombre completo es obligatorio") @Size(max = 150, message = "El nombre completo no puede exceder 150 caracteres") String nombreCompleto,
        @Size(max = 150, message = "El área no puede exceder 150 caracteres") String area,
        @Size(max = 150, message = "La empresa no puede exceder 150 caracteres") String empresa,
        @Size(max = 150, message = "La transportadora no puede exceder 150 caracteres") String transportadora,
        @Size(max = 150, message = "El destino no puede exceder 150 caracteres") String destino,
        @Size(max = 100, message = "La marca no puede exceder 100 caracteres") String marca,
        @NotBlank(message = "La placa del vehículo es obligatoria") @Size(max = 30, message = "La placa no puede exceder 30 caracteres") String placa,
        @Size(max = 100, message = "El color no puede exceder 100 caracteres") String color,
        @Size(max = 3, message = "El tipo de movimiento no es válido") String tipoMovimiento,
        @Size(max = 50, message = "La villa no puede exceder 50 caracteres") String villa,
        @jakarta.validation.constraints.Min(value = 0, message = "El número de pasajeros no puede ser negativo") Integer numeroPax,
        @Size(max = 30, message = "El número de unidad no puede exceder 30 caracteres") String numeroUnidad,
        @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres") String observaciones,
        LocalDateTime fechaEntrada) {
}