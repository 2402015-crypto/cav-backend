package mx.hotel.backend_cav.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.hotel.backend_cav.service.TransportadoraService;

@RestController
@RequestMapping("/api/transportadoras")
public class TransportadoraController extends AbstractNombreController {

    public TransportadoraController(TransportadoraService service) {
        super(service, "Transportadora");
    }
}