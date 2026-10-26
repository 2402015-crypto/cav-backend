package mx.hotel.backend_cav.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.SalidaLookupResponse;
import mx.hotel.backend_cav.dto.SalidaRequest;
import mx.hotel.backend_cav.dto.SalidaActivaResponse;
import mx.hotel.backend_cav.dto.SalidaDetalleResponse;
import mx.hotel.backend_cav.service.SalidaService;

@RestController
@RequestMapping("/api/salidas")
@Validated
public class SalidaController {

    private final SalidaService service;

    public SalidaController(SalidaService service) {
        this.service = service;
    }

    /** Busca la entrada activa de un vehículo por placa. */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<SalidaLookupResponse>> buscar(@RequestParam String placa) {
        return ResponseEntity.ok(ApiResponse.ok("Vehículo activo encontrado", service.buscar(placa)));
    }

    /** Lista vehículos con entrada activa, filtrando opcionalmente por placa o conductor. */
    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<java.util.List<SalidaActivaResponse>>> activos(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.ok("Vehículos activos consultados", service.listarActivos(q)));
    }

    /** Obtiene el detalle e historial de visitas de un vehículo. */
    @GetMapping("/activos/{id}")
    public ResponseEntity<ApiResponse<SalidaDetalleResponse>> detalle(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Detalle de vehículo consultado", service.detalle(id)));
    }

    /** Registra la salida y actualiza las observaciones del vehículo. */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalidaLookupResponse>> registrar(
            @PathVariable Integer id, @Valid @RequestBody SalidaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Salida registrada", service.registrar(id, request)));
    }
}