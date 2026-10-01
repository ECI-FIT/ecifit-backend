package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.EstudianteEntityMapper;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Tanque;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.validator.IEstudianteValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para EstudianteServiceImpl (Patrón AAA)")
class EstudianteServiceImplTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private EstudianteEntityMapper estudianteEntityMapper;

    @Mock
    private IEstudianteValidator estudianteValidator;

    @InjectMocks
    private EstudianteServiceImpl estudianteService;

    @Test
    @DisplayName("Debe crear un estudiante exitosamente cuando los datos son válidos")
    void debeCrearEstudianteExitosamente() {
        // Arrange
        String rolString = "TANQUE";
        Estudiante estudiante = new Estudiante("E1", "Santiago Ruiz", "santiago.ruiz@mail.escuelaing.edu.co", new Tanque());
        EstudianteEntity entity = EstudianteEntity.builder()
                .id("E1")
                .nombre("Santiago Ruiz")
                .correoInstitucional("santiago.ruiz@mail.escuelaing.edu.co")
                .puntosAcumulados(0)
                .rolActivo("TANQUE")
                .build();
        EstudianteEntity savedEntity = EstudianteEntity.builder()
                .id("E1")
                .nombre("Santiago Ruiz")
                .correoInstitucional("santiago.ruiz@mail.escuelaing.edu.co")
                .puntosAcumulados(0)
                .rolActivo("TANQUE")
                .build();

        doNothing().when(estudianteValidator).validar(estudiante, rolString);
        when(estudianteEntityMapper.toEntity(estudiante)).thenReturn(entity);
        when(estudianteRepository.save(entity)).thenReturn(savedEntity);
        when(estudianteEntityMapper.toDomain(savedEntity)).thenReturn(estudiante);

        // Act
        Estudiante resultado = estudianteService.crearEstudiante(estudiante, rolString);

        // Assert
        assertNotNull(resultado);
        assertEquals("E1", resultado.getId());
        assertEquals("Santiago Ruiz", resultado.getNombre());
        verify(estudianteValidator).validar(estudiante, rolString);
        verify(estudianteRepository).save(entity);
        verify(estudianteEntityMapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Debe lanzar ConflictoException cuando el correo ya se encuentra registrado")
    void debeLanzarConflictoExceptionCuandoCorreoYaExiste() {
        // Arrange
        String rolString = "CORREDOR";
        Estudiante estudiante = new Estudiante("E2", "Laura Torres", "laura.torres@mail.escuelaing.edu.co", null);
        doThrow(new ConflictoException("El correo institucional ya se encuentra registrado"))
                .when(estudianteValidator).validar(estudiante, rolString);

        // Act & Assert
        ConflictoException exception = assertThrows(ConflictoException.class, () -> {
            estudianteService.crearEstudiante(estudiante, rolString);
        });

        assertEquals("El correo institucional ya se encuentra registrado", exception.getMessage());
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException cuando se consulta un ID inexistente")
    void debeLanzarRecursoNoEncontradoExceptionCuandoNoExistePorId() {
        // Arrange
        String idInexistente = "ID-999";
        when(estudianteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RecursoNoEncontradoException exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            estudianteService.obtenerPorId(idInexistente);
        });

        assertEquals("Estudiante no encontrado con ID: ID-999", exception.getMessage());
        verify(estudianteEntityMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Debe retornar el estudiante cuando el ID existe")
    void debeObtenerEstudiantePorIdExitosamente() {
        // Arrange
        String idExistente = "E1";
        EstudianteEntity entity = EstudianteEntity.builder()
                .id(idExistente)
                .nombre("Santiago Ruiz")
                .correoInstitucional("santiago.ruiz@mail.escuelaing.edu.co")
                .puntosAcumulados(150)
                .rolActivo("TANQUE")
                .build();
        Estudiante estudiante = new Estudiante(idExistente, "Santiago Ruiz", "santiago.ruiz@mail.escuelaing.edu.co", 150, new Tanque(), null);

        when(estudianteRepository.findById(idExistente)).thenReturn(Optional.of(entity));
        when(estudianteEntityMapper.toDomain(entity)).thenReturn(estudiante);

        // Act
        Estudiante resultado = estudianteService.obtenerPorId(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getId());
        assertEquals("Santiago Ruiz", resultado.getNombre());
        assertEquals(150, resultado.getPuntosAcumulados());
        verify(estudianteRepository).findById(idExistente);
    }
}
