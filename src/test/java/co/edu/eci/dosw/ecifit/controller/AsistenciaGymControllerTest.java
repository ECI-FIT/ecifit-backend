package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.AsistenciaGymRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.AsistenciaGymResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.AsistenciaGymMapper;
import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import co.edu.eci.dosw.ecifit.service.IAsistenciaGymService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsistenciaGymController.class)
@AutoConfigureMockMvc(addFilters = false)
class AsistenciaGymControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAsistenciaGymService asistenciaGymService;

    @MockitoBean
    private AsistenciaGymMapper asistenciaGymMapper;

    @Test
    void debeRegistrarAsistenciaYRetornar201() throws Exception {
        AsistenciaGymRequestDTO request = new AsistenciaGymRequestDTO("EST-1", "ENT-1");
        AsistenciaGym asistencia = new AsistenciaGym(
                "ASIS-1", "EST-1", "ENT-1", LocalDateTime.now());
        AsistenciaGymResponseDTO response = new AsistenciaGymResponseDTO(
                "ASIS-1", "EST-1", "ENT-1", asistencia.getFechaHora(), 20);
        when(asistenciaGymService.registrarAsistencia("EST-1", "ENT-1")).thenReturn(asistencia);
        when(asistenciaGymMapper.toResponse(asistencia)).thenReturn(response);

        mockMvc.perform(post("/api/v1/gimnasio/asistencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ASIS-1"))
                .andExpect(jsonPath("$.estudianteId").value("EST-1"))
                .andExpect(jsonPath("$.entrenadorId").value("ENT-1"))
                .andExpect(jsonPath("$.puntosOtorgados").value(20));

        verify(asistenciaGymService).registrarAsistencia("EST-1", "ENT-1");
        verify(asistenciaGymMapper).toResponse(any(AsistenciaGym.class));
    }

    @Test
    void debeRetornar400CuandoLosIdsNoSonValidos() throws Exception {
        AsistenciaGymRequestDTO request = new AsistenciaGymRequestDTO(" ", "");

        mockMvc.perform(post("/api/v1/gimnasio/asistencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
