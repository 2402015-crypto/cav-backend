package mx.hotel.backend_cav.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.DashboardAdminResponse;
import mx.hotel.backend_cav.dto.DashboardAdminResponse.PuntoSerie;
import mx.hotel.backend_cav.dto.DashboardAdminResponse.Ranking;
import mx.hotel.backend_cav.dto.DashboardAdminResponse.Resumen;
import mx.hotel.backend_cav.entity.EstadoUsuario;
import mx.hotel.backend_cav.entity.Nombrable;
import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.repository.DashboardAdminRepository;
import mx.hotel.backend_cav.repository.EmpresaRepository;
import mx.hotel.backend_cav.repository.MetricasOcrRepository;
import mx.hotel.backend_cav.repository.TransportadoraRepository;
import mx.hotel.backend_cav.repository.UsuarioRepository;

@Service
public class DashboardAdminService {

    private static final Locale LOCALE = Locale.forLanguageTag("es-MX");

    private final DashboardAdminRepository registroRepository;
    private final MetricasOcrRepository metricasRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final TransportadoraRepository transportadoraRepository;
    private final ApplicationTimeService timeService;

    public DashboardAdminService(
            DashboardAdminRepository registroRepository,
            MetricasOcrRepository metricasRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            TransportadoraRepository transportadoraRepository,
            ApplicationTimeService timeService) {
        this.registroRepository = registroRepository;
        this.metricasRepository = metricasRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.transportadoraRepository = transportadoraRepository;
        this.timeService = timeService;
    }

    @Transactional(readOnly = true)
    public DashboardAdminResponse obtener(int dias) {
        int periodo = dias == 30 ? 30 : 7;
        LocalDate hoy = timeService.today();
        LocalDate inicioFecha = hoy.minusDays(periodo - 1L);
        LocalDateTime inicio = inicioFecha.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        List<RegistroAcceso> registros = registroRepository.findForDashboard(inicio, fin);
        List<mx.hotel.backend_cav.entity.MetricasOcr> metricas = metricasRepository
                .findAllByFechaBetweenOrderByFechaAsc(inicioFecha, hoy);

        long entradasHoy = registros.stream().filter(r -> r.getFechaEntrada().toLocalDate().equals(hoy)).count();
        long salidasHoy = registros.stream()
                .filter(r -> r.getFechaSalida() != null && r.getFechaSalida().toLocalDate().equals(hoy)).count();
        long activos = registroRepository.countByFechaSalidaIsNull();
        long usuariosActivos = usuarioRepository.countByEstado(EstadoUsuario.ACTIVO);
        long procesadas = metricas.stream().mapToLong(m -> m.getImagenesProcesadas()).sum();
        long detectadas = metricas.stream().mapToLong(m -> m.getDetectadas()).sum();
        BigDecimal precision = porcentaje(detectadas, procesadas);

        return new DashboardAdminResponse(
                hoy,
                new Resumen(entradasHoy + salidasHoy, activos, entradasHoy, salidasHoy,
                        empresaRepository.count() + transportadoraRepository.count(), precision,
                        usuariosActivos == 0 ? BigDecimal.ZERO
                                : BigDecimal.valueOf((entradasHoy + salidasHoy) / (double) usuariosActivos)
                                        .setScale(1, RoundingMode.HALF_UP)),
                agruparEntradas(registros, inicioFecha, periodo),
                agruparOcr(metricas, inicioFecha, periodo),
                ranking(registros, registro -> registro.getEmpresa(), true),
                ranking(registros, registro -> registro.getTransportadora(), false));
    }

    private List<PuntoSerie> agruparEntradas(List<RegistroAcceso> registros, LocalDate inicio, int dias) {
        Map<LocalDate, Long> valores = registros.stream().collect(Collectors.groupingBy(
                r -> r.getFechaEntrada().toLocalDate(), LinkedHashMap::new, Collectors.counting()));
        if (dias == 7) {
            return inicio.datesUntil(inicio.plusDays(7))
                    .map(fecha -> new PuntoSerie(fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, LOCALE),
                            BigDecimal.valueOf(valores.getOrDefault(fecha, 0L))))
                    .toList();
        }
        return agruparSemanas(inicio, valores);
    }

    private List<PuntoSerie> agruparOcr(List<mx.hotel.backend_cav.entity.MetricasOcr> metricas,
            LocalDate inicio, int dias) {
        Map<LocalDate, BigDecimal> valores = metricas.stream().collect(Collectors.toMap(
                metrica -> metrica.getFecha(),
                metrica -> metrica.getPrecisionOcr() == null ? BigDecimal.ZERO : metrica.getPrecisionOcr(),
                (a, b) -> b));
        if (dias == 7) {
            return inicio.datesUntil(inicio.plusDays(7))
                    .map(fecha -> new PuntoSerie(fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, LOCALE),
                            valores.getOrDefault(fecha, BigDecimal.ZERO)))
                    .toList();
        }
        Map<Integer, List<BigDecimal>> semanas = new LinkedHashMap<>();
        inicio.datesUntil(inicio.plusDays(30)).forEach(fecha ->
                semanas.computeIfAbsent((int) (java.time.temporal.ChronoUnit.DAYS.between(inicio, fecha) * 4 / 30) + 1,
                        ignored -> new ArrayList<>()).add(valores.getOrDefault(fecha, BigDecimal.ZERO)));
        return semanas.entrySet().stream()
                .map(entry -> new PuntoSerie("Semana " + entry.getKey(),
                        entry.getValue().stream().reduce(BigDecimal.ZERO, (total, valor) -> total.add(valor))
                                .divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP)))
                .toList();
    }

    private List<PuntoSerie> agruparSemanas(LocalDate inicio, Map<LocalDate, Long> valores) {
        Map<Integer, Long> semanas = new LinkedHashMap<>();
        inicio.datesUntil(inicio.plusDays(30)).forEach(fecha ->
                semanas.merge((int) (java.time.temporal.ChronoUnit.DAYS.between(inicio, fecha) * 4 / 30) + 1,
                        valores.getOrDefault(fecha, 0L), (total, valor) -> total + valor));
        return semanas.entrySet().stream()
                .map(entry -> new PuntoSerie("Semana " + entry.getKey(), BigDecimal.valueOf(entry.getValue())))
                .toList();
    }

    private List<Ranking> ranking(List<RegistroAcceso> registros,
            Function<RegistroAcceso, Nombrable> extractor,
            boolean minimum) {
        Map<String, Long> valores = registros.stream().map(extractor)
                .filter(value -> value != null)
                .collect(Collectors.groupingBy(value -> value.getNombre(),
                        Collectors.counting()));
        return valores.entrySet().stream()
                .filter(entry -> !minimum || entry.getValue() > 3)
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new Ranking(entry.getKey(), entry.getValue()))
                .toList();
    }

    private BigDecimal porcentaje(long parte, long total) {
        return total == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(parte * 100.0 / total).setScale(2, RoundingMode.HALF_UP);
    }
}
