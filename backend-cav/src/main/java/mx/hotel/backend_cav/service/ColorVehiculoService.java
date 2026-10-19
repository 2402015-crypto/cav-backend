package mx.hotel.backend_cav.service;

import org.springframework.stereotype.Service;

import mx.hotel.backend_cav.entity.ColorVehiculo;
import mx.hotel.backend_cav.repository.ColorVehiculoRepository;

@Service
public class ColorVehiculoService extends AbstractNombreService<ColorVehiculo> {

    public ColorVehiculoService(ColorVehiculoRepository repository) {
        super(repository, ColorVehiculo::new, "Color");
    }
}