package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarComidaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ComidaResponseDTO;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.ComidaMapper;
import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.service.IComidaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComidaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IComidaService comidaService;

    @MockitoBean
    private ComidaMapper comidaMapper;

    private static ComidaResponseDTO respuesta() {
        return new ComidaResponseDTO("C-1", "EST-1", TipoComida.ALMUERZO, "Pollo con arroz y ensalada",
                LocalDateTime.of(2026, 10, 20, 12, 30), EstadoEvaluacion.EVALUADA, true,
                CalificacionComida.BUENA, 85, "Muy buena combinación.", List.of("Toma agua"), 20,
                ComidaMapper.AVISO_NUTRICIONAL);
    }

    @Test
    void debeRegistrarComidaYRetornar201() throws Exception {
        RegistrarComidaRequestDTO request = new RegistrarComidaRequestDTO(
                "EST-1", TipoComida.ALMUERZO, "Pollo con arroz y ensalada", null);
        Comida dominio = new Comida();
        when(comidaMapper.toDomain(any(RegistrarComidaRequestDTO.class))).thenReturn(dominio);
        when(comidaService.registrar(dominio)).thenReturn(dominio);
        when(comidaMapper.toResponse(dominio)).thenReturn(respuesta());

        mockMvc.perform(post("/api/v1/nutricion/comidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("C-1"))
                .andExpect(jsonPath("$.estado").value("EVALUADA"))
                .andExpect(jsonPath("$.balanceada").value(true))
                .andExpect(jsonPath("$.consejo").value("Muy buena combinación."))
                .andExpect(jsonPath("$.puntosOtorgados").value(20))
                .andExpect(jsonPath("$.aviso").exists());
    }

    @Test
    void debeRetornar400CuandoFaltanDatos() throws Exception {
        RegistrarComidaRequestDTO request = new RegistrarComidaRequestDTO("", null, "", null);

        mockMvc.perform(post("/api/v1/nutricion/comidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(comidaService, never()).registrar(any());
    }

    @Test
    void debeRetornar400CuandoElTipoDeComidaNoExiste() throws Exception {
        String request = """
                {"estudianteId":"EST-1","tipo":"BRUNCH","descripcion":"Huevos con tocineta"}
                """;

        mockMvc.perform(post("/api/v1/nutricion/comidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar404CuandoElEstudianteNoExiste() throws Exception {
        RegistrarComidaRequestDTO request = new RegistrarComidaRequestDTO(
                "NO-EXISTE", TipoComida.CENA, "Pasta con atún", null);
        Comida dominio = new Comida();
        when(comidaMapper.toDomain(any(RegistrarComidaRequestDTO.class))).thenReturn(dominio);
        when(comidaService.registrar(dominio)).thenThrow(new RecursoNoEncontradoException("Estudiante", "NO-EXISTE"));

        mockMvc.perform(post("/api/v1/nutricion/comidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void debeConsultarUnaComida() throws Exception {
        Comida dominio = new Comida();
        when(comidaService.obtenerPorId("C-1")).thenReturn(dominio);
        when(comidaMapper.toResponse(dominio)).thenReturn(respuesta());

        mockMvc.perform(get("/api/v1/nutricion/comidas/C-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacion").value("BUENA"));
    }

    @Test
    void debeConsultarHistorialSinFecha() throws Exception {
        List<Comida> comidas = List.of(new Comida());
        when(comidaService.obtenerHistorial("EST-1", null)).thenReturn(comidas);
        when(comidaMapper.toResponseList(comidas)).thenReturn(List.of(respuesta()));

        mockMvc.perform(get("/api/v1/nutricion/comidas/estudiante/EST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("C-1"));
    }

    @Test
    void debeConsultarHistorialPorFecha() throws Exception {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        List<Comida> comidas = List.of(new Comida());
        when(comidaService.obtenerHistorial("EST-1", fecha)).thenReturn(comidas);
        when(comidaMapper.toResponseList(comidas)).thenReturn(List.of(respuesta()));

        mockMvc.perform(get("/api/v1/nutricion/comidas/estudiante/EST-1").param("fecha", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("ALMUERZO"));
    }
}
