package mx.hotel.backend_cav.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;
import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.UsuarioRequest;
import mx.hotel.backend_cav.dto.UsuarioResponse;
import mx.hotel.backend_cav.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios consultados", usuarioService.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario consultado", usuarioService.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado", usuarioService.crear(request, userDetails)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody UsuarioRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado", usuarioService.actualizar(id, request, userDetails)));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.desactivar(id, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Usuario desactivado", null));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.activar(id, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Usuario activado", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        usuarioService.eliminar(id, userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado", null));
    }
}