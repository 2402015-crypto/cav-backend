package mx.hotel.backend_cav.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.hotel.backend_cav.service.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController extends AbstractNombreController {

    public EmpresaController(EmpresaService service) {
        super(service, "Empresa");
    }
}