package mx.hotel.backend_cav.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.hotel.backend_cav.service.ColorVehiculoService;

@RestController
@RequestMapping("/api/catalogos/colores")
public class ColorVehiculoController extends AbstractNombreController {

    public ColorVehiculoController(ColorVehiculoService service) {
        super(service, "Color");
    }
}