package mx.hotel.backend_cav.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.entity.OcrHistorial;
import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.OcrHistorialRepository;
import mx.hotel.backend_cav.repository.UsuarioRepository;

@Service
public class OcrHistorialService {

    private final OcrHistorialRepository historialRepository;
    private final UsuarioRepository usuarioRepository;
    private final MetricasOcrService metricasService;

    public OcrHistorialService(OcrHistorialRepository historialRepository,
            UsuarioRepository usuarioRepository, MetricasOcrService metricasService) {
        this.historialRepository = historialRepository;
        this.usuarioRepository = usuarioRepository;
        this.metricasService = metricasService;
    }

    @Transactional
    public OcrHistorial guardar(OcrHistorial historial) {
        OcrHistorial guardado = historialRepository.save(historial);
        metricasService.registrarProcesamiento(guardado.getFecha());
        return guardado;
    }

    @Transactional
    public void registrarCorreccion(Integer idOcr, String placaFinal, String nombreUsuario) {
        if (idOcr == null || placaFinal == null || placaFinal.isBlank()) {
            return;
        }
        OcrHistorial historial = historialRepository.findById(idOcr)
                .orElseThrow(() -> new ResourceNotFoundException("Historial OCR no encontrado"));
        String detectada = historial.getPlacaDetectada();
        String normalizada = placaFinal.trim().toUpperCase(Locale.ROOT);
        if (detectada != null && detectada.trim().equalsIgnoreCase(normalizada)) {
            return;
        }
        Usuario usuario = usuarioRepository.findByUsuarioIgnoreCase(nombreUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        historial.marcarCorreccion(normalizada, usuario);
        historialRepository.save(historial);
        metricasService.registrarCorreccion(historial.getFecha());
    }
}
