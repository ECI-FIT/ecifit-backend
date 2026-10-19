package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ClanEntityMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import co.edu.eci.dosw.ecifit.repository.ClanRepository;
import co.edu.eci.dosw.ecifit.validator.IClanValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para ClanServiceImpl (Patrón AAA)")
class ClanServiceImplTest {

    @Mock
    private ClanRepository clanRepository;
    @Mock
    private ClanEntityMapper entityMapper;
    @Mock
    private IClanValidator validator;
    @InjectMocks
    private ClanServiceImpl service;

    @Test
    @DisplayName("crear - clan valido se guarda y retorna con id asignado")
    void crear_clanValido_retornaConId() {
        // Arrange
        Clan dominio = new Clan(null, "Guerreros ECI");
        ClanEntity entidad = new ClanEntity();
        entidad.setId("C1");
        entidad.setNombre("Guerreros ECI");
        Clan dominioConId = new Clan("C1", "Guerreros ECI");

        when(entityMapper.toEntity(any(Clan.class))).thenReturn(entidad);
        when(clanRepository.save(entidad)).thenReturn(entidad);
        when(entityMapper.toDomain(entidad)).thenReturn(dominioConId);

        // Act
        Clan resultado = service.crear(dominio);

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("Guerreros ECI", resultado.getNombre());
        verify(validator, times(1)).validarNombreUnico("Guerreros ECI");
        verify(clanRepository, times(1)).save(entidad);
    }

    @Test
    @DisplayName("obtenerPorId - id inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_idInexistente_lanzaExcepcion() {
        // Arrange
        when(clanRepository.findById("C99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId("C99"));
    }

    @Test
    @DisplayName("obtenerPorId - id existente retorna dominio")
    void obtenerPorId_idExistente_retornaClan() {
        // Arrange
        String clanId = "C1";
        ClanEntity entidad = new ClanEntity();
        entidad.setId(clanId);
        entidad.setNombre("Guerreros ECI");
        Clan dominio = new Clan(clanId, "Guerreros ECI");

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(dominio);

        // Act
        Clan resultado = service.obtenerPorId(clanId);

        // Assert
        assertNotNull(resultado);
        assertEquals(clanId, resultado.getId());
        assertEquals("Guerreros ECI", resultado.getNombre());
    }

    @Test
    @DisplayName("crear - nombre duplicado propaga ConflictoException del validator")
    void crear_nombreDuplicado_lanzaConflicto() {
        // Arrange
        Clan dominio = new Clan(null, "Guerreros ECI");
        doThrow(new ConflictoException("Ya existe un clan con ese nombre"))
                .when(validator).validarNombreUnico("Guerreros ECI");

        // Act & Assert
        assertThrows(ConflictoException.class, () -> service.crear(dominio));
        verify(clanRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirse - clan lleno lanza ReglaDeNegocioException")
    void unirse_clanLleno_lanzaReglaDeNegocio() {
        // Arrange
        Clan clanLleno = new Clan("C1", "Guerreros ECI");
        for (int i = 0; i < 5; i++) {
            clanLleno.agregarMiembro("E" + i);
        }
        ClanEntity entidad = new ClanEntity();
        entidad.setId("C1");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(clanLleno);
        doThrow(new ReglaDeNegocioException("El clan ya alcanzo el limite de 5 miembros"))
                .when(validator).validarCupoDisponible(clanLleno);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> service.unirse("C1", "E99"));
        verify(clanRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirse - cupo disponible agrega miembro y persiste cambios")
    void unirse_cupoDisponible_agregaMiembroYRetornaClan() {
        // Arrange
        String clanId = "C1";
        String estudianteId = "E10";
        Clan dominio = new Clan(clanId, "Guerreros ECI");
        ClanEntity entidad = new ClanEntity();
        entidad.setId(clanId);
        entidad.setNombre("Guerreros ECI");

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(dominio);
        doNothing().when(validator).validarCupoDisponible(dominio);
        when(entityMapper.toEntity(dominio)).thenReturn(entidad);
        when(clanRepository.save(entidad)).thenReturn(entidad);

        // Act
        Clan resultado = service.unirse(clanId, estudianteId);

        // Assert
        assertNotNull(resultado);
        assertTrue(dominio.getMiembrosIds().contains(estudianteId));
        verify(clanRepository).save(entidad);
    }

    @Test
    @DisplayName("atacarTorreRival - clan existente reduce salud de la torre y persiste")
    void atacarTorreRival_clanExistente_reduceSaludTorre() {
        // Arrange
        String clanId = "C1";
        Clan dominio = new Clan(clanId, "Guerreros ECI");
        dominio.setSaludTorre(1000);
        ClanEntity entidad = new ClanEntity();
        entidad.setId(clanId);

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(dominio);
        when(entityMapper.toEntity(dominio)).thenReturn(entidad);
        when(clanRepository.save(entidad)).thenReturn(entidad);

        // Act
        Clan resultado = service.atacarTorreRival(clanId, 250);

        // Assert
        assertNotNull(resultado);
        assertEquals(750, dominio.getSaludTorre());
        verify(clanRepository).save(entidad);
    }

    @Test
    @DisplayName("obtenerTodos - sin datos devuelve lista vacía, no null")
    void obtenerTodos_sinDatos_devuelveListaVacia() {
        // Arrange
        when(clanRepository.findAll()).thenReturn(List.of());

        // Act
        List<Clan> resultado = service.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerTodos - con datos devuelve lista poblada de clanes")
    void obtenerTodos_conDatos_devuelveListaClanes() {
        // Arrange
        ClanEntity e1 = new ClanEntity();
        e1.setId("C1");
        ClanEntity e2 = new ClanEntity();
        e2.setId("C2");
        Clan c1 = new Clan("C1", "Guerreros");
        Clan c2 = new Clan("C2", "Titanes");

        when(clanRepository.findAll()).thenReturn(List.of(e1, e2));
        when(entityMapper.toDomain(e1)).thenReturn(c1);
        when(entityMapper.toDomain(e2)).thenReturn(c2);

        // Act
        List<Clan> resultado = service.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("C1", resultado.get(0).getId());
    }
}