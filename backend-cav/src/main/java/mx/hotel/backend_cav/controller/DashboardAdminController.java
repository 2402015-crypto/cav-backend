package mx.hotel.backend_cav.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.hotel.backend_cav.dto.ApiResponse;
import mx.hotel.backend_cav.dto.DashboardAdminResponse;
import mx.hotel.backend_cav.service.DashboardAdminService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardAdminController {

    private final DashboardAdminService service;

    public DashboardAdminController(DashboardAdminService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardAdminResponse>> obtener(
            @RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard administrativo consultado", service.obtener(dias)));
    }
}
