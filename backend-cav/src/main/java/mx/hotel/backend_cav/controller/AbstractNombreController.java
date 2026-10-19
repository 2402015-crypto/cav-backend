package mx.hotel.backend_cav.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.NombreRequest;
import mx.hotel.backend_cav.dto.NombreResponse;
import mx.hotel.backend_cav.service.AbstractNombreService;

public abstract class AbstractNombreController {

    private final AbstractNombreService<?> service;
    private final String recurso;

    protected AbstractNombreController(AbstractNombreService<?> service, String recurso) {
        this.service = service;
        this.recurso = recurso;
    }

    /** Obtiene todos los registros del catálogo. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NombreResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(recurso + "s consultadas", service.listar()));
    }

    /** Obtiene un registro por su identificador. */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NombreResponse>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(recurso + " consultada", service.obtener(id)));
    }

    /** Devuelve sugerencias que contienen el texto recibido. */
    @GetMapping("/sugerencias")
    public ResponseEntity<ApiResponse<List<NombreResponse>>> sugerir(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("Sugerencias consultadas", service.sugerir(q)));
    }

    /** Crea un registro del catálogo. */
    @PostMapping
    public ResponseEntity<ApiResponse<NombreResponse>> crear(@Valid @RequestBody NombreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(recurso + " creada", service.crear(request)));
    }

    /** Actualiza un registro del catálogo. */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NombreResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody NombreRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(recurso + " actualizada", service.actualizar(id, request)));
    }

    /** Elimina un registro del catálogo. */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(recurso + " eliminada", null));
    }
}