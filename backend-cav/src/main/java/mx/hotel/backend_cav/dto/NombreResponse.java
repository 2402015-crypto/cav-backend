package mx.hotel.backend_cav.dto;

import mx.hotel.backend_cav.entity.Nombrable;

public record NombreResponse(Integer id, String nombre) {

    public static NombreResponse from(Nombrable entidad) {
        return new NombreResponse(entidad.getId(), entidad.getNombre());
    }
}