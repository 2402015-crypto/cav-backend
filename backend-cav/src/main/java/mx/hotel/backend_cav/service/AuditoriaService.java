package mx.hotel.backend_cav.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.AuditoriaResponse;
import mx.hotel.backend_cav.entity.Auditoria;
import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.AuditoriaRepository;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;
import mx.hotel.backend_cav.repository.UsuarioRepository;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RegistroAccesoRepository registroRepository;
    public AuditoriaService(AuditoriaRepository auditoriaRepository, UsuarioRepository usuarioRepository,
            RegistroAccesoRepository registroRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listar(String placa, Integer usuario, String accion,
            LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? null : hasta.plusDays(1).atStartOfDay();
        return auditoriaRepository.buscar(normalizar(placa), usuario, normalizar(accion), inicio, fin)
                .stream().map(this::mapear).toList();
    }

    @Transactional(readOnly = true)
    public AuditoriaResponse detalle(Integer id) {
        return auditoriaRepository.findById(id).map(this::mapear)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de bitácora no encontrado"));
    }

    @Transactional
    public void exportar(UserDetails userDetails, String descripcion) {
        registrar(userDetails, "EXPORTO_REPORTE", descripcion, null, null);
    }

    @Transactional
    public void registrar(UserDetails userDetails, String accion, String descripcion,
            Integer registroId, String placa) {
        Usuario usuario = usuarioRepository.findByUsuarioIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAccion(accion);
        auditoria.setDescripcion(descripcion);
        auditoria.setPlaca(placa);
        if (registroId != null) {
            RegistroAcceso registro = registroRepository.findById(registroId)
                    .orElseThrow(() -> new ResourceNotFoundException("Registro de acceso no encontrado"));
            auditoria.setRegistro(registro);
        }
        auditoriaRepository.save(auditoria);
    }

    private AuditoriaResponse mapear(Auditoria auditoria) {
        return new AuditoriaResponse(auditoria.getId(),
                auditoria.getRegistro() == null ? null : auditoria.getRegistro().getId(),
                auditoria.getUsuario().getUsuario(), auditoria.getAccion(),
                auditoria.getDescripcion(), auditoria.getPlaca(), auditoria.getFecha());
    }

    private String normalizar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
