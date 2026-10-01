package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.MisionEntityMapper;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.factory.FabricaMisiones;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.model.factory.MisionDiaria;
import co.edu.eci.dosw.ecifit.persistence.MisionEntity;
import co.edu.eci.dosw.ecifit.repository.MisionRepository;
import co.edu.eci.dosw.ecifit.validator.IMisionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MisionServiceImplTest {

    @Mock
    private MisionRepository misionRepository;
    @Mock
    private MisionEntityMapper entityMapper;
    @Mock
    private IMisionValidator validator;
    @Mock
    private FabricaMisiones fabricaMisiones;
    @InjectMocks
    private MisionServiceImpl service;

    @Test
    @DisplayName("generarMisionDiaria - estudiante con cupo genera y guarda la misión")
    void generarMisionDiaria_estudianteConCupo_retornaConId() {
        // Arrange
        Mision misionCreada = new MisionDiaria("M1", "Caminar 30 min", 50, 30);
        MisionEntity entidad = new MisionEntity();
        entidad.setId("M1");
        entidad.setEstudianteId("E1");
        Mision misionGuardada = new MisionDiaria("M1", "Caminar 30 min", 50, 30);
        misionGuardada.setEstudianteId("E1");

        when(fabricaMisiones.crearMisionDiaria()).thenReturn(misionCreada);
        when(entityMapper.toEntity(any(Mision.class))).thenReturn(entidad);
        when(misionRepository.save(entidad)).thenReturn(entidad);
        when(entityMapper.toDomain(entidad)).thenReturn(misionGuardada);

        // Act
        Mision resultado = service.generarMisionDiaria("E1");

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("E1", resultado.getEstudianteId());
        verify(validator, times(1)).validarLimiteMisionesActivas("E1");
        verify(misionRepository, times(1)).save(entidad);
    }

    @Test
    @DisplayName("evaluarCumplimiento - misión inexistente lanza RecursoNoEncontradoException")
    void evaluarCumplimiento_misionInexistente_lanzaExcepcion() {
        // Arrange
        when(misionRepository.findById("M99")).thenReturn(Optional.empty());
        Actividad actividad = new Actividad("A1", "Trote", 35, 6, LocalDateTime.now());

        // Act & Assert
        assertThrows(co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException.class,
                () -> service.evaluarCumplimiento("M99", actividad));
    }

    @Test
    @DisplayName("generarMisionDiaria - limite de misiones activas propaga ReglaDeNegocioException")
    void generarMisionDiaria_limiteAlcanzado_lanzaReglaDeNegocio() {
        // Arrange
        doThrow(new ReglaDeNegocioException("El estudiante ya tiene 3 misiones activas simultáneamente"))
                .when(validator).validarLimiteMisionesActivas("E1");

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class,
                () -> service.generarMisionDiaria("E1"));
        verify(misionRepository, never()).save(any());
    }

    @Test
    @DisplayName("evaluarCumplimiento - misión ya completada lanza EstadoInvalidoException")
    void evaluarCumplimiento_misionCompletada_lanzaEstadoInvalido() {
        // Arrange
        MisionEntity entidad = new MisionEntity();
        entidad.setId("M1");
        entidad.setCompletada(true);
        Mision misionCompletada = new MisionDiaria("M1", "Caminar 30 min", 50, 30);
        misionCompletada.setCompletada(true);

        when(misionRepository.findById("M1")).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(misionCompletada);
        doThrow(new EstadoInvalidoException("La misión 'M1' ya fue completada"))
                .when(validator).validarNoCompletada(misionCompletada);

        Actividad actividad = new Actividad("A1", "Trote", 35, 6, LocalDateTime.now());

        // Act & Assert
        assertThrows(EstadoInvalidoException.class,
                () -> service.evaluarCumplimiento("M1", actividad));
        verify(misionRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerPorEstudiante - sin misiones devuelve lista vacía, no null")
    void obtenerPorEstudiante_sinDatos_devuelveListaVacia() {
        // Arrange
        when(misionRepository.findByEstudianteId("E1")).thenReturn(List.of());

        // Act
        List<Mision> resultado = service.obtenerPorEstudiante("E1");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
