package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.IaNoDisponibleException;
import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.EvaluacionNutricional;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.ComidaRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.repository.MenuComedorRepository;
import co.edu.eci.dosw.ecifit.service.ia.IEvaluadorNutricional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Verifica contra la base H2 que comidas y menús se guardan y se leen completos
 * (mappers, entidades y colecciones), con la IA simulada.
 */
@SpringBootTest
class NutricionIntegrationTest {

    @Autowired
    private IComidaService comidaService;
    @Autowired
    private IMenuComedorService menuComedorService;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private ComidaRepository comidaRepository;
    @Autowired
    private MenuComedorRepository menuComedorRepository;

    @MockitoBean
    private IEvaluadorNutricional evaluadorNutricional;

    @AfterEach
    void limpiar() {
        comidaRepository.deleteAll();
        menuComedorRepository.deleteAll();
    }

    private String crearEstudiante() {
        String id = UUID.randomUUID().toString();
        estudianteRepository.save(EstudianteEntity.builder()
                .id(id).nombre("Ana").correoInstitucional(id + "@mail.escuelaing.edu.co")
                .puntosAcumulados(10).rolActivo("TANQUE").build());
        return id;
    }

    @Test
    void debeRegistrarEvaluarYGuardarComidaCompleta() {
        String estudianteId = crearEstudiante();
        when(evaluadorNutricional.evaluar(any(), anyString())).thenReturn(new EvaluacionNutricional(
                true, CalificacionComida.BUENA, 88, "Excelente almuerzo.", List.of("Toma agua", "Agrega fruta")));

        Comida registrada = comidaService.registrar(new Comida(
                estudianteId, TipoComida.ALMUERZO, "Pollo, arroz, ensalada y lentejas", LocalDateTime.now()));
        Comida consultada = comidaService.obtenerPorId(registrada.getId());

        assertEquals(EstadoEvaluacion.EVALUADA, consultada.getEstado());
        assertEquals(CalificacionComida.BUENA, consultada.getCalificacion());
        assertEquals(88, consultada.getPuntaje());
        assertEquals("Excelente almuerzo.", consultada.getConsejo());
        assertEquals(List.of("Toma agua", "Agrega fruta"), consultada.getRecomendaciones());
        assertEquals(20, consultada.getPuntosOtorgados());
        assertEquals(30, estudianteRepository.findById(estudianteId).orElseThrow().getPuntosAcumulados());
        assertEquals(1, comidaService.obtenerHistorial(estudianteId, LocalDate.now()).size());
    }

    @Test
    void debeDejarPendienteYLuegoEvaluarConElReintento() {
        String estudianteId = crearEstudiante();
        when(evaluadorNutricional.evaluar(any(), anyString()))
                .thenThrow(new IaNoDisponibleException("sin cuota"))
                .thenReturn(new EvaluacionNutricional(false, CalificacionComida.REGULAR, 50, "Le faltó verdura.", List.of()));

        Comida registrada = comidaService.registrar(new Comida(
                estudianteId, TipoComida.CENA, "Hamburguesa con papas", LocalDateTime.now()));
        assertEquals(EstadoEvaluacion.PENDIENTE, registrada.getEstado());
        assertEquals(10, estudianteRepository.findById(estudianteId).orElseThrow().getPuntosAcumulados());

        int evaluadas = comidaService.reintentarPendientes();

        assertEquals(1, evaluadas);
        assertEquals(EstadoEvaluacion.EVALUADA, comidaService.obtenerPorId(registrada.getId()).getEstado());
        assertEquals(15, estudianteRepository.findById(estudianteId).orElseThrow().getPuntosAcumulados());
    }

    @Test
    void debePublicarYConsultarMenuConSusPlatos() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        menuComedorService.publicar(new MenuComedor(null, fecha, TipoComida.ALMUERZO, List.of(
                new PlatoMenu(null, "Pollo a la plancha", "Con arroz y ensalada", 12000),
                new PlatoMenu(null, "Lasaña vegetariana", null, 11000))));

        List<MenuComedor> menus = menuComedorService.obtenerPorFecha(fecha);

        assertEquals(1, menus.size());
        assertEquals(TipoComida.ALMUERZO, menus.get(0).getTipo());
        assertEquals(2, menus.get(0).getPlatos().size());
        assertNotNull(menus.get(0).getPlatos().get(0).getId());
    }
}
