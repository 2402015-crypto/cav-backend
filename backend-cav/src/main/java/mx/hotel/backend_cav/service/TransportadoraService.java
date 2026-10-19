package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.Transportadora;
import mx.hotel.backend_cav.repository.TransportadoraRepository;

@Service
public class TransportadoraService extends AbstractNombreService<Transportadora> {

    public TransportadoraService(TransportadoraRepository repository) {
        super(repository, Transportadora::new, "Transportadora");
    }
}