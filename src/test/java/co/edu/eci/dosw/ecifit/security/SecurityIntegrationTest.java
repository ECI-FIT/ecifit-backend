package co.edu.eci.dosw.ecifit.security;

import co.edu.eci.dosw.ecifit.dto.request.CrearEstudianteRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.RegistrarActividadRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.TemporadaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.UnirseClanRequestDTO;
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

import java.time.LocalDate;

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

    @Autowired
    private co.edu.eci.dosw.ecifit.repository.MisionRepository misionRepository;

    @Autowired
    private co.edu.eci.dosw.ecifit.repository.EstudianteRepository estudianteRepository;

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

    // =========================================================================
    // PRUEBAS DE LA MATRIZ RBAC SOBRE ENDPOINTS DE NEGOCIO (Sprint 3)
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/estudiantes sin autenticación debe retornar 401 Unauthorized")
    void debeRechazarCrearEstudianteSinAutenticacion() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(post("/api/v1/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/v1/estudiantes con rol ESTUDIANTE debe retornar 403 Forbidden")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debeRechazarCrearEstudianteConRolEstudiante() throws Exception {
        // Arrange
        CrearEstudianteRequestDTO dto = new CrearEstudianteRequestDTO("Carlos", "carlos@mail.escuelaing.edu.co", "TANQUE");

        // Act & Assert
        mockMvc.perform(post("/api/v1/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("POST /api/v1/actividades con rol ENTRENADOR debe retornar 403 Forbidden")
    @WithMockUser(username = "entrenador@mail.escuelaing.edu.co", roles = {"ENTRENADOR"})
    void debeRechazarRegistrarActividadConRolEntrenador() throws Exception {
        // Arrange
        RegistrarActividadRequestDTO dto = new RegistrarActividadRequestDTO("EST-1", "CARDIO", 30, 8);

        // Act & Assert
        mockMvc.perform(post("/api/v1/actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("POST /api/v1/temporadas con rol ESTUDIANTE debe retornar 403 Forbidden")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debeRechazarCrearTemporadaConRolEstudiante() throws Exception {
        // Arrange
        TemporadaRequestDTO dto = TemporadaRequestDTO.builder()
                .nombre("2026-1")
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(4))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/temporadas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("POST /api/v1/misiones/diarias con rol ESTUDIANTE debe retornar 403 Forbidden")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debeRechazarCrearMisionConRolEstudiante() throws Exception {
        // Arrange
        CrearMisionRequestDTO dto = new CrearMisionRequestDTO("EST-1");

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/diarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("POST /api/v1/misiones/semanales con rol ESTUDIANTE debe retornar 403 Forbidden")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debeRechazarCrearMisionSemanalConRolEstudiante() throws Exception {
        // Arrange
        CrearMisionRequestDTO dto = new CrearMisionRequestDTO("EST-1");

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/semanales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("POST /api/v1/misiones/{id}/completar con rol ENTRENADOR debe retornar 403 Forbidden")
    @WithMockUser(username = "entrenador@mail.escuelaing.edu.co", roles = {"ENTRENADOR"})
    void debeRechazarCompletarMisionConRolEntrenador() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(post("/api/v1/misiones/M-1/completar"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/unirse con rol ADMINISTRADOR debe retornar 403 Forbidden")
    @WithMockUser(username = "admin@mail.escuelaing.edu.co", roles = {"ADMINISTRADOR"})
    void debeRechazarUnirseClanConRolAdministrador() throws Exception {
        // Arrange
        UnirseClanRequestDTO dto = new UnirseClanRequestDTO("EST-1", "C1");

        // Act & Assert
        mockMvc.perform(post("/api/v1/clanes/unirse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado: permisos insuficientes"));
    }

    @Test
    @DisplayName("GET /api/v1/clanes sin autenticación debe retornar 401 Unauthorized")
    void debeRechazarConsultaClanesSinToken() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/clanes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/v1/clanes con rol ESTUDIANTE debe retornar 200 OK")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debePermitirConsultaClanesConRolEstudiante() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/clanes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/misiones/diarias con datos inválidos debe retornar 400 Bad Request")
    @WithMockUser(username = "entrenador@mail.escuelaing.edu.co", roles = {"ENTRENADOR"})
    void debeRetornar400AlCrearMisionDiariaConDatosInvalidos() throws Exception {
        // Arrange
        String invalidJson = """
                {
                  "estudianteId": "EST-1",
                  "descripcion": "",
                  "recompensa": -50
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/diarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/misiones/{id}/completar - primera llamada retorna 200 OK y segunda retorna 422 Unprocessable Entity")
    @WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
    void debeCompletarMisionPrimeraVezYRetornar422EnSegundaVez() throws Exception {
        // Arrange: Crear estudiante y misión pendiente en BD
        String estId = "EST-FLOW-1";
        co.edu.eci.dosw.ecifit.persistence.EstudianteEntity est = co.edu.eci.dosw.ecifit.persistence.EstudianteEntity.builder()
                .id(estId)
                .nombre("Atleta Flow")
                .puntosAcumulados(50)
                .build();
        estudianteRepository.save(est);

        String misId = java.util.UUID.randomUUID().toString();
        co.edu.eci.dosw.ecifit.persistence.MisionEntity mis = new co.edu.eci.dosw.ecifit.persistence.MisionEntity();
        mis.setId(misId);
        mis.setEstudianteId(estId);
        mis.setDescripcion("Misión de integración");
        mis.setRecompensa(50);
        mis.setCompletada(false);
        mis.setTipo("DIARIA");
        misionRepository.save(mis);

        // Act & Assert 1: Primera llamada -> 200 OK, completada: true
        mockMvc.perform(post("/api/v1/misiones/{id}/completar", misId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(misId))
                .andExpect(jsonPath("$.completada").value(true));

        // Act & Assert 2: Segunda llamada consecutiva -> 422 Unprocessable Entity con mensaje exacto
        mockMvc.perform(post("/api/v1/misiones/{id}/completar", misId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje").value("La misión ya se encuentra completada previamente"));
    }
}
