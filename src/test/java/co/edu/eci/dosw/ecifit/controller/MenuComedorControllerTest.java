package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.dto.request.CrearMenuComedorRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.PlatoMenuRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MenuComedorResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.PlatoMenuResponseDTO;
import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.mapper.MenuComedorMapper;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.service.IMenuComedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuComedorController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuComedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMenuComedorService menuComedorService;

    @MockitoBean
    private MenuComedorMapper menuComedorMapper;

    private static final LocalDate FECHA = LocalDate.of(2026, 10, 20);

    private static MenuComedorResponseDTO respuesta() {
        return new MenuComedorResponseDTO("M-1", FECHA, TipoComida.ALMUERZO,
                List.of(new PlatoMenuResponseDTO("P-1", "Pollo a la plancha", "Con arroz", 12000)));
    }

    @Test
    void debePublicarMenuYRetornar201() throws Exception {
        CrearMenuComedorRequestDTO request = new CrearMenuComedorRequestDTO(FECHA, TipoComida.ALMUERZO,
                List.of(new PlatoMenuRequestDTO("Pollo a la plancha", "Con arroz", 12000)));
        MenuComedor dominio = new MenuComedor();
        when(menuComedorMapper.toDomain(any(CrearMenuComedorRequestDTO.class))).thenReturn(dominio);
        when(menuComedorService.publicar(dominio)).thenReturn(dominio);
        when(menuComedorMapper.toResponse(dominio)).thenReturn(respuesta());

        mockMvc.perform(post("/api/v1/comedor/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("M-1"))
                .andExpect(jsonPath("$.tipo").value("ALMUERZO"))
                .andExpect(jsonPath("$.platos[0].nombre").value("Pollo a la plancha"));
    }

    @Test
    void debeRetornar400CuandoElMenuNoTienePlatos() throws Exception {
        CrearMenuComedorRequestDTO request = new CrearMenuComedorRequestDTO(FECHA, TipoComida.ALMUERZO, List.of());

        mockMvc.perform(post("/api/v1/comedor/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(menuComedorService, never()).publicar(any());
    }

    @Test
    void debeRetornar400CuandoUnPlatoNoTieneNombre() throws Exception {
        CrearMenuComedorRequestDTO request = new CrearMenuComedorRequestDTO(FECHA, TipoComida.ALMUERZO,
                List.of(new PlatoMenuRequestDTO("", null, null)));

        mockMvc.perform(post("/api/v1/comedor/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar409CuandoElMenuYaExiste() throws Exception {
        CrearMenuComedorRequestDTO request = new CrearMenuComedorRequestDTO(FECHA, TipoComida.ALMUERZO,
                List.of(new PlatoMenuRequestDTO("Sopa", null, null)));
        MenuComedor dominio = new MenuComedor();
        when(menuComedorMapper.toDomain(any(CrearMenuComedorRequestDTO.class))).thenReturn(dominio);
        when(menuComedorService.publicar(dominio)).thenThrow(new ConflictoException("Ya existe"));

        mockMvc.perform(post("/api/v1/comedor/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void debeConsultarElMenuDeHoy() throws Exception {
        List<MenuComedor> menus = List.of(new MenuComedor());
        when(menuComedorService.obtenerHoy()).thenReturn(menus);
        when(menuComedorMapper.toResponseList(menus)).thenReturn(List.of(respuesta()));

        mockMvc.perform(get("/api/v1/comedor/menus/hoy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("M-1"));
    }

    @Test
    void debeConsultarElMenuPorFecha() throws Exception {
        List<MenuComedor> menus = List.of(new MenuComedor());
        when(menuComedorService.obtenerPorFecha(FECHA)).thenReturn(menus);
        when(menuComedorMapper.toResponseList(menus)).thenReturn(List.of(respuesta()));

        mockMvc.perform(get("/api/v1/comedor/menus").param("fecha", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fecha").value("2026-10-20"));
    }

    @Test
    void debeRetornar400CuandoLaFechaTieneFormatoInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/comedor/menus").param("fecha", "20-10-2026"))
                .andExpect(status().isBadRequest());
    }
}
