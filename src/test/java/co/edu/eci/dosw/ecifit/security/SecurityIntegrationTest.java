package co.edu.eci.dosw.ecifit.security;

import co.edu.eci.dosw.ecifit.security.dto.request.LoginRequestDTO;
import co.edu.eci.dosw.ecifit.security.enums.Rol;
import co.edu.eci.dosw.ecifit.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración de Seguridad (Sprint 3 - Patrón AAA)")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /api/v1/test/publico debe retornar 200 OK de forma anónima")
    void debePermitirAccesoARutaPublica() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/test/publico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Endpoint público accesible"));
    }

    @Test
    @DisplayName("GET /api-docs debe responder de forma pública sin requerir autenticación")
    void debePermitirAccesoPublicoAOpenApiDocs() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/test/protegido sin token debe retornar 401 Unauthorized estructurado")
    void debeRechazarAccesoProtegidoSinToken() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/test/protegido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").value("No autenticado o token inválido"));
    }

    @Test
    @DisplayName("GET /api/v1/test/admin con rol ESTUDIANTE debe retornar 403 Forbidden estructurado")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debeRechazarAccesoAdminConRolEstudiante() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("GET /api/v1/test/admin con rol ADMINISTRADOR debe retornar 200 OK")
    @WithMockUser(username = "admin@mail.escuelaing.edu.co", roles = {"ADMINISTRADOR"})
    void debePermitirAccesoAdminConRolAdecuado() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/test/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Acceso concedido a Administrador"));
    }

    @Test
    @DisplayName("GET /api/v1/test/protegido con Bearer Token válido debe retornar 200 OK")
    void debePermitirAccesoProtegidoConBearerTokenValido() throws Exception {
        // Arrange
        String token = jwtUtil.generateToken("admin@mail.escuelaing.edu.co", Rol.ADMINISTRADOR);

        // Act & Assert
        mockMvc.perform(get("/api/v1/test/protegido")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Endpoint protegido accesible para autenticados"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login con credenciales válidas sembradas debe retornar 200 y token")
    void debeIniciarSesionExitosamenteConUsuarioSembrado() throws Exception {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("admin@mail.escuelaing.edu.co", "Admin123*");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("admin@mail.escuelaing.edu.co"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login con credenciales erróneas debe retornar 401 Unauthorized")
    void debeRechazarLoginConContrasenaErronea() throws Exception {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("admin@mail.escuelaing.edu.co", "PasswordIncorrecta!");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }
}
