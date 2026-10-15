package mx.hotel.backend_cav.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import mx.hotel.backend_cav.entity.EstadoUsuario;
import mx.hotel.backend_cav.entity.Rol;
import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.repository.RolRepository;
import mx.hotel.backend_cav.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararDatos() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        Rol admin = new Rol();
        admin.setNombre("ADMIN");
        admin.setDescripcion("Administrador");
        admin = rolRepository.save(admin);

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto("Administrador de pruebas");
        usuario.setUsuario("admin");
        usuario.setContrasena(passwordEncoder.encode("Password123!"));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setRol(admin);
        usuarioRepository.save(usuario);
    }

    @Test
    void loginDevuelveJwtYDatosDelUsuario() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"contrasena\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.rol").value("ADMIN"));
    }

    @Test
    void rutaDeUsuariosRechazaSolicitudSinToken() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rutaDeUsuariosAceptaTokenValido() throws Exception {
        String respuestaLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"admin\",\"contrasena\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(respuestaLogin, "$.data.token");

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

            @Test
            void activaUsuarioInactivo() throws Exception {
            Usuario usuarioInactivo = new Usuario();
            usuarioInactivo.setNombreCompleto("Usuario inactivo");
            usuarioInactivo.setUsuario("inactivo");
            usuarioInactivo.setContrasena(passwordEncoder.encode("Password123!"));
            usuarioInactivo.setEstado(EstadoUsuario.INACTIVO);
            usuarioInactivo.setRol(rolRepository.findByNombre("ADMIN").orElseThrow());
            usuarioInactivo = usuarioRepository.save(usuarioInactivo);

            String respuestaLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"usuario\":\"admin\",\"contrasena\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
            String token = JsonPath.read(respuestaLogin, "$.data.token");

            mockMvc.perform(put("/api/usuarios/{id}/activar", usuarioInactivo.getId())
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario activado"));

            org.assertj.core.api.Assertions.assertThat(usuarioRepository.findById(usuarioInactivo.getId()).orElseThrow()
                .getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
            }
}