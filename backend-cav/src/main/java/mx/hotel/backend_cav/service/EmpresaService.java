package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.Empresa;
import mx.hotel.backend_cav.repository.EmpresaRepository;

@Service
public class EmpresaService extends AbstractNombreService<Empresa> {

    public EmpresaService(EmpresaRepository repository) {
        super(repository, Empresa::new, "Empresa");
    }
}