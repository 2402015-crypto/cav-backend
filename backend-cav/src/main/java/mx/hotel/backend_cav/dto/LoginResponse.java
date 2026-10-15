package mx.hotel.backend_cav.dto;

public record LoginResponse(String token, String tipo, Integer id, String nombre, String rol) {
}