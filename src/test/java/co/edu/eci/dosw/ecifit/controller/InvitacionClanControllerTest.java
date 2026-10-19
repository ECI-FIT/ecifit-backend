package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.InvitarClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.ResponderInvitacionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.InvitacionClanResponseDTO;
import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.GlobalExceptionHandler;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.InvitacionClanMapper;
import co.edu.eci.dosw.ecifit.model.EstadoInvitacion;
import co.edu.eci.dosw.ecifit.model.InvitacionClan;
import co.edu.eci.dosw.ecifit.service.IInvitacionClanService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvitacionClanController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "estudiante@mail.escuelaing.edu.co", roles = {"ESTUDIANTE"})
@DisplayName("Pruebas Unitarias para InvitacionClanController (Patrón AAA)")
class InvitacionClanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IInvitacionClanService invitacionService;

    @MockitoBean
    private InvitacionClanMapper invitacionMapper;

    @Test
    @DisplayName("POST /api/v1/clanes/invitaciones debe retornar 201 Created al enviar invitación válida")
    void debeCrearInvitacionYRetornar201() throws Exception {
        // Arrange
        InvitarClanRequestDTO request = new InvitarClanRequestDTO("C1", "E2", "E1");
        InvitacionClan domain = new InvitacionClan("INV-1", "C1", "E2", "E1", EstadoInvitacion.PENDIENTE, LocalDateTime.now());
        InvitacionClanResponseDTO responseDTO = new InvitacionClanResponseDTO(
                "INV-1", "C1", "E2", "E1", EstadoInvitacion.PENDIENTE, LocalDateTime.now());

        when(invitacionService.invitar("C1", "E2", "E1")).thenReturn(domain);
        when(invitacionMapper.toResponse(domain)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clanes/invitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("INV-1"))
                .andExpect(jsonPath("$.clanId").value("C1"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/invitaciones/{id}/responder con aceptar=true retorna 200 OK y estado ACEPTADA")
    void debeAceptarInvitacionYRetornar200() throws Exception {
        // Arrange
        String invId = "INV-1";
        ResponderInvitacionRequestDTO request = new ResponderInvitacionRequestDTO(true);
        InvitacionClan domain = new InvitacionClan(invId, "C1", "E2", "E1", EstadoInvitacion.ACEPTADA, LocalDateTime.now());
        InvitacionClanResponseDTO responseDTO = new InvitacionClanResponseDTO(
                invId, "C1", "E2", "E1", EstadoInvitacion.ACEPTADA, LocalDateTime.now());

        when(invitacionService.responder(invId, true)).thenReturn(domain);
        when(invitacionMapper.toResponse(domain)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clanes/invitaciones/{id}/responder", invId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invId))
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/invitaciones/{id}/responder con aceptar=false retorna 200 OK y estado RECHAZADA")
    void debeRechazarInvitacionYRetornar200() throws Exception {
        // Arrange
        String invId = "INV-1";
        ResponderInvitacionRequestDTO request = new ResponderInvitacionRequestDTO(false);
        InvitacionClan domain = new InvitacionClan(invId, "C1", "E2", "E1", EstadoInvitacion.RECHAZADA, LocalDateTime.now());
        InvitacionClanResponseDTO responseDTO = new InvitacionClanResponseDTO(
                invId, "C1", "E2", "E1", EstadoInvitacion.RECHAZADA, LocalDateTime.now());

        when(invitacionService.responder(invId, false)).thenReturn(domain);
        when(invitacionMapper.toResponse(domain)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clanes/invitaciones/{id}/responder", invId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(invId))
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/invitaciones/{id}/responder con ID inexistente retorna 404")
    void debeRetornar404CuandoInvitacionNoExiste() throws Exception {
        // Arrange
        String invId = "INV-999";
        ResponderInvitacionRequestDTO request = new ResponderInvitacionRequestDTO(true);
        when(invitacionService.responder(invId, true))
                .thenThrow(new RecursoNoEncontradoException("Invitación no encontrada"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/clanes/invitaciones/{id}/responder", invId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/v1/clanes/invitaciones/estudiante/{estudianteId} retorna 200 y lista de pendientes")
    void debeRetornarPendientesYRetornar200() throws Exception {
        // Arrange
        String estudianteId = "E2";
        InvitacionClan domain = new InvitacionClan("INV-1", "C1", "E2", "E1", EstadoInvitacion.PENDIENTE, LocalDateTime.now());
        InvitacionClanResponseDTO responseDTO = new InvitacionClanResponseDTO(
                "INV-1", "C1", "E2", "E1", EstadoInvitacion.PENDIENTE, LocalDateTime.now());

        when(invitacionService.obtenerPendientes(estudianteId)).thenReturn(List.of(domain));
        when(invitacionMapper.toResponse(domain)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/clanes/invitaciones/estudiante/{estudianteId}", estudianteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("INV-1"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }
}
