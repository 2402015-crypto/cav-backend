package mx.hotel.backend_cav.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.LoginRequest;
import mx.hotel.backend_cav.dto.LoginResponse;
import mx.hotel.backend_cav.entity.EstadoUsuario;
import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String nombreUsuario = request.usuario().trim();
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsuarioIgnoreCase(nombreUsuario);
        if (usuarioEncontrado.isEmpty()) {
            LOGGER.warn("Intento de inicio de sesión rechazado: usuario no encontrado [{}]", nombreUsuario);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Usuario usuario = usuarioEncontrado.get();
        boolean passwordValida = passwordEncoder.matches(request.contrasena(), usuario.getContrasena());
        if (usuario.getEstado() != EstadoUsuario.ACTIVO || !passwordValida) {
            LOGGER.warn("Intento de inicio de sesión rechazado para [{}]: estado={}, contraseñaValida={}",
                    nombreUsuario, usuario.getEstado(), passwordValida);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        String token = jwtService.generateToken(usuario);
        LOGGER.info("Inicio de sesión exitoso para [{}]", nombreUsuario);
        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getRol().getNombre());
    }
}
