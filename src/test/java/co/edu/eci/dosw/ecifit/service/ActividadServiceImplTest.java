package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ActividadEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.EstudianteEntityMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Corredor;
import co.edu.eci.dosw.ecifit.persistence.ActividadEntity;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.ActividadRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IActividadValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para ActividadServiceImpl (Patrón AAA)")
class ActividadServiceImplTest {

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private ActividadEntityMapper actividadEntityMapper;

    @Mock
    private EstudianteEntityMapper estudianteEntityMapper;

    @Mock
    private IActividadValidator actividadValidator;

    @InjectMocks
    private ActividadServiceImpl actividadService;

    @Test
    @DisplayName("Debe registrar actividad calculando puntos y actualizando al estudiante exitosamente")
    void debeRegistrarActividadExitosamenteYOtorgarPuntos() {
        // Arrange
        String estudianteId = "EST-1";
        Actividad actividad = new Actividad("ACT-1", "CARDIO", 30, 8, LocalDateTime.now());
        EstudianteEntity estudianteEntity = EstudianteEntity.builder()
                .id(estudianteId)
                .nombre("Laura Gomez")
                .correoInstitucional("laura.gomez@mail.escuelaing.edu.co")
                .puntosAcumulados(100)
                .rolActivo("CORREDOR")
                .build();
        Estudiante estudiante = new Estudiante(estudianteId, "Laura Gomez", "laura.gomez@mail.escuelaing.edu.co", 100, new Corredor(), null);
        ActividadEntity actividadEntity = ActividadEntity.builder()
                .id("ACT-1")
                .tipo("CARDIO")
                .duracionMinutos(30)
                .intensidad(8)
                .estudianteId(estudianteId)
                .puntosOtorgados(336)
                .build();

        when(estudianteRepository.findById(estudianteId)).thenReturn(Optional.of(estudianteEntity));
        doNothing().when(actividadValidator).validar(actividad);
        when(estudianteEntityMapper.toDomain(estudianteEntity)).thenReturn(estudiante);
        when(actividadEntityMapper.toEntity(eq(actividad), eq(estudianteId), eq(336))).thenReturn(actividadEntity);

        // Act
        Actividad resultado = actividadService.registrarActividad(actividad, estudianteId);

        // Assert
        assertNotNull(resultado);
        assertEquals("ACT-1", resultado.getId());
        assertEquals(436, estudiante.getPuntosAcumulados());
        assertEquals(436, estudianteEntity.getPuntosAcumulados());
        verify(estudianteRepository).findById(estudianteId);
        verify(actividadValidator).validar(actividad);
        verify(actividadRepository).save(actividadEntity);
        verify(estudianteRepository).save(estudianteEntity);
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException cuando el estudiante no existe")
    void debeLanzarRecursoNoEncontradoExceptionCuandoEstudianteNoExiste() {
        // Arrange
        String estudianteIdInexistente = "EST-999";
        Actividad actividad = new Actividad("ACT-1", "FUERZA", 45, 7, LocalDateTime.now());
        when(estudianteRepository.findById(estudianteIdInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            actividadService.registrarActividad(actividad, estudianteIdInexistente);
        });

        assertEquals("Estudiante no encontrado con ID: " + estudianteIdInexistente, exception.getMessage());
        verify(actividadValidator, never()).validar(any());
        verify(actividadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe propagar ReglaDeNegocioException cuando la actividad no pasa el validador")
    void debePropagarReglaDeNegocioExceptionCuandoActividadEsInvalida() {
        // Arrange
        String estudianteId = "EST-1";
        Actividad actividadInvalida = new Actividad("ACT-2", "MOVILIDAD", 5, 1, LocalDateTime.now());
        EstudianteEntity estudianteEntity = EstudianteEntity.builder()
                .id(estudianteId)
                .puntosAcumulados(0)
                .rolActivo("TANQUE")
                .build();

        when(estudianteRepository.findById(estudianteId)).thenReturn(Optional.of(estudianteEntity));
        doThrow(new ReglaDeNegocioException("Actividad inválida: duración mínima 10 min e intensidad entre 1 y 10"))
                .when(actividadValidator).validar(actividadInvalida);

        // Act & Assert
        ReglaDeNegocioException exception = assertThrows(ReglaDeNegocioException.class, () -> {
            actividadService.registrarActividad(actividadInvalida, estudianteId);
        });

        assertTrue(exception.getMessage().contains("Actividad inválida"));
        verify(actividadRepository, never()).save(any());
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar lista de actividades ordenadas para un estudiante existente")
    void debeRetornarHistorialDeActividadesOrdenadasExitosamente() {
        // Arrange
        String estudianteId = "EST-1";
        ActividadEntity entity1 = ActividadEntity.builder()
                .id("ACT-1")
                .tipo("CARDIO")
                .duracionMinutos(30)
                .intensidad(8)
                .estudianteId(estudianteId)
                .fecha(LocalDateTime.now())
                .puntosOtorgados(336)
                .build();
        ActividadEntity entity2 = ActividadEntity.builder()
                .id("ACT-2")
                .tipo("FUERZA")
                .duracionMinutos(45)
                .intensidad(7)
                .estudianteId(estudianteId)
                .fecha(LocalDateTime.now().minusDays(1))
                .puntosOtorgados(450)
                .build();

        Actividad actividad1 = new Actividad("ACT-1", "CARDIO", 30, 8, entity1.getFecha());
        Actividad actividad2 = new Actividad("ACT-2", "FUERZA", 45, 7, entity2.getFecha());

        when(estudianteRepository.existsById(estudianteId)).thenReturn(true);
        when(actividadRepository.findByEstudianteIdOrderByFechaDesc(estudianteId)).thenReturn(List.of(entity1, entity2));
        when(actividadEntityMapper.toDomain(entity1)).thenReturn(actividad1);
        when(actividadEntityMapper.toDomain(entity2)).thenReturn(actividad2);

        // Act
        List<Actividad> resultado = actividadService.obtenerHistorialPorEstudiante(estudianteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ACT-1", resultado.get(0).getId());
        assertEquals("ACT-2", resultado.get(1).getId());
        verify(estudianteRepository).existsById(estudianteId);
        verify(actividadRepository).findByEstudianteIdOrderByFechaDesc(estudianteId);
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException cuando se consulta historial de un estudiante inexistente")
    void debeLanzarRecursoNoEncontradoExceptionCuandoEstudianteNoExisteAlConsultarHistorial() {
        // Arrange
        String estudianteIdInexistente = "EST-999";
        when(estudianteRepository.existsById(estudianteIdInexistente)).thenReturn(false);

        // Act & Assert
        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            actividadService.obtenerHistorialPorEstudiante(estudianteIdInexistente);
        });

        assertEquals("Estudiante no encontrado con ID: " + estudianteIdInexistente, exception.getMessage());
        verify(actividadRepository, never()).findByEstudianteIdOrderByFechaDesc(any());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el estudiante existe pero no tiene actividades registradas")
    void debeRetornarListaVaciaCuandoEstudianteNoTieneActividades() {
        // Arrange
        String estudianteId = "EST-1";
        when(estudianteRepository.existsById(estudianteId)).thenReturn(true);
        when(actividadRepository.findByEstudianteIdOrderByFechaDesc(estudianteId)).thenReturn(List.of());

        // Act
        List<Actividad> resultado = actividadService.obtenerHistorialPorEstudiante(estudianteId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(estudianteRepository).existsById(estudianteId);
        verify(actividadRepository).findByEstudianteIdOrderByFechaDesc(estudianteId);
    }
}
