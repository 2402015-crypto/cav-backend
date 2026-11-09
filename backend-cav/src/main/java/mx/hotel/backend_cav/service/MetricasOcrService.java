package mx.hotel.backend_cav.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.CorreccionesUsuarioResponse;
import mx.hotel.backend_cav.dto.DesempenoOcrResponse;
import mx.hotel.backend_cav.dto.MetricasOcrDiaResponse;
import mx.hotel.backend_cav.dto.MetricasOcrResponse;
import mx.hotel.backend_cav.entity.MetricasOcr;
import mx.hotel.backend_cav.repository.MetricasOcrRepository;
import mx.hotel.backend_cav.repository.OcrHistorialRepository;

@Service
public class MetricasOcrService {

    private final MetricasOcrRepository metricasRepository;
    private final OcrHistorialRepository historialRepository;
    private final ApplicationTimeService timeService;

    public MetricasOcrService(MetricasOcrRepository metricasRepository,
            OcrHistorialRepository historialRepository, ApplicationTimeService timeService) {
        this.metricasRepository = metricasRepository;
        this.historialRepository = historialRepository;
        this.timeService = timeService;
    }

    @Transactional
    public void registrarProcesamiento(LocalDateTime fecha) {
        recalcular(fecha.toLocalDate());
    }

    @Transactional
    public void registrarCorreccion(LocalDateTime fecha) {
        recalcular(fecha.toLocalDate());
    }

    @Transactional
    public MetricasOcrResponse hoy() {
        MetricasOcr metricas = recalcularLectura(timeService.today());
        return respuesta(metricas);
    }

    @Transactional(readOnly = true)
    public List<MetricasOcrDiaResponse> ultimos30Dias() {
        LocalDate fin = timeService.today();
        return metricasRepository.findAllByFechaBetweenOrderByFechaAsc(fin.minusDays(29), fin)
                .stream().map(this::respuestaDia).toList();
    }

    @Transactional
    public DesempenoOcrResponse desempeno() {
        LocalDate hoy = timeService.today();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        MetricasOcr metricas = recalcularLectura(hoy);
        Double promedioTiempo = historialRepository.promedioTiempo(inicio, fin);
        int totalCorrecciones = metricasRepository.findAllByFechaBetweenOrderByFechaAsc(hoy.minusDays(29), hoy)
                .stream().mapToInt(metricasDia -> metricasDia.getCorreccionesManuales()).sum();
        BigDecimal promedioCorrecciones = BigDecimal.valueOf(totalCorrecciones / 30.0)
                .setScale(2, RoundingMode.HALF_UP);
        return new DesempenoOcrResponse(metricas.getPrecisionOcr(), promedioCorrecciones,
                promedioTiempo == null ? null : (int) Math.round(promedioTiempo));
    }

    @Transactional(readOnly = true)
    public List<CorreccionesUsuarioResponse> correccionesPorUsuario() {
        LocalDate hoy = timeService.today();
        return historialRepository.correccionesPorUsuario(hoy.minusDays(29).atStartOfDay(),
                        hoy.plusDays(1).atStartOfDay()).stream()
                .map(row -> new CorreccionesUsuarioResponse((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }

    @Transactional
    public void recalcular(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        long procesadas = historialRepository.countByFechaGreaterThanEqualAndFechaLessThan(inicio, fin);
        long detectadas = historialRepository.countDetectadas(inicio, fin);
        Double promedio = historialRepository.promedioTiempo(inicio, fin);
        MetricasOcr metricas = metricasRepository.findByFecha(fecha).orElseGet(MetricasOcr::new);
        metricas.setFecha(fecha);
        metricas.setImagenesProcesadas((int) procesadas);
        metricas.setDetectadas((int) detectadas);
        metricas.setFallidas((int) (procesadas - detectadas));
        metricas.setCorreccionesManuales((int) historialRepository
                .countByFechaGreaterThanEqualAndFechaLessThanAndCorregidaManualTrue(inicio, fin));
        metricas.setPrecisionOcr(procesadas == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(detectadas * 100.0 / procesadas).setScale(2, RoundingMode.HALF_UP));
        metricas.setTiempoPromedioMs(promedio == null ? null : (int) Math.round(promedio));
        metricasRepository.save(metricas);
    }

    private MetricasOcr recalcularLectura(LocalDate fecha) {
        MetricasOcr metricas = metricasRepository.findByFecha(fecha).orElse(null);
        if (metricas == null) {
            recalcular(fecha);
            metricas = metricasRepository.findByFecha(fecha).orElseThrow();
        }
        return metricas;
    }

    private MetricasOcrResponse respuesta(MetricasOcr metricas) {
        return new MetricasOcrResponse(metricas.getImagenesProcesadas(), metricas.getDetectadas(),
                metricas.getFallidas(), metricas.getCorreccionesManuales(), metricas.getPrecisionOcr(),
                metricas.getTiempoPromedioMs());
    }

    private MetricasOcrDiaResponse respuestaDia(MetricasOcr metricas) {
        return new MetricasOcrDiaResponse(metricas.getFecha(), metricas.getImagenesProcesadas(),
                metricas.getDetectadas(), metricas.getFallidas(), metricas.getCorreccionesManuales(),
                metricas.getPrecisionOcr(), metricas.getTiempoPromedioMs());
    }
}
