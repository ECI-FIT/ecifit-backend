package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarActividadRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ActividadResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.ActividadMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Corredor;
import co.edu.eci.dosw.ecifit.service.IActividadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActividadController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Pruebas Unitarias para ActividadController (Patrón AAA)")
class ActividadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IActividadService actividadService;

    @MockitoBean
    private ActividadMapper actividadMapper;

    @Test
    @DisplayName("POST /api/v1/actividades debe retornar 201 Created cuando el request es válido")
    void debeRegistrarActividadYRetornar201() throws Exception {
        // Arrange
        String estudianteId = "EST-1";
        RegistrarActividadRequestDTO request = new RegistrarActividadRequestDTO(
                estudianteId,
                "CARDIO",
                30,
                8
        );
        Actividad actividad = new Actividad("ACT-1", "CARDIO", 30, 8, LocalDateTime.now());
        ActividadResponseDTO responseDTO = new ActividadResponseDTO(
                "ACT-1",
                "CARDIO",
                30,
                8,
                LocalDateTime.now(),
                estudianteId,
                336
        );

        when(actividadMapper.toDomain(any(RegistrarActividadRequestDTO.class))).thenReturn(actividad);
        when(actividadService.registrarActividad(any(Actividad.class), eq(estudianteId))).thenAnswer(invocation -> {
            Actividad act = invocation.getArgument(0);
            Estudiante mockEstudiante = new Estudiante(estudianteId, "Test", "test@mail.escuelaing.edu.co", new Corredor());
            act.notificarObservadores(336, mockEstudiante);
            return act;
        });
        when(actividadMapper.toResponse(any(Actividad.class), eq(estudianteId), eq(336))).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/actividades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ACT-1"))
                .andExpect(jsonPath("$.tipo").value("CARDIO"))
                .andExpect(jsonPath("$.puntosOtorgados").value(336));
    }

    @Test
    @DisplayName("GET /api/v1/actividades/estudiante/{estudianteId} debe retornar 200 OK y la lista de actividades")
    void debeObtenerHistorialPorEstudianteYRetornar200() throws Exception {
        // Arrange
        String estudianteId = "EST-1";
        Actividad actividad = new Actividad("ACT-1", "CARDIO", 30, 8, LocalDateTime.now());
        ActividadResponseDTO responseDTO = new ActividadResponseDTO(
                "ACT-1",
                "CARDIO",
                30,
                8,
                LocalDateTime.now(),
                estudianteId,
                336
        );

        when(actividadService.obtenerHistorialPorEstudiante(estudianteId)).thenReturn(List.of(actividad));
        when(actividadMapper.toResponseList(List.of(actividad), estudianteId)).thenReturn(List.of(responseDTO));

        // Act
        var resultado = mockMvc.perform(get("/api/v1/actividades/estudiante/{estudianteId}", estudianteId));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ACT-1"))
                .andExpect(jsonPath("$[0].tipo").value("CARDIO"))
                .andExpect(jsonPath("$[0].estudianteId").value(estudianteId));
    }
}
