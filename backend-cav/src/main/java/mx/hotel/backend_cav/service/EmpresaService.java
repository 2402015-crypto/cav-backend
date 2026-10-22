package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.Empresa;
import mx.hotel.backend_cav.repository.EmpresaRepository;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;

@Service
public class EmpresaService extends AbstractNombreService<Empresa> {

    private final RegistroAccesoRepository registroRepository;

    public EmpresaService(EmpresaRepository repository, RegistroAccesoRepository registroRepository) {
        super(repository, Empresa::new, "Empresa");
        this.registroRepository = registroRepository;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void eliminar(Integer id) {
        registroRepository.desvincularEmpresa(id);
        super.eliminar(id);
    }
}