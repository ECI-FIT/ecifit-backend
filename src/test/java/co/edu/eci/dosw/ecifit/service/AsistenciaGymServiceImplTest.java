package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.AsistenciaGymEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.EstudianteEntityMapper;
import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.persistence.AsistenciaGymEntity;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.AsistenciaGymRepository;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IAsistenciaGymValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaGymServiceImplTest {

    @Mock
    private AsistenciaGymRepository asistenciaGymRepository;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private EstudianteEntityMapper estudianteEntityMapper;
    @Mock
    private AsistenciaGymEntityMapper asistenciaGymEntityMapper;
    @Mock
    private IAsistenciaGymValidator asistenciaGymValidator;
    @Mock
    private ITemporadaService temporadaService;
    @InjectMocks
    private AsistenciaGymServiceImpl service;

    @Test
    void debeRegistrarAsistenciaYPersistirBonoDeVeintePuntos() {
        String estudianteId = "EST-1";
        String entrenadorId = "ENT-1";
        EstudianteEntity estudianteEntity = EstudianteEntity.builder()
                .id(estudianteId)
                .puntosAcumulados(50)
                .build();
        Estudiante estudiante = new Estudiante(estudianteId, "Ana", "ana@eci.edu.co", 50, null, null);
        AsistenciaGymEntity asistenciaEntity = AsistenciaGymEntity.builder()
                .id("ASIS-1")
                .estudianteId(estudianteId)
                .entrenadorId(entrenadorId)
                .fechaHora(LocalDateTime.now())
                .puntosOtorgados(20)
                .build();
        AsistenciaGym asistencia = new AsistenciaGym(
                "ASIS-1", estudianteId, entrenadorId, asistenciaEntity.getFechaHora());

        when(estudianteRepository.findById(estudianteId)).thenReturn(Optional.of(estudianteEntity));
        when(asistenciaGymRepository.existsByEstudianteIdAndFechaHoraBetween(
                eq(estudianteId), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(false);
        when(estudianteEntityMapper.toDomain(estudianteEntity)).thenReturn(estudiante);
        when(estudianteEntityMapper.toEntity(estudiante)).thenReturn(estudianteEntity);
        when(asistenciaGymEntityMapper.toEntity(any(AsistenciaGym.class))).thenReturn(asistenciaEntity);
        when(asistenciaGymRepository.save(asistenciaEntity)).thenReturn(asistenciaEntity);
        when(asistenciaGymEntityMapper.toDomain(asistenciaEntity)).thenReturn(asistencia);

        AsistenciaGym resultado = service.registrarAsistencia(estudianteId, entrenadorId);

        assertSame(asistencia, resultado);
        assertEquals(20, resultado.getPuntosOtorgados());
        ArgumentCaptor<Estudiante> estudianteCaptor = ArgumentCaptor.forClass(Estudiante.class);
        verify(estudianteEntityMapper).toEntity(estudianteCaptor.capture());
        assertEquals(70, estudianteCaptor.getValue().getPuntosAcumulados());
        verify(asistenciaGymRepository).save(asistenciaEntity);
    }

    @Test
    void debeRechazarAsistenciaDuplicadaEnElMismoDia() {
        String estudianteId = "EST-1";
        when(estudianteRepository.findById(estudianteId))
                .thenReturn(Optional.of(EstudianteEntity.builder().id(estudianteId).build()));
        when(asistenciaGymRepository.existsByEstudianteIdAndFechaHoraBetween(
                eq(estudianteId), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> service.registrarAsistencia(estudianteId, "ENT-1"));

        verify(estudianteRepository, never()).save(any());
        verify(asistenciaGymRepository, never()).save(any());
    }

    @Test
    void debeFallarSiElEstudianteNoExiste() {
        when(estudianteRepository.findById("EST-404")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.registrarAsistencia("EST-404", "ENT-1"));

        verify(asistenciaGymRepository, never()).save(any());
    }
}
