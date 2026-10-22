package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.MarcaVehiculo;
import mx.hotel.backend_cav.repository.MarcaVehiculoRepository;
import mx.hotel.backend_cav.repository.VehiculoRepository;

@Service
public class MarcaVehiculoService extends AbstractNombreService<MarcaVehiculo> {

    private final VehiculoRepository vehiculoRepository;

    public MarcaVehiculoService(MarcaVehiculoRepository repository, VehiculoRepository vehiculoRepository) {
        super(repository, MarcaVehiculo::new, "Marca");
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void eliminar(Integer id) {
        vehiculoRepository.desvincularMarca(id);
        super.eliminar(id);
    }
}