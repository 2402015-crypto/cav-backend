package mx.hotel.backend_cav.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.EstadisticasDiaResponse;
import mx.hotel.backend_cav.dto.DashboardOperativoResponse;
import mx.hotel.backend_cav.dto.MovimientoAccesoResponse;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;

@Service
public class EstadisticasAccesoService {

    private final RegistroAccesoRepository registroRepository;
    private final ApplicationTimeService timeService;

    public EstadisticasAccesoService(RegistroAccesoRepository registroRepository, ApplicationTimeService timeService) {
        this.registroRepository = registroRepository;
        this.timeService = timeService;
    }

    @Transactional(readOnly = true)
    public EstadisticasDiaResponse obtenerHoy() {
        LocalDate hoy = timeService.today();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        long salidas = registroRepository.countByFechaSalidaGreaterThanEqualAndFechaSalidaLessThan(inicio, fin);
        long entradas = registroRepository.countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThan(inicio, fin);
        long registros = entradas + salidas;
        return new EstadisticasDiaResponse(hoy, registros, entradas, salidas);
    }

    @Transactional(readOnly = true)
    public DashboardOperativoResponse obtenerDashboard() {
        LocalDate hoy = timeService.today();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        long entradas = registroRepository.countByFechaEntradaGreaterThanEqualAndFechaEntradaLessThan(inicio, fin);
        long salidas = registroRepository.countByFechaSalidaGreaterThanEqualAndFechaSalidaLessThan(inicio, fin);
        List<MovimientoAccesoResponse> eventos = new ArrayList<>();
        registroRepository.buscarMovimientosDelDia(inicio, fin).forEach(registro -> {
            String conductor = registro.getConductor() == null ? null : registro.getConductor().getNombre();
            String tipo = registro.getTipoVisitante() == null ? null : registro.getTipoVisitante().getNombre();
            String empresa = registro.getEmpresa() == null ? null : registro.getEmpresa().getNombre();
            String transportadora = registro.getTransportadora() == null ? null : registro.getTransportadora().getNombre();
            if (!registro.getFechaEntrada().isBefore(inicio) && registro.getFechaEntrada().isBefore(fin)) {
                eventos.add(new MovimientoAccesoResponse(registro.getId(), registro.getPlacaRegistrada(),
                        conductor, tipo, empresa, transportadora, registro.getFechaEntrada(), false));
            }
            if (registro.getFechaSalida() != null
                    && !registro.getFechaSalida().isBefore(inicio) && registro.getFechaSalida().isBefore(fin)) {
                eventos.add(new MovimientoAccesoResponse(registro.getId(), registro.getPlacaRegistrada(),
                        conductor, tipo, empresa, transportadora, registro.getFechaSalida(), true));
            }
        });
        eventos.sort(java.util.Comparator.comparing(MovimientoAccesoResponse::fecha).reversed());
        List<MovimientoAccesoResponse> movimientos = eventos;
        return new DashboardOperativoResponse(hoy, entradas + salidas, entradas, salidas,
                registroRepository.countByFechaSalidaIsNull(), movimientos);
    }
}