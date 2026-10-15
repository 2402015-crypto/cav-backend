package mx.hotel.backend_cav.dto;

import java.time.LocalDateTime;

import mx.hotel.backend_cav.entity.Usuario;

public record UsuarioResponse(
        Integer id,
        String nombreCompleto,
        String usuario,
        String rol,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime ultimoAcceso) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getUsuario(),
                usuario.getRol().getNombre(),
                usuario.getEstado().name(),
                usuario.getFechaCreacion(),
                usuario.getUltimoAcceso());
    }
}