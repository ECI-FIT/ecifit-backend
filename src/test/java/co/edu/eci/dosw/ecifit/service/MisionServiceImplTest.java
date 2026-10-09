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
    private co.edu.eci.dosw.ecifit.repository.EstudianteRepository estudianteRepository;
    @Mock
    private MisionEntityMapper entityMapper;
    @Mock
    private co.edu.eci.dosw.ecifit.mapper.MisionMapper misionMapper;
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

    // =========================================================================
    // PRUEBAS UNITARIAS EF-41 (HU-22 Misiones Semanales)
    // =========================================================================

    @Test
    @DisplayName("generarMisionSemanal_conEstudianteValido_retornaMisionCreada")
    void generarMisionSemanal_conEstudianteValido_retornaMisionCreada() {
        // Arrange
        co.edu.eci.dosw.ecifit.persistence.EstudianteEntity estudianteEntity = co.edu.eci.dosw.ecifit.persistence.EstudianteEntity.builder()
                .id("E1")
                .nombre("Juan")
                .puntosAcumulados(100)
                .build();
        co.edu.eci.dosw.ecifit.model.factory.MisionSemanal misionCreada = new co.edu.eci.dosw.ecifit.model.factory.MisionSemanal("M-SEM-1", "Completar 150 min semanales", 100, 150);
        MisionEntity entidad = new MisionEntity();
        entidad.setId("M-SEM-1");
        entidad.setEstudianteId("E1");
        entidad.setTipo("SEMANAL");
        entidad.setRecompensa(100);

        co.edu.eci.dosw.ecifit.model.factory.MisionSemanal misionGuardada = new co.edu.eci.dosw.ecifit.model.factory.MisionSemanal("M-SEM-1", "Completar 150 min semanales", 100, 150);
        misionGuardada.setEstudianteId("E1");

        when(estudianteRepository.findById("E1")).thenReturn(Optional.of(estudianteEntity));
        when(fabricaMisiones.crearMisionSemanal()).thenReturn(misionCreada);
        when(entityMapper.toEntity(any(Mision.class))).thenReturn(entidad);
        when(misionRepository.save(entidad)).thenReturn(entidad);
        when(entityMapper.toDomain(entidad)).thenReturn(misionGuardada);

        // Act
        Mision resultado = service.generarMisionSemanal("E1");

        // Assert
        assertNotNull(resultado);
        assertEquals("M-SEM-1", resultado.getId());
        assertEquals("E1", resultado.getEstudianteId());
        verify(estudianteRepository, times(1)).findById("E1");
        verify(validator, times(1)).validarLimiteMisionesActivas("E1");
        verify(misionRepository, times(1)).save(entidad);
    }

    @Test
    @DisplayName("generarMisionSemanal_conEstudianteInexistente_lanzaExcepcion")
    void generarMisionSemanal_conEstudianteInexistente_lanzaExcepcion() {
        // Arrange
        when(estudianteRepository.findById("E999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException.class,
                () -> service.generarMisionSemanal("E999"));
        verify(misionRepository, never()).save(any());
    }

    // =========================================================================
    // PRUEBAS UNITARIAS EF-61 (HU-26 Completar Misión)
    // =========================================================================

    @Test
    @DisplayName("completarMision_misionPendienteYCriteriosCumplidos_sumaRecompensaYRetorna200")
    void completarMision_misionPendienteYCriteriosCumplidos_sumaRecompensaYRetorna200() {
        // Arrange
        String misionId = "M-10";
        String estudianteId = "E-10";
        int puntosIniciales = 100;
        int recompensa = 50;

        MisionEntity misionEntity = new MisionEntity();
        misionEntity.setId(misionId);
        misionEntity.setEstudianteId(estudianteId);
        misionEntity.setRecompensa(recompensa);
        misionEntity.setCompletada(false);

        co.edu.eci.dosw.ecifit.persistence.EstudianteEntity estudianteEntity = co.edu.eci.dosw.ecifit.persistence.EstudianteEntity.builder()
                .id(estudianteId)
                .nombre("Carlos")
                .puntosAcumulados(puntosIniciales)
                .build();

        // Misión de dominio con evaluarCumplimiento() == true
        Mision misionDominio = new MisionDiaria(misionId, "Meta 30 min", recompensa, 30) {
            @Override
            public boolean evaluarCumplimiento() {
                return true;
            }
        };
        misionDominio.setEstudianteId(estudianteId);
        misionDominio.setCompletada(false);

        co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO expectedResponse =
                new co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO(misionId, "Meta 30 min", recompensa, true, estudianteId);

        when(misionRepository.findById(misionId)).thenReturn(Optional.of(misionEntity));
        when(entityMapper.toDomain(misionEntity)).thenReturn(misionDominio);
        when(estudianteRepository.findById(estudianteId)).thenReturn(Optional.of(estudianteEntity));
        when(misionMapper.toResponse(misionDominio)).thenReturn(expectedResponse);

        // Act
        co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO response = service.completarMision(misionId);

        // Assert
        assertNotNull(response);
        assertTrue(response.completada());
        assertEquals(150, estudianteEntity.getPuntosAcumulados());
        assertTrue(misionEntity.isCompletada());
        verify(misionRepository, times(1)).save(misionEntity);
        verify(estudianteRepository, times(1)).save(estudianteEntity);
    }

    @Test
    @DisplayName("completarMision_misionYaCompletada_lanzaExcepcionYNoModificaPuntos")
    void completarMision_misionYaCompletada_lanzaExcepcionYNoModificaPuntos() {
        // Arrange
        String misionId = "M-20";
        MisionEntity misionEntity = new MisionEntity();
        misionEntity.setId(misionId);
        misionEntity.setCompletada(true);

        when(misionRepository.findById(misionId)).thenReturn(Optional.of(misionEntity));

        // Act & Assert
        EstadoInvalidoException ex = assertThrows(EstadoInvalidoException.class, () -> service.completarMision(misionId));
        assertEquals("La misión ya se encuentra completada previamente", ex.getMessage());
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    @DisplayName("completarMision_criteriosNoCumplidos_lanzaExcepcion")
    void completarMision_criteriosNoCumplidos_lanzaExcepcion() {
        // Arrange
        String misionId = "M-30";
        MisionEntity misionEntity = new MisionEntity();
        misionEntity.setId(misionId);
        misionEntity.setEstudianteId("E-30");
        misionEntity.setRecompensa(40);
        misionEntity.setCompletada(false);

        Mision misionDominio = new MisionDiaria(misionId, "Meta 30 min", 40, 30) {
            @Override
            public boolean evaluarCumplimiento() {
                return false;
            }
        };
        misionDominio.setEstudianteId("E-30");
        misionDominio.setCompletada(false);

        when(misionRepository.findById(misionId)).thenReturn(Optional.of(misionEntity));
        when(entityMapper.toDomain(misionEntity)).thenReturn(misionDominio);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> service.completarMision(misionId));
        verify(estudianteRepository, never()).save(any());
    }

    @Test
    @DisplayName("completarMision_misionInexistente_lanzaRecursoNoEncontrado")
    void completarMision_misionInexistente_lanzaRecursoNoEncontrado() {
        // Arrange
        String misionId = "M-INEXISTENTE";
        when(misionRepository.findById(misionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException.class,
                () -> service.completarMision(misionId));
        verify(estudianteRepository, never()).save(any());
        verify(misionRepository, never()).save(any());
    }

    @Test
    @DisplayName("generarMisionDiaria - con CrearMisionRequestDTO personalizado persiste datos del payload")
    void generarMisionDiaria_conPayloadPersonalizado_persisteDatosPersonalizados() {
        // Arrange
        co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO dto =
                new co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO("E1", "Correr 5k", 80);
        Mision misionCreada = new MisionDiaria("M-CUSTOM", "Default", 50, 30);
        MisionEntity entidad = new MisionEntity();
        entidad.setId("M-CUSTOM");
        entidad.setEstudianteId("E1");
        entidad.setDescripcion("Correr 5k");
        entidad.setRecompensa(80);

        Mision misionGuardada = new MisionDiaria("M-CUSTOM", "Correr 5k", 80, 30);
        misionGuardada.setEstudianteId("E1");

        when(fabricaMisiones.crearMisionDiaria()).thenReturn(misionCreada);
        when(entityMapper.toEntity(any(Mision.class))).thenReturn(entidad);
        when(misionRepository.save(entidad)).thenReturn(entidad);
        when(entityMapper.toDomain(entidad)).thenReturn(misionGuardada);

        // Act
        Mision resultado = service.generarMisionDiaria(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Correr 5k", resultado.getDescripcion());
        assertEquals(80, resultado.getRecompensa());
        assertEquals("E1", resultado.getEstudianteId());
        verify(validator, times(1)).validarLimiteMisionesActivas("E1");
    }

    @Test
    @DisplayName("evaluarCumplimiento - entidad Mision valida criterios de dominio correctamente")
    void evaluarCumplimiento_dominioMision_validaCorrectamente() {
        // Misión pendiente con estudiante asignado -> true
        Mision misionActiva = new MisionDiaria("M1", "Trote", 50, 30);
        misionActiva.setEstudianteId("E1");
        misionActiva.setCompletada(false);
        assertTrue(misionActiva.evaluarCumplimiento());

        // Misión ya completada -> false
        misionActiva.setCompletada(true);
        org.junit.jupiter.api.Assertions.assertFalse(misionActiva.evaluarCumplimiento());

        // Misión sin estudiante asignado -> false
        Mision misionSinEstudiante = new MisionDiaria("M2", "Trote", 50, 30);
        misionSinEstudiante.setCompletada(false);
        misionSinEstudiante.setEstudianteId(null);
        org.junit.jupiter.api.Assertions.assertFalse(misionSinEstudiante.evaluarCumplimiento());
    }
}
