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
    private static final long ACCESOS_MINIMOS_PROVEEDOR = 2L;

    private final DashboardAdminRepository registroRepository;
    private final MetricasOcrRepository metricasRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final TransportadoraRepository transportadoraRepository;
    private final ApplicationTimeService timeService;
    private final MetricasOcrService metricasOcrService;

    public DashboardAdminService(
            DashboardAdminRepository registroRepository,
            MetricasOcrRepository metricasRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            TransportadoraRepository transportadoraRepository,
            ApplicationTimeService timeService,
            MetricasOcrService metricasOcrService) {
        this.registroRepository = registroRepository;
        this.metricasRepository = metricasRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.transportadoraRepository = transportadoraRepository;
        this.timeService = timeService;
        this.metricasOcrService = metricasOcrService;
    }

    @Transactional
    public DashboardAdminResponse obtener(int dias) {
        int periodo = dias == 30 ? 30 : 7;
        LocalDate hoy = timeService.today();
        LocalDate inicioFecha = hoy.minusDays(periodo - 1L);
        LocalDateTime inicio = inicioFecha.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        inicioFecha.datesUntil(hoy.plusDays(1)).forEach(metricasOcrService::recalcular);
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
                ranking(registros, registro -> registro.getEmpresa(), true,
                        Integer.MAX_VALUE),
                ranking(registros, registro -> registro.getTransportadora(), false,
                        Integer.MAX_VALUE));
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
        Map<Integer, List<mx.hotel.backend_cav.entity.MetricasOcr>> semanas = new LinkedHashMap<>();
        Map<LocalDate, mx.hotel.backend_cav.entity.MetricasOcr> metricasPorFecha = metricas.stream()
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toMap(
                        metrica -> metrica.getFecha(),
                        Function.identity(),
                        (primera, segunda) -> segunda));
        inicio.datesUntil(inicio.plusDays(30)).forEach(fecha -> {
            int semana = (int) (java.time.temporal.ChronoUnit.DAYS.between(inicio, fecha) / 7) + 1;
            mx.hotel.backend_cav.entity.MetricasOcr metrica = metricasPorFecha.get(fecha);
            if (metrica != null && metrica.getImagenesProcesadas() > 0) {
                semanas.computeIfAbsent(semana, ignored -> new ArrayList<>()).add(metrica);
            } else {
                semanas.putIfAbsent(semana, new ArrayList<>());
            }
        });
        return semanas.entrySet().stream()
                .map(entry -> new PuntoSerie("Semana " + entry.getKey(),
                        promedioPrecisionSemana(entry.getValue())))
                .toList();
    }

    private BigDecimal promedioPrecisionSemana(List<mx.hotel.backend_cav.entity.MetricasOcr> metricasSemana) {
        BigDecimal total = BigDecimal.ZERO;
        int diasConPrecision = 0;
        for (mx.hotel.backend_cav.entity.MetricasOcr metrica : metricasSemana) {
            BigDecimal precisionDia = metrica.getPrecisionOcr();
            if (precisionDia != null) {
                total = total.add(precisionDia);
                diasConPrecision++;
            }
        }
        return diasConPrecision == 0 ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(diasConPrecision), 2, RoundingMode.HALF_UP);
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
            boolean minimum, int maxResults) {
        Map<String, Long> valores = registros.stream().map(extractor)
                .filter(value -> value != null)
                .collect(Collectors.groupingBy(value -> value.getNombre(),
                        Collectors.counting()));
        return valores.entrySet().stream()
                .filter(entry -> !minimum || entry.getValue() >= ACCESOS_MINIMOS_PROVEEDOR)
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(maxResults)
                .map(entry -> new Ranking(entry.getKey(), entry.getValue()))
                .toList();
    }

    private BigDecimal porcentaje(long parte, long total) {
        return total == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(parte * 100.0 / total).setScale(2, RoundingMode.HALF_UP);
    }
}
