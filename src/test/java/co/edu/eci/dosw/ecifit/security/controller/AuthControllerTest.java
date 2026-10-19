package co.edu.eci.dosw.ecifit.security.controller;

import co.edu.eci.dosw.ecifit.security.dto.request.LoginRequestDTO;
import co.edu.eci.dosw.ecifit.security.dto.response.AuthResponseDTO;
import co.edu.eci.dosw.ecifit.security.service.IAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Pruebas Unitarias para AuthController (Patrón AAA)")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuthService authService;

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 200 OK con token cuando las credenciales son válidas")
    void debeRetornar200ConTokenCuandoCredencialesSonValidas() throws Exception {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("admin@mail.escuelaing.edu.co", "Admin123*");
        AuthResponseDTO responseDTO = new AuthResponseDTO(
                "eyJhbGciOiJIUzI1NiJ9.fakeToken",
                "Bearer",
                "admin@mail.escuelaing.edu.co",
                "ADMINISTRADOR",
                86400000
        );
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9.fakeToken"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("admin@mail.escuelaing.edu.co"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.expiresIn").value(86400000));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 401 Unauthorized cuando las credenciales son incorrectas")
    void debeRetornar401CuandoCredencialesSonIncorrectas() throws Exception {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("admin@mail.escuelaing.edu.co", "PasswordIncorrecto");
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 400 Bad Request cuando el email o password son inválidos")
    void debeRetornar400CuandoPayloadEsInvalido() throws Exception {
        // Arrange
        LoginRequestDTO invalidRequest = new LoginRequestDTO("correo-invalido", "");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
