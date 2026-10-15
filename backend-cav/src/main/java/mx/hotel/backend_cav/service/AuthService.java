package mx.hotel.backend_cav.service;

import java.time.LocalDateTime;

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
        Usuario usuario = usuarioRepository.findByUsuarioIgnoreCase(request.usuario().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        boolean passwordValida = passwordEncoder.matches(request.contrasena(), usuario.getContrasena());
        if (usuario.getEstado() != EstadoUsuario.ACTIVO || !passwordValida) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        String token = jwtService.generateToken(usuario);
        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getRol().getNombre());
    }
}
