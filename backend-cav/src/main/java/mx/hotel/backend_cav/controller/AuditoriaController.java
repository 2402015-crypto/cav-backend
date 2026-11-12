package mx.hotel.backend_cav.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.AuditoriaExportRequest;
import mx.hotel.backend_cav.dto.AuditoriaResponse;
import mx.hotel.backend_cav.service.AuditoriaService;

@RestController
@RequestMapping("/api/bitacora")
@Validated
public class AuditoriaController {

    private final AuditoriaService service;

    public AuditoriaController(AuditoriaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditoriaResponse>>> listar(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) Integer usuario,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.ok("Bitácora consultada",
                service.listar(placa, usuario, accion, desde, hasta)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditoriaResponse>> detalle(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Registro de bitácora consultado", service.detalle(id)));
    }

    @PostMapping("/exportaciones")
    public ResponseEntity<ApiResponse<Void>> exportar(@Valid @RequestBody AuditoriaExportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        service.exportar(userDetails, request.descripcion());
        return ResponseEntity.ok(ApiResponse.ok("Exportación registrada en bitácora", null));
    }
}
