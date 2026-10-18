package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.IaNoDisponibleException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ComidaEntityMapper;
import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.EvaluacionNutricional;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.persistence.ComidaEntity;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.ComidaRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.service.ia.IEvaluadorNutricional;
import co.edu.eci.dosw.ecifit.validator.IComidaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComidaServiceImplTest {

    private static final String ESTUDIANTE_ID = "EST-1";
    private static final String DESCRIPCION = "Pollo con arroz y ensalada";

    @Mock
    private ComidaRepository comidaRepository;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private ComidaEntityMapper comidaEntityMapper;
    @Mock
    private IComidaValidator comidaValidator;
    @Mock
    private IEvaluadorNutricional evaluadorNutricional;
    @InjectMocks
    private ComidaServiceImpl service;

    private static Comida nuevaComida() {
        return new Comida(ESTUDIANTE_ID, TipoComida.ALMUERZO, DESCRIPCION, LocalDateTime.now());
    }

    private static EstudianteEntity estudiante(Integer puntos) {
        return EstudianteEntity.builder().id(ESTUDIANTE_ID).puntosAcumulados(puntos).build();
    }

    private static EvaluacionNutricional evaluacion(boolean balanceada) {
        return new EvaluacionNutricional(balanceada,
                balanceada ? CalificacionComida.BUENA : CalificacionComida.MALA,
                balanceada ? 85 : 30, "Consejo de prueba", List.of("Toma agua"));
    }

    private void prepararRegistro(EstudianteEntity estudiante, boolean yaHayComidaDelTipo) {
        when(estudianteRepository.findById(ESTUDIANTE_ID)).thenReturn(Optional.of(estudiante));
        when(comidaRepository.existsByEstudianteIdAndTipoAndFechaHoraBetween(
                eq(ESTUDIANTE_ID), eq(TipoComida.ALMUERZO), any(), any())).thenReturn(yaHayComidaDelTipo);
        ComidaEntity entity = ComidaEntity.builder().id("C-1").build();
        when(comidaEntityMapper.toEntity(any(Comida.class))).thenReturn(entity);
        when(comidaRepository.save(entity)).thenReturn(entity);
        when(comidaEntityMapper.toDomain(entity)).thenAnswer(inv -> new Comida());
    }

    private Comida comidaGuardada() {
        ArgumentCaptor<Comida> captor = ArgumentCaptor.forClass(Comida.class);
        verify(comidaEntityMapper).toEntity(captor.capture());
        return captor.getValue();
    }

    @Test
    void debeRegistrarComidaBalanceadaYOtorgarVeintePuntos() {
        EstudianteEntity estudiante = estudiante(100);
        prepararRegistro(estudiante, false);
        when(evaluadorNutricional.evaluar(TipoComida.ALMUERZO, DESCRIPCION)).thenReturn(evaluacion(true));

        service.registrar(nuevaComida());

        Comida guardada = comidaGuardada();
        assertNotNull(guardada.getId());
        assertEquals(EstadoEvaluacion.EVALUADA, guardada.getEstado());
        assertTrue(guardada.getBalanceada());
        assertEquals("Consejo de prueba", guardada.getConsejo());
        assertEquals(20, guardada.getPuntosOtorgados());
        assertEquals(120, estudiante.getPuntosAcumulados());
        verify(estudianteRepository).save(estudiante);
    }

    @Test
    void debeOtorgarCincoPuntosPorComidaNoBalanceada() {
        EstudianteEntity estudiante = estudiante(null);
        prepararRegistro(estudiante, false);
        when(evaluadorNutricional.evaluar(any(), anyString())).thenReturn(evaluacion(false));

        service.registrar(nuevaComida());

        assertEquals(5, comidaGuardada().getPuntosOtorgados());
        assertEquals(5, estudiante.getPuntosAcumulados());
    }

    @Test
    void debeEvaluarSegundaComidaDelMismoTipoSinOtorgarPuntos() {
        EstudianteEntity estudiante = estudiante(100);
        prepararRegistro(estudiante, true);
        when(evaluadorNutricional.evaluar(any(), anyString())).thenReturn(evaluacion(true));

        service.registrar(nuevaComida());

        Comida guardada = comidaGuardada();
        assertEquals(EstadoEvaluacion.EVALUADA, guardada.getEstado());
        assertFalse(guardada.getOtorgaPuntos());
        assertEquals(0, guardada.getPuntosOtorgados());
        assertEquals(100, estudiante.getPuntosAcumulados());
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void debeDejarComidaPendienteSinPuntosCuandoLaIaNoEstaDisponible() {
        EstudianteEntity estudiante = estudiante(100);
        prepararRegistro(estudiante, false);
        when(evaluadorNutricional.evaluar(any(), anyString()))
                .thenThrow(new IaNoDisponibleException("Gemini respondió con error HTTP 429"));

        assertDoesNotThrow(() -> service.registrar(nuevaComida()));

        Comida guardada = comidaGuardada();
        assertEquals(EstadoEvaluacion.PENDIENTE, guardada.getEstado());
        assertEquals(1, guardada.getIntentosEvaluacion());
        assertTrue(guardada.getOtorgaPuntos());
        assertEquals(0, guardada.getPuntosOtorgados());
        assertEquals(100, estudiante.getPuntosAcumulados());
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    void debeUsarLaFechaActualCuandoNoSeEnvia() {
        prepararRegistro(estudiante(0), false);
        when(evaluadorNutricional.evaluar(any(), anyString())).thenReturn(evaluacion(true));
        Comida comida = nuevaComida();
        comida.setFechaHora(null);

        service.registrar(comida);

        assertNotNull(comida.getFechaHora());
    }

    @Test
    void debeBuscarDuplicadosEnElDiaDeLaComida() {
        prepararRegistro(estudiante(0), false);
        when(evaluadorNutricional.evaluar(any(), anyString())).thenReturn(evaluacion(true));
        LocalDateTime fechaHora = LocalDateTime.now().minusMinutes(1);
        Comida comida = nuevaComida();
        comida.setFechaHora(fechaHora);

        service.registrar(comida);

        LocalDate dia = fechaHora.toLocalDate();
        verify(comidaRepository).existsByEstudianteIdAndTipoAndFechaHoraBetween(
                ESTUDIANTE_ID, TipoComida.ALMUERZO, dia.atStartOfDay(), dia.atTime(LocalTime.MAX));
    }

    @Test
    void debeLanzar404CuandoElEstudianteNoExiste() {
        when(estudianteRepository.findById(ESTUDIANTE_ID)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(nuevaComida()));

        verifyNoInteractions(evaluadorNutricional);
        verify(comidaRepository, never()).save(any());
    }

    @Test
    void debePropagarErrorDelValidador() {
        Comida comida = nuevaComida();
        doThrow(new ReglaDeNegocioException("No se puede registrar una comida en el futuro."))
                .when(comidaValidator).validar(comida);

        assertThrows(ReglaDeNegocioException.class, () -> service.registrar(comida));

        verifyNoInteractions(comidaRepository, evaluadorNutricional);
    }

    private Comida comidaPendienteEnRepositorio(String id) {
        ComidaEntity entity = ComidaEntity.builder().id(id).build();
        Comida pendiente = nuevaComida();
        pendiente.setId(id);
        pendiente.setIntentosEvaluacion(1);
        when(comidaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(comidaEntityMapper.toDomain(entity)).thenReturn(pendiente);
        return pendiente;
    }

    @Test
    void debeReevaluarComidaPendienteYOtorgarPuntos() {
        Comida pendiente = comidaPendienteEnRepositorio("C-1");
        EstudianteEntity estudiante = estudiante(10);
        ComidaEntity guardada = ComidaEntity.builder().id("C-1").build();
        when(estudianteRepository.findById(ESTUDIANTE_ID)).thenReturn(Optional.of(estudiante));
        when(evaluadorNutricional.evaluar(any(), anyString())).thenReturn(evaluacion(true));
        when(comidaEntityMapper.toEntity(pendiente)).thenReturn(guardada);
        when(comidaRepository.save(guardada)).thenReturn(guardada);
        when(comidaEntityMapper.toDomain(guardada)).thenReturn(pendiente);

        Comida resultado = service.reevaluar("C-1");

        assertEquals(EstadoEvaluacion.EVALUADA, resultado.getEstado());
        assertEquals(30, estudiante.getPuntosAcumulados());
    }

    @Test
    void debeRechazarReevaluarUnaComidaYaEvaluada() {
        Comida evaluada = comidaPendienteEnRepositorio("C-1");
        evaluada.aplicarEvaluacion(evaluacion(true));

        assertThrows(ReglaDeNegocioException.class, () -> service.reevaluar("C-1"));

        verifyNoInteractions(evaluadorNutricional);
    }

    @Test
    void debeLanzar404AlReevaluarComidaInexistente() {
        when(comidaRepository.findById("NO")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.reevaluar("NO"));
    }

    @Test
    void debeReintentarPendientesYContarSoloLasEvaluadas() {
        ComidaEntity entityOk = ComidaEntity.builder().id("C-1").build();
        ComidaEntity entityFalla = ComidaEntity.builder().id("C-2").build();
        Comida comidaOk = nuevaComida();
        Comida comidaFalla = nuevaComida();
        comidaFalla.setIntentosEvaluacion(2);
        when(comidaRepository.findTop50ByEstadoAndIntentosEvaluacionLessThanOrderByFechaHoraAsc(
                EstadoEvaluacion.PENDIENTE, Comida.MAX_INTENTOS_EVALUACION))
                .thenReturn(List.of(entityOk, entityFalla));
        when(comidaEntityMapper.toDomain(entityOk)).thenReturn(comidaOk);
        when(comidaEntityMapper.toDomain(entityFalla)).thenReturn(comidaFalla);
        when(estudianteRepository.findById(ESTUDIANTE_ID)).thenReturn(Optional.of(estudiante(0)));
        when(evaluadorNutricional.evaluar(any(), anyString()))
                .thenReturn(evaluacion(true))
                .thenThrow(new IaNoDisponibleException("timeout"));

        int evaluadas = service.reintentarPendientes();

        assertEquals(1, evaluadas);
        assertEquals(EstadoEvaluacion.EVALUADA, comidaOk.getEstado());
        assertEquals(EstadoEvaluacion.ERROR, comidaFalla.getEstado());
        verify(comidaRepository, times(2)).save(any());
    }

    @Test
    void debeOmitirPendientesDeEstudiantesInexistentes() {
        ComidaEntity entity = ComidaEntity.builder().id("C-1").build();
        when(comidaRepository.findTop50ByEstadoAndIntentosEvaluacionLessThanOrderByFechaHoraAsc(
                EstadoEvaluacion.PENDIENTE, Comida.MAX_INTENTOS_EVALUACION)).thenReturn(List.of(entity));
        when(comidaEntityMapper.toDomain(entity)).thenReturn(nuevaComida());
        when(estudianteRepository.findById(ESTUDIANTE_ID)).thenReturn(Optional.empty());

        int evaluadas = service.reintentarPendientes();

        assertEquals(0, evaluadas);
        verifyNoInteractions(evaluadorNutricional);
        verify(comidaRepository, never()).save(any());
    }

    @Test
    void noDebeHacerNadaCuandoNoHayPendientes() {
        when(comidaRepository.findTop50ByEstadoAndIntentosEvaluacionLessThanOrderByFechaHoraAsc(
                EstadoEvaluacion.PENDIENTE, Comida.MAX_INTENTOS_EVALUACION)).thenReturn(List.of());

        assertEquals(0, service.reintentarPendientes());

        verifyNoInteractions(evaluadorNutricional);
    }

    @Test
    void debeConsultarComidaPorId() {
        ComidaEntity entity = ComidaEntity.builder().id("C-1").build();
        Comida comida = new Comida();
        when(comidaRepository.findById("C-1")).thenReturn(Optional.of(entity));
        when(comidaEntityMapper.toDomain(entity)).thenReturn(comida);

        assertSame(comida, service.obtenerPorId("C-1"));
    }

    @Test
    void debeLanzar404CuandoLaComidaNoExiste() {
        when(comidaRepository.findById("NO")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId("NO"));
    }

    @Test
    void debeConsultarHistorialCompletoCuandoNoHayFecha() {
        ComidaEntity entity = ComidaEntity.builder().id("C-1").build();
        Comida comida = new Comida();
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        when(comidaRepository.findByEstudianteIdOrderByFechaHoraDesc(ESTUDIANTE_ID)).thenReturn(List.of(entity));
        when(comidaEntityMapper.toDomain(entity)).thenReturn(comida);

        assertEquals(List.of(comida), service.obtenerHistorial(ESTUDIANTE_ID, null));
    }

    @Test
    void debeConsultarHistorialDeUnDia() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        when(comidaRepository.findByEstudianteIdAndFechaHoraBetweenOrderByFechaHoraDesc(
                ESTUDIANTE_ID, fecha.atStartOfDay(), fecha.atTime(LocalTime.MAX))).thenReturn(List.of());

        assertTrue(service.obtenerHistorial(ESTUDIANTE_ID, fecha).isEmpty());
    }

    @Test
    void debeLanzar404AlConsultarHistorialDeEstudianteInexistente() {
        when(estudianteRepository.existsById("NO")).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerHistorial("NO", null));
    }
}
