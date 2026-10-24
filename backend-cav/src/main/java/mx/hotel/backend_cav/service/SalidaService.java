package mx.hotel.backend_cav.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.SalidaLookupResponse;
import mx.hotel.backend_cav.dto.SalidaRequest;
import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.exception.ConflictException;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;

@Service
public class SalidaService {

    private final RegistroAccesoRepository registroRepository;
    private final ApplicationTimeService timeService;

    public SalidaService(RegistroAccesoRepository registroRepository, ApplicationTimeService timeService) {
        this.registroRepository = registroRepository;
        this.timeService = timeService;
    }

    @Transactional(readOnly = true)
    public SalidaLookupResponse buscar(String placa) {
        String valor = placa == null ? "" : placa.trim();
        return registroRepository.findTopByPlacaRegistradaIgnoreCaseAndFechaSalidaIsNullOrderByFechaEntradaDesc(valor)
                .map(this::mapear)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una entrada activa para esa placa"));
    }

    @Transactional
    public SalidaLookupResponse registrar(Integer id, SalidaRequest request) {
        RegistroAcceso registro = registroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de entrada no encontrado"));
        if (registro.getFechaSalida() != null) {
            throw new ConflictException("La salida de este vehículo ya fue registrada");
        }
        registro.setFechaSalida(timeService.now());
        registro.setObservaciones(request == null || request.observaciones() == null
                ? registro.getObservaciones() : request.observaciones().trim());
        return mapear(registroRepository.save(registro));
    }

    private SalidaLookupResponse mapear(RegistroAcceso registro) {
        long minutos = Math.max(0, Duration.between(registro.getFechaEntrada(),
                registro.getFechaSalida() == null ? timeService.now() : registro.getFechaSalida()).toMinutes());
        String tipo = registro.getTipoVisitante() == null ? null : registro.getTipoVisitante().getNombre();
        return new SalidaLookupResponse(registro.getId(), registro.getPlacaRegistrada(),
                registro.getConductor() == null ? null : registro.getConductor().getNombre(), tipo,
                registro.getEmpresa() == null ? null : registro.getEmpresa().getNombre(),
                registro.getTransportadora() == null ? null : registro.getTransportadora().getNombre(),
                registro.getVehiculo() == null || registro.getVehiculo().getMarca() == null ? null : registro.getVehiculo().getMarca().getNombre(),
                registro.getVehiculo() == null || registro.getVehiculo().getColor() == null ? null : registro.getVehiculo().getColor().getNombre(),
                registro.getFechaEntrada(), registro.getFechaSalida(), registro.getObservaciones(), minutos, formatear(minutos));
    }

    private String formatear(long minutos) {
        return (minutos / 60) + "h " + (minutos % 60) + "m";
    }
}