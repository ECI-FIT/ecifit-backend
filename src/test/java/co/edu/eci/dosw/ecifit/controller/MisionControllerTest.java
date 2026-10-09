package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO;
import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import co.edu.eci.dosw.ecifit.exception.GlobalExceptionHandler;
import co.edu.eci.dosw.ecifit.mapper.MisionMapper;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.model.factory.MisionDiaria;
import co.edu.eci.dosw.ecifit.service.IMisionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MisionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "admin@mail.escuelaing.edu.co", roles = {"ADMINISTRADOR", "ENTRENADOR"})
@DisplayName("Pruebas Unitarias para MisionController (Patrón AAA)")
class MisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMisionService misionService;

    @MockitoBean
    private MisionMapper misionMapper;

    @Test
    @DisplayName("POST /api/v1/misiones/diarias con descripcion vacia debe retornar 400 Bad Request")
    void debeRetornar400CuandoDescripcionVacia() throws Exception {
        // Arrange
        String payloadJson = """
                {
                  "estudianteId": "EST-1",
                  "descripcion": "",
                  "recompensa": 50
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/diarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/misiones/diarias con recompensa negativa debe retornar 400 Bad Request")
    void debeRetornar400CuandoRecompensaNegativa() throws Exception {
        // Arrange
        String payloadJson = """
                {
                  "estudianteId": "EST-1",
                  "descripcion": "Activación diaria de carrera",
                  "recompensa": -50
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/diarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/misiones/diarias con datos validos debe retornar 201 Created")
    void debeRetornar201CuandoDatosValidos() throws Exception {
        // Arrange
        CrearMisionRequestDTO dto = new CrearMisionRequestDTO(
                "EST-1",
                "Completar 30 minutos de trote",
                50
        );
        Mision mision = new MisionDiaria("M-1", "Completar 30 minutos de trote", 50, 30);
        mision.setEstudianteId("EST-1");
        MisionResponseDTO responseDTO = new MisionResponseDTO(
                "M-1", "Completar 30 minutos de trote", 50, false, "EST-1"
        );

        when(misionService.generarMisionDiaria(any(CrearMisionRequestDTO.class))).thenReturn(mision);
        when(misionMapper.toResponse(mision)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/diarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("M-1"))
                .andExpect(jsonPath("$.descripcion").value("Completar 30 minutos de trote"))
                .andExpect(jsonPath("$.recompensa").value(50))
                .andExpect(jsonPath("$.completada").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/misiones/{id}/completar primera llamada retorna 200 OK con completada=true")
    void debeCompletarMisionExitosamenteYRetornar200() throws Exception {
        // Arrange
        String misionId = "M-100";
        MisionResponseDTO responseDTO = new MisionResponseDTO(
                misionId, "Meta 30 min", 50, true, "EST-1"
        );

        when(misionService.completarMision(misionId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/{id}/completar", misionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(misionId))
                .andExpect(jsonPath("$.completada").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/misiones/{id}/completar cuando ya esta completada retorna 422 con mensaje exacto")
    void debeRetornar422CuandoMisionYaEstaCompletada() throws Exception {
        // Arrange
        String misionId = "M-100";
        when(misionService.completarMision(misionId))
                .thenThrow(new EstadoInvalidoException("La misión ya se encuentra completada previamente"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/misiones/{id}/completar", misionId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje").value("La misión ya se encuentra completada previamente"));
    }
}
