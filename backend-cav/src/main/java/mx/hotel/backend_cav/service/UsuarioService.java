package mx.hotel.backend_cav.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.UsuarioRequest;
import mx.hotel.backend_cav.dto.UsuarioResponse;
import mx.hotel.backend_cav.entity.EstadoUsuario;
import mx.hotel.backend_cav.entity.Rol;
import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.exception.BadRequestException;
import mx.hotel.backend_cav.exception.ConflictException;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.RolRepository;
import mx.hotel.backend_cav.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByIdAsc().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Integer id) {
        return UsuarioResponse.from(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        String nombreUsuario = normalizarUsuario(request.usuario());
        if (request.contrasena() == null || request.contrasena().isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria al crear un usuario");
        }
        validarUsuarioDisponible(nombreUsuario, null);

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setUsuario(nombreUsuario);
        usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setRol(buscarRol(request.rol()));
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        String nombreUsuario = normalizarUsuario(request.usuario());
        validarUsuarioDisponible(nombreUsuario, id);

        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setUsuario(nombreUsuario);
        usuario.setRol(buscarRol(request.rol()));
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        }
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public void desactivar(Integer id) {
        Usuario usuario = buscar(id);
        usuario.setEstado(EstadoUsuario.INACTIVO);
    }

    @Transactional
    public void activar(Integer id) {
        Usuario usuario = buscar(id);
        usuario.setEstado(EstadoUsuario.ACTIVO);
    }

    private Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private Rol buscarRol(String nombre) {
        String rol = nombre.trim().toUpperCase(Locale.ROOT);
        if (!rol.equals("ADMIN") && !rol.equals("OPERATIVO")) {
            throw new ResourceNotFoundException("Rol inválido");
        }
        return rolRepository.findByNombre(rol)
                .orElseThrow(() -> new ResourceNotFoundException("El rol no está configurado"));
    }

    private void validarUsuarioDisponible(String usuario, Integer idActual) {
        usuarioRepository.findByUsuarioIgnoreCase(usuario).ifPresent(existente -> {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new ConflictException("El usuario ya está registrado");
            }
        });
    }

    private String normalizarUsuario(String usuario) {
        return usuario.trim();
    }
}