package mx.hotel.backend_cav.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.EstadisticasDiaResponse;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;

@Service
public class EstadisticasAccesoService {

    private final RegistroAccesoRepository registroRepository;

    public EstadisticasAccesoService(RegistroAccesoRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public EstadisticasDiaResponse obtenerHoy() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        long salidas = registroRepository.countByFechaSalidaGreaterThanEqualAndFechaSalidaLessThan(inicio, fin);
        long entradas = registroRepository.countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThanAndFechaSalidaIsNull(inicio, fin);
        long registros = entradas + salidas;
        return new EstadisticasDiaResponse(hoy, registros, entradas, salidas);
    }
}