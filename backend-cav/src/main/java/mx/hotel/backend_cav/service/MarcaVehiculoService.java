package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.MarcaVehiculo;
import mx.hotel.backend_cav.repository.MarcaVehiculoRepository;

@Service
public class MarcaVehiculoService extends AbstractNombreService<MarcaVehiculo> {

    public MarcaVehiculoService(MarcaVehiculoRepository repository) {
        super(repository, MarcaVehiculo::new, "Marca");
    }
}