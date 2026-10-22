package mx.hotel.backend_cav.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.EntradaLookupResponse;
import mx.hotel.backend_cav.dto.EntradaManualRequest;
import mx.hotel.backend_cav.dto.EntradaManualResponse;
import mx.hotel.backend_cav.dto.EstadisticasDiaResponse;
import mx.hotel.backend_cav.service.EntradaManualService;
import mx.hotel.backend_cav.service.EstadisticasAccesoService;

@RestController
@RequestMapping("/api/entradas/manual")
@Validated
public class EntradaManualController {

    private final EntradaManualService service;
    private final EstadisticasAccesoService estadisticasService;

    public EntradaManualController(EntradaManualService service, EstadisticasAccesoService estadisticasService) {
        this.service = service;
        this.estadisticasService = estadisticasService;
    }

    /** Busca un vehículo existente por placa para precargar el formulario. */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<EntradaLookupResponse>> buscar(@RequestParam String placa) {
        return ResponseEntity.ok(ApiResponse.ok("Búsqueda de vehículo completada", service.buscar(placa)));
    }

    /** Registra una nueva entrada manual sin modificar registros anteriores. */
    @PostMapping
    public ResponseEntity<ApiResponse<EntradaManualResponse>> registrar(
            @Valid @RequestBody EntradaManualRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Entrada registrada", service.registrar(request, userDetails)));
    }

    /** Obtiene los contadores de accesos del día actual sin eliminar históricos. */
    @GetMapping("/estadisticas-hoy")
    public ResponseEntity<ApiResponse<EstadisticasDiaResponse>> estadisticasHoy() {
        return ResponseEntity.ok(ApiResponse.ok("Estadísticas del día consultadas", estadisticasService.obtenerHoy()));
    }
}