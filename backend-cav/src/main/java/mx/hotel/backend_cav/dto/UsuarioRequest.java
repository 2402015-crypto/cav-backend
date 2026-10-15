package mx.hotel.backend_cav.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 150, message = "El nombre completo no puede superar 150 caracteres")
        String nombreCompleto,
        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 50, message = "El usuario no puede superar 50 caracteres")
        String usuario,
        @Size(min = 8, max = 255, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,
        @NotBlank(message = "El rol es obligatorio") String rol) {
}