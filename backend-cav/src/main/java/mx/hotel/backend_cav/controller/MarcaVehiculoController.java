package mx.hotel.backend_cav.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.hotel.backend_cav.service.MarcaVehiculoService;

@RestController
@RequestMapping("/api/catalogos/marcas")
public class MarcaVehiculoController extends AbstractNombreController {

    public MarcaVehiculoController(MarcaVehiculoService service) {
        super(service, "Marca");
    }
}