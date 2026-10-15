package mx.hotel.backend_cav.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import mx.hotel.backend_cav.security.CustomUserDetailsService;
import mx.hotel.backend_cav.service.JwtService;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private CustomUserDetailsService customUserDetailsService;

    @Test
    void returnsBackendStatus() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "status": "ok",
                          "message": "Backend funcionando correctamente"
                        }
                        """));
    }

}