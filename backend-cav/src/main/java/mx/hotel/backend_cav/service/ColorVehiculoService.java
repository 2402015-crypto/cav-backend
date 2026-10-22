package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.ColorVehiculo;
import mx.hotel.backend_cav.repository.ColorVehiculoRepository;
import mx.hotel.backend_cav.repository.VehiculoRepository;

@Service
public class ColorVehiculoService extends AbstractNombreService<ColorVehiculo> {

    private final VehiculoRepository vehiculoRepository;

    public ColorVehiculoService(ColorVehiculoRepository repository, VehiculoRepository vehiculoRepository) {
        super(repository, ColorVehiculo::new, "Color");
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void eliminar(Integer id) {
        vehiculoRepository.desvincularColor(id);
        super.eliminar(id);
    }
}