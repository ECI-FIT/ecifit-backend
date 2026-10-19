package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.AbandonarClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.CompararRetoClanesRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.CrearClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.PublicarRetoClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.UnirseClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ClanResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.ResultadoRetoClanesResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.RetoClanResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.ClanMapper;
import co.edu.eci.dosw.ecifit.mapper.RetoClanMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import co.edu.eci.dosw.ecifit.service.IClanService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClanController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "admin@mail.escuelaing.edu.co", roles = {"ADMINISTRADOR", "ENTRENADOR"})
@DisplayName("Pruebas Unitarias para ClanController (Patrón AAA)")
class ClanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IClanService clanService;

    @MockitoBean
    private ClanMapper clanMapper;

    @MockitoBean
    private RetoClanMapper retoClanMapper;

    @Test
    @DisplayName("POST /api/v1/clanes debe retornar 201 Created cuando el request es valido")
    void debeCrearClanYRetornar201() throws Exception {
        // Arrange
        CrearClanRequestDTO request = new CrearClanRequestDTO("Guerreros", "E1");
        Clan clan = new Clan("C1", "Guerreros");
        ClanResponseDTO responseDTO = new ClanResponseDTO("C1", "Guerreros", 5000, 0, 1);

        when(clanMapper.toDomain(any(CrearClanRequestDTO.class))).thenReturn(clan);
        when(clanService.crear(any(Clan.class))).thenReturn(clan);
        when(clanMapper.toResponse(clan)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/clanes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("C1"))
                .andExpect(jsonPath("$.nombre").value("Guerreros"));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/unirse debe retornar 200 OK cuando se une exitosamente")
    void debeUnirseAClanYRetornar200() throws Exception {
        // Arrange
        UnirseClanRequestDTO request = new UnirseClanRequestDTO("E2", "C1");
        Clan clan = new Clan("C1", "Guerreros");
        ClanResponseDTO responseDTO = new ClanResponseDTO("C1", "Guerreros", 5000, 0, 2);

        when(clanService.unirse("C1", "E2")).thenReturn(clan);
        when(clanMapper.toResponse(clan)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/clanes/unirse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("C1"))
                .andExpect(jsonPath("$.cantidadMiembros").value(2));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/abandonar debe retornar 200 OK cuando el estudiante abandona")
    void debeAbandonarClanYRetornar200() throws Exception {
        // Arrange
        AbandonarClanRequestDTO request = new AbandonarClanRequestDTO("E2", "C1");
        Clan clan = new Clan("C1", "Guerreros");
        ClanResponseDTO responseDTO = new ClanResponseDTO("C1", "Guerreros", 5000, 0, 1);

        when(clanService.abandonar("C1", "E2")).thenReturn(clan);
        when(clanMapper.toResponse(clan)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/clanes/abandonar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("C1"))
                .andExpect(jsonPath("$.cantidadMiembros").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/clanes/{id} debe retornar 200 OK cuando el clan existe")
    void debeObtenerClanPorId() throws Exception {
        // Arrange
        String clanId = "C1";
        Clan clan = new Clan(clanId, "Guerreros");
        ClanResponseDTO responseDTO = new ClanResponseDTO(clanId, "Guerreros", 5000, 100, 3);

        when(clanService.obtenerPorId(clanId)).thenReturn(clan);
        when(clanMapper.toResponse(clan)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(get("/api/v1/clanes/{id}", clanId));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clanId))
                .andExpect(jsonPath("$.nombre").value("Guerreros"));
    }

    @Test
    @DisplayName("GET /api/v1/clanes debe retornar 200 OK y la lista de clanes")
    void debeObtenerTodosLosClanes() throws Exception {
        // Arrange
        Clan clan = new Clan("C1", "Guerreros");
        ClanResponseDTO responseDTO = new ClanResponseDTO("C1", "Guerreros", 5000, 100, 3);

        when(clanService.obtenerTodos()).thenReturn(List.of(clan));
        when(clanMapper.toResponse(clan)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(get("/api/v1/clanes"));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("C1"))
                .andExpect(jsonPath("$[0].nombre").value("Guerreros"));
    }

    // ==========================================
    // PRUEBAS DE ENDPOINTS PARA RF-34
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/clanes/retos debe retornar 201 Created al publicar reto colectivo")
    void debePublicarRetoColectivoYRetornar201() throws Exception {
        // Arrange
        PublicarRetoClanRequestDTO request = new PublicarRetoClanRequestDTO(
                "CLAN-01",
                "Acumular 500 puntos colectivos",
                500,
                150,
                LocalDate.of(2026, 10, 18),
                LocalDate.of(2026, 10, 25)
        );
        RetoClan reto = new RetoClan("RETO-01", "CLAN-01", request.descripcion(),
                500, 0, 150, request.fechaInicio(), request.fechaFin(), false);
        RetoClanResponseDTO responseDTO = new RetoClanResponseDTO(
                "RETO-01",
                "CLAN-01",
                request.descripcion(),
                500,
                0,
                150,
                request.fechaInicio(),
                request.fechaFin(),
                false,
                "PENDIENTE: (0/500 pts)"
        );

        when(retoClanMapper.toDomain(any(PublicarRetoClanRequestDTO.class))).thenReturn(reto);
        when(clanService.publicarRetoSemanal(any(RetoClan.class))).thenReturn(reto);
        when(retoClanMapper.toResponse(reto)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/clanes/retos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("RETO-01"))
                .andExpect(jsonPath("$.clanId").value("CLAN-01"))
                .andExpect(jsonPath("$.objetivoPuntos").value(500))
                .andExpect(jsonPath("$.recompensa").value(150));
    }

    // ==========================================
    // PRUEBAS DE ENDPOINTS PARA RF-36
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/clanes/{clanId}/retos/{retoId}/calcular debe retornar 200 OK con el reto evaluado")
    void debeCalcularResultadoRetoYRetornar200() throws Exception {
        // Arrange
        String clanId = "CLAN-01";
        String retoId = "RETO-01";
        RetoClan retoEvaluado = new RetoClan(retoId, clanId, "Meta 500", 500, 600, 150,
                LocalDate.now(), LocalDate.now().plusDays(7), true);
        RetoClanResponseDTO responseDTO = new RetoClanResponseDTO(
                retoId,
                clanId,
                "Meta 500",
                500,
                600,
                150,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                true,
                "SUPERADO: +150 pts asignados al clan"
        );

        when(clanService.calcularResultadoReto(clanId, retoId)).thenReturn(retoEvaluado);
        when(retoClanMapper.toResponse(retoEvaluado)).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/clanes/{clanId}/retos/{retoId}/calcular", clanId, retoId));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(retoId))
                .andExpect(jsonPath("$.completado").value(true))
                .andExpect(jsonPath("$.progresoActual").value(600));
    }

    @Test
    @DisplayName("POST /api/v1/clanes/retos/comparar-duelo debe retornar 200 OK con el resultado del enfrentamiento")
    void debeCompararDueloEntreClanesYRetornar200() throws Exception {
        // Arrange
        CompararRetoClanesRequestDTO request = new CompararRetoClanesRequestDTO("CLAN-01", "CLAN-02", 200);
        ResultadoRetoClanesResponseDTO responseDTO = new ResultadoRetoClanesResponseDTO(
                "CLAN-01",
                "Guerreros",
                800,
                "CLAN-02",
                "Titanes",
                550,
                "CLAN-01",
                200,
                false,
                "¡Victoria para Guerreros! +200 pts otorgados."
        );

        when(clanService.compararRetoEntreClanes(eq("CLAN-01"), eq("CLAN-02"), eq(200))).thenReturn(responseDTO);

        // Act
        var resultado = mockMvc.perform(post("/api/v1/clanes/retos/comparar-duelo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.clanGanadorId").value("CLAN-01"))
                .andExpect(jsonPath("$.bonificacionOtorgada").value(200))
                .andExpect(jsonPath("$.esEmpate").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/clanes/{clanId}/retos debe retornar 200 OK con los retos del clan")
    void debeObtenerRetosPorClanYRetornar200() throws Exception {
        // Arrange
        String clanId = "CLAN-01";
        RetoClan reto = new RetoClan("RETO-01", clanId, "Meta 500", 500, 0, 150,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        RetoClanResponseDTO responseDTO = new RetoClanResponseDTO(
                "RETO-01",
                clanId,
                "Meta 500",
                500,
                0,
                150,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                false,
                "PENDIENTE: (0/500 pts)"
        );

        when(clanService.obtenerRetosPorClan(clanId)).thenReturn(List.of(reto));
        when(retoClanMapper.toResponseList(List.of(reto))).thenReturn(List.of(responseDTO));

        // Act
        var resultado = mockMvc.perform(get("/api/v1/clanes/{clanId}/retos", clanId));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("RETO-01"))
                .andExpect(jsonPath("$[0].clanId").value(clanId));
    }
}
