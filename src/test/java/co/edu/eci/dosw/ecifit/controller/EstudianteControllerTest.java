package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.CrearEstudianteRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.EstudianteResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.EstudianteMapper;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Tanque;
import co.edu.eci.dosw.ecifit.service.IEstudianteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstudianteController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "admin@mail.escuelaing.edu.co", roles = {"ADMINISTRADOR"})
@DisplayName("Pruebas Unitarias para EstudianteController (Patrón AAA)")
class EstudianteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IEstudianteService estudianteService;

    @MockitoBean
    private EstudianteMapper estudianteMapper;

    @Test
    @DisplayName("POST /api/v1/estudiantes debe retornar 201 Created cuando el request es válido")
    void debeCrearEstudianteYRetornar201() throws Exception {
        // Arrange
        CrearEstudianteRequestDTO request = new CrearEstudianteRequestDTO(
                "Carlos Mendoza",
                "carlos.mendoza@mail.escuelaing.edu.co",
                "TANQUE"
        );
        Estudiante estudiante = new Estudiante("E1", request.nombre(), request.correoInstitucional(), new Tanque());
        EstudianteResponseDTO responseDTO = new EstudianteResponseDTO(
                "E1",
                "Carlos Mendoza",
                "carlos.mendoza@mail.escuelaing.edu.co",
                0,
                "TANQUE",
                null
        );

        when(estudianteMapper.toDomain(any(CrearEstudianteRequestDTO.class))).thenReturn(estudiante);
        when(estudianteService.crearEstudiante(any(Estudiante.class), eq("TANQUE"))).thenReturn(estudiante);
        when(estudianteMapper.toResponse(estudiante)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("E1"))
                .andExpect(jsonPath("$.nombre").value("Carlos Mendoza"))
                .andExpect(jsonPath("$.rolActivo").value("TANQUE"));
    }

    @Test
    @DisplayName("GET /api/v1/estudiantes/{id} debe retornar 200 OK cuando existe")
    void debeObtenerEstudiantePorId() throws Exception {
        // Arrange
        String id = "E1";
        Estudiante estudiante = new Estudiante(id, "Carlos Mendoza", "carlos.mendoza@mail.escuelaing.edu.co", new Tanque());
        EstudianteResponseDTO responseDTO = new EstudianteResponseDTO(
                id,
                "Carlos Mendoza",
                "carlos.mendoza@mail.escuelaing.edu.co",
                100,
                "TANQUE",
                null
        );

        when(estudianteService.obtenerPorId(id)).thenReturn(estudiante);
        when(estudianteMapper.toResponse(estudiante)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(get("/api/v1/estudiantes/{id}", id));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("E1"))
                .andExpect(jsonPath("$.nombre").value("Carlos Mendoza"));
    }
}
