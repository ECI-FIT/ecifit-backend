package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ComidaEntityMapper;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.persistence.ComidaEntity;
import co.edu.eci.dosw.ecifit.repository.ComidaRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComidaServiceImplTest {

    private static final String ESTUDIANTE_ID = "EST-1";

    @Mock
    private ComidaRepository comidaRepository;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private ComidaEntityMapper comidaEntityMapper;
    @Mock
    private IComidaValidator comidaValidator;
    @InjectMocks
    private ComidaServiceImpl service;

    private static Comida nuevaComida() {
        return new Comida(ESTUDIANTE_ID, TipoComida.ALMUERZO, "Pollo con arroz y ensalada", LocalDateTime.now());
    }

    private void prepararGuardado() {
        ComidaEntity entity = ComidaEntity.builder().id("C-1").build();
        when(comidaEntityMapper.toEntity(any(Comida.class))).thenReturn(entity);
        when(comidaRepository.save(entity)).thenReturn(entity);
        when(comidaEntityMapper.toDomain(entity)).thenAnswer(inv -> new Comida());
    }

    @Test
    void debeRegistrarPrimeraComidaDelTipoComoPendienteQueOtorgaPuntos() {
        Comida comida = nuevaComida();
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        when(comidaRepository.existsByEstudianteIdAndTipoAndFechaHoraBetween(
                eq(ESTUDIANTE_ID), eq(TipoComida.ALMUERZO), any(), any())).thenReturn(false);
        prepararGuardado();

        service.registrar(comida);

        ArgumentCaptor<Comida> captor = ArgumentCaptor.forClass(Comida.class);
        verify(comidaEntityMapper).toEntity(captor.capture());
        Comida guardada = captor.getValue();
        assertNotNull(guardada.getId());
        assertEquals(EstadoEvaluacion.PENDIENTE, guardada.getEstado());
        assertTrue(guardada.getOtorgaPuntos());
        assertEquals(0, guardada.getPuntosOtorgados());
        verify(comidaValidator).validar(comida);
    }

    @Test
    void debeRegistrarSegundaComidaDelMismoTipoSinOtorgarPuntos() {
        Comida comida = nuevaComida();
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        when(comidaRepository.existsByEstudianteIdAndTipoAndFechaHoraBetween(
                eq(ESTUDIANTE_ID), eq(TipoComida.ALMUERZO), any(), any())).thenReturn(true);
        prepararGuardado();

        service.registrar(comida);

        ArgumentCaptor<Comida> captor = ArgumentCaptor.forClass(Comida.class);
        verify(comidaEntityMapper).toEntity(captor.capture());
        assertFalse(captor.getValue().getOtorgaPuntos());
    }

    @Test
    void debeUsarLaFechaActualCuandoNoSeEnvia() {
        Comida comida = nuevaComida();
        comida.setFechaHora(null);
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        prepararGuardado();

        service.registrar(comida);

        assertNotNull(comida.getFechaHora());
    }

    @Test
    void debeBuscarDuplicadosEnElDiaDeLaComida() {
        LocalDateTime fechaHora = LocalDateTime.now().minusMinutes(1);
        Comida comida = nuevaComida();
        comida.setFechaHora(fechaHora);
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        prepararGuardado();

        service.registrar(comida);

        LocalDate dia = fechaHora.toLocalDate();
        verify(comidaRepository).existsByEstudianteIdAndTipoAndFechaHoraBetween(
                ESTUDIANTE_ID, TipoComida.ALMUERZO, dia.atStartOfDay(), dia.atTime(LocalTime.MAX));
    }

    @Test
    void debeLanzar404CuandoElEstudianteNoExiste() {
        Comida comida = nuevaComida();
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> service.registrar(comida));

        verify(comidaRepository, never()).save(any());
    }

    @Test
    void debePropagarErrorDelValidador() {
        Comida comida = nuevaComida();
        doThrow(new ReglaDeNegocioException("No se puede registrar una comida en el futuro."))
                .when(comidaValidator).validar(comida);

        assertThrows(ReglaDeNegocioException.class, () -> service.registrar(comida));

        verifyNoInteractions(comidaRepository);
    }

    @Test
    void debeConsultarComidaPorId() {
        ComidaEntity entity = ComidaEntity.builder().id("C-1").build();
        Comida comida = new Comida();
        when(comidaRepository.findById("C-1")).thenReturn(Optional.of(entity));
        when(comidaEntityMapper.toDomain(entity)).thenReturn(comida);

        Comida resultado = service.obtenerPorId("C-1");

        assertSame(comida, resultado);
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

        List<Comida> resultado = service.obtenerHistorial(ESTUDIANTE_ID, null);

        assertEquals(List.of(comida), resultado);
    }

    @Test
    void debeConsultarHistorialDeUnDia() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        when(estudianteRepository.existsById(ESTUDIANTE_ID)).thenReturn(true);
        when(comidaRepository.findByEstudianteIdAndFechaHoraBetweenOrderByFechaHoraDesc(
                ESTUDIANTE_ID, fecha.atStartOfDay(), fecha.atTime(LocalTime.MAX))).thenReturn(List.of());

        List<Comida> resultado = service.obtenerHistorial(ESTUDIANTE_ID, fecha);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeLanzar404AlConsultarHistorialDeEstudianteInexistente() {
        when(estudianteRepository.existsById("NO")).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerHistorial("NO", null));
    }
}
