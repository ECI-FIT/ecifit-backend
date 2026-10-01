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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        Clan dominio = new Clan(null, "Guerreros ECI");
        ClanEntity entidad = new ClanEntity();
        entidad.setId("C1");
        entidad.setNombre("Guerreros ECI");
        Clan dominioConId = new Clan("C1", "Guerreros ECI");

        when(entityMapper.toEntity(any(Clan.class))).thenReturn(entidad);
        when(clanRepository.save(entidad)).thenReturn(entidad);
        when(entityMapper.toDomain(entidad)).thenReturn(dominioConId);

        Clan resultado = service.crear(dominio);

        assertNotNull(resultado.getId());
        assertEquals("Guerreros ECI", resultado.getNombre());
        verify(validator, times(1)).validarNombreUnico("Guerreros ECI");
        verify(clanRepository, times(1)).save(entidad);
    }

    @Test
    @DisplayName("obtenerPorId - id inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_idInexistente_lanzaExcepcion() {
        when(clanRepository.findById("C99")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtenerPorId("C99"));
    }

    @Test
    @DisplayName("crear - nombre duplicado propaga ConflictoException del validator")
    void crear_nombreDuplicado_lanzaConflicto() {
        Clan dominio = new Clan(null, "Guerreros ECI");
        doThrow(new ConflictoException("Ya existe un clan con ese nombre"))
                .when(validator).validarNombreUnico("Guerreros ECI");

        assertThrows(ConflictoException.class,
                () -> service.crear(dominio));
        verify(clanRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirse - clan lleno lanza ReglaDeNegocioException")
    void unirse_clanLleno_lanzaReglaDeNegocio() {
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

        assertThrows(ReglaDeNegocioException.class,
                () -> service.unirse("C1", "E99"));
        verify(clanRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerTodos - sin datos devuelve lista vacía, no null")
    void obtenerTodos_sinDatos_devuelveListaVacia() {
        when(clanRepository.findAll()).thenReturn(List.of());

        List<Clan> resultado = service.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}