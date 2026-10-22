package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.Transportadora;
import mx.hotel.backend_cav.repository.TransportadoraRepository;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;

@Service
public class TransportadoraService extends AbstractNombreService<Transportadora> {

    private final RegistroAccesoRepository registroRepository;

    public TransportadoraService(TransportadoraRepository repository, RegistroAccesoRepository registroRepository) {
        super(repository, Transportadora::new, "Transportadora");
        this.registroRepository = registroRepository;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void eliminar(Integer id) {
        registroRepository.desvincularTransportadora(id);
        super.eliminar(id);
    }
}