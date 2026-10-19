package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.dto.response.ResultadoRetoClanesResponseDTO;
import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ClanEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.RetoClanEntityMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import co.edu.eci.dosw.ecifit.persistence.RetoClanEntity;
import co.edu.eci.dosw.ecifit.repository.ClanRepository;
import co.edu.eci.dosw.ecifit.repository.RetoClanRepository;
import co.edu.eci.dosw.ecifit.validator.IClanValidator;
import co.edu.eci.dosw.ecifit.validator.IRetoClanValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    @Mock
    private RetoClanRepository retoClanRepository;
    @Mock
    private RetoClanEntityMapper retoClanEntityMapper;
    @Mock
    private IRetoClanValidator retoClanValidator;

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

    // ==========================================
    // PRUEBAS PARA RF-34: PUBLICAR RETO SEMANAL
    // ==========================================

    @Test
    @DisplayName("publicarRetoSemanal - clan no encontrado lanza RecursoNoEncontradoException")
    void publicarRetoSemanal_clanInexistente_lanzaExcepcion() {
        // Arrange
        RetoClan reto = new RetoClan(null, "CLAN-99", "Meta 500", 500, 0, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        doNothing().when(retoClanValidator).validarPublicacionReto(reto);
        when(clanRepository.existsById("CLAN-99")).thenReturn(false);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> service.publicarRetoSemanal(reto));
        verify(retoClanRepository, never()).save(any());
    }

    @Test
    @DisplayName("publicarRetoSemanal - clan existente persiste y retorna reto publicado")
    void publicarRetoSemanal_exitoso_retornaReto() {
        // Arrange
        RetoClan reto = new RetoClan(null, "CLAN-01", "Acumular 500 pts", 500, 0, 150,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        RetoClanEntity entity = new RetoClanEntity();
        entity.setId("RETO-01");
        entity.setClanId("CLAN-01");

        doNothing().when(retoClanValidator).validarPublicacionReto(reto);
        when(clanRepository.existsById("CLAN-01")).thenReturn(true);
        when(retoClanEntityMapper.toEntity(any(RetoClan.class))).thenReturn(entity);
        when(retoClanRepository.save(entity)).thenReturn(entity);
        when(retoClanEntityMapper.toDomain(entity)).thenReturn(reto);

        // Act
        RetoClan resultado = service.publicarRetoSemanal(reto);

        // Assert
        assertNotNull(resultado);
        verify(retoClanValidator).validarPublicacionReto(reto);
        verify(retoClanRepository).save(entity);
    }

    // ========================================================
    // PRUEBAS PARA RF-36: CALCULAR RESULTADO DE RETO DE CLAN
    // ========================================================

    @Test
    @DisplayName("calcularResultadoReto - reto no encontrado lanza RecursoNoEncontradoException")
    void calcularResultadoReto_retoInexistente_lanzaExcepcion() {
        // Arrange
        ClanEntity clanEntity = new ClanEntity();
        clanEntity.setId("CLAN-01");
        Clan clan = new Clan("CLAN-01", "Guerreros");

        when(clanRepository.findById("CLAN-01")).thenReturn(Optional.of(clanEntity));
        when(entityMapper.toDomain(clanEntity)).thenReturn(clan);
        when(retoClanRepository.findById("RETO-99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> service.calcularResultadoReto("CLAN-01", "RETO-99"));
    }

    @Test
    @DisplayName("calcularResultadoReto - reto asignado a otro clan lanza ReglaDeNegocioException")
    void calcularResultadoReto_retoDeOtroClan_lanzaExcepcion() {
        // Arrange
        ClanEntity clanEntity = new ClanEntity();
        clanEntity.setId("CLAN-01");
        Clan clan = new Clan("CLAN-01", "Guerreros");

        RetoClanEntity retoEntity = new RetoClanEntity();
        retoEntity.setId("RETO-01");
        retoEntity.setClanId("CLAN-02");
        RetoClan reto = new RetoClan("RETO-01", "CLAN-02", "Reto", 500, 0, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), false);

        when(clanRepository.findById("CLAN-01")).thenReturn(Optional.of(clanEntity));
        when(entityMapper.toDomain(clanEntity)).thenReturn(clan);
        when(retoClanRepository.findById("RETO-01")).thenReturn(Optional.of(retoEntity));
        when(retoClanEntityMapper.toDomain(retoEntity)).thenReturn(reto);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> service.calcularResultadoReto("CLAN-01", "RETO-01"));
        verify(retoClanRepository, never()).save(any());
    }

    @Test
    @DisplayName("calcularResultadoReto - meta alcanzada supera reto y acredita recompensa al clan")
    void calcularResultadoReto_superaMeta_acreditaRecompensa() {
        // Arrange
        String clanId = "CLAN-01";
        String retoId = "RETO-01";

        Clan clan = new Clan(clanId, "Guerreros");
        clan.setPuntosTotales(600); // 600 >= 500
        ClanEntity clanEntity = new ClanEntity();
        clanEntity.setId(clanId);

        RetoClan reto = new RetoClan(retoId, clanId, "Meta 500", 500, 0, 150,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        RetoClanEntity retoEntity = new RetoClanEntity();
        retoEntity.setId(retoId);

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clanEntity));
        when(entityMapper.toDomain(clanEntity)).thenReturn(clan);
        when(retoClanRepository.findById(retoId)).thenReturn(Optional.of(retoEntity));
        when(retoClanEntityMapper.toDomain(retoEntity)).thenReturn(reto);
        doNothing().when(retoClanValidator).validarEvaluacionReto(reto);

        when(entityMapper.toEntity(clan)).thenReturn(clanEntity);
        when(clanRepository.save(clanEntity)).thenReturn(clanEntity);
        when(retoClanEntityMapper.toEntity(reto)).thenReturn(retoEntity);
        when(retoClanRepository.save(retoEntity)).thenReturn(retoEntity);
        when(retoClanEntityMapper.toDomain(retoEntity)).thenReturn(reto);

        // Act
        RetoClan resultado = service.calcularResultadoReto(clanId, retoId);

        // Assert
        assertNotNull(resultado);
        assertTrue(reto.getCompletado());
        assertEquals(750, clan.getPuntosTotales()); // 600 + 150
        verify(clanRepository).save(clanEntity);
        verify(retoClanRepository).save(retoEntity);
    }

    @Test
    @DisplayName("calcularResultadoReto - meta no alcanzada no otorga recompensa y deja reto no completado")
    void calcularResultadoReto_noAlcanzaMeta_noOtorgaRecompensa() {
        // Arrange
        String clanId = "CLAN-01";
        String retoId = "RETO-01";

        Clan clan = new Clan(clanId, "Guerreros");
        clan.setPuntosTotales(300); // 300 < 500
        ClanEntity clanEntity = new ClanEntity();
        clanEntity.setId(clanId);

        RetoClan reto = new RetoClan(retoId, clanId, "Meta 500", 500, 0, 150,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        RetoClanEntity retoEntity = new RetoClanEntity();
        retoEntity.setId(retoId);

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clanEntity));
        when(entityMapper.toDomain(clanEntity)).thenReturn(clan);
        when(retoClanRepository.findById(retoId)).thenReturn(Optional.of(retoEntity));
        when(retoClanEntityMapper.toDomain(retoEntity)).thenReturn(reto);
        doNothing().when(retoClanValidator).validarEvaluacionReto(reto);

        when(retoClanEntityMapper.toEntity(reto)).thenReturn(retoEntity);
        when(retoClanRepository.save(retoEntity)).thenReturn(retoEntity);
        when(retoClanEntityMapper.toDomain(retoEntity)).thenReturn(reto);

        // Act
        RetoClan resultado = service.calcularResultadoReto(clanId, retoId);

        // Assert
        assertNotNull(resultado);
        assertFalse(reto.getCompletado());
        assertEquals(300, clan.getPuntosTotales()); // No cambió
        verify(clanRepository, never()).save(clanEntity); // No se modificó el clan
        verify(retoClanRepository).save(retoEntity);
    }

    // ========================================================
    // PRUEBAS PARA RF-36: COMPARAR RETO / DUELO ENTRE CLANES
    // ========================================================

    @Test
    @DisplayName("compararRetoEntreClanes - clan 1 con mayor puntaje gana bonificación")
    void compararRetoEntreClanes_clan1Gana_recibeBonificacion() {
        // Arrange
        Clan clan1 = new Clan("C1", "Guerreros");
        clan1.setPuntosTotales(500);
        Clan clan2 = new Clan("C2", "Titanes");
        clan2.setPuntosTotales(300);

        ClanEntity ce1 = new ClanEntity();
        ce1.setId("C1");
        ClanEntity ce2 = new ClanEntity();
        ce2.setId("C2");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(ce1));
        when(clanRepository.findById("C2")).thenReturn(Optional.of(ce2));
        when(entityMapper.toDomain(ce1)).thenReturn(clan1);
        when(entityMapper.toDomain(ce2)).thenReturn(clan2);
        doNothing().when(retoClanValidator).validarDueloClanes("C1", "C2");

        when(entityMapper.toEntity(clan1)).thenReturn(ce1);
        when(clanRepository.save(ce1)).thenReturn(ce1);

        // Act
        ResultadoRetoClanesResponseDTO resultado = service.compararRetoEntreClanes("C1", "C2", 100);

        // Assert
        assertNotNull(resultado);
        assertEquals("C1", resultado.clanGanadorId());
        assertEquals(600, clan1.getPuntosTotales()); // 500 + 100
        assertFalse(resultado.esEmpate());
        verify(clanRepository).save(ce1);
        verify(clanRepository, never()).save(ce2);
    }

    @Test
    @DisplayName("compararRetoEntreClanes - clan 2 con mayor puntaje gana bonificación")
    void compararRetoEntreClanes_clan2Gana_recibeBonificacion() {
        // Arrange
        Clan clan1 = new Clan("C1", "Guerreros");
        clan1.setPuntosTotales(200);
        Clan clan2 = new Clan("C2", "Titanes");
        clan2.setPuntosTotales(450);

        ClanEntity ce1 = new ClanEntity();
        ce1.setId("C1");
        ClanEntity ce2 = new ClanEntity();
        ce2.setId("C2");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(ce1));
        when(clanRepository.findById("C2")).thenReturn(Optional.of(ce2));
        when(entityMapper.toDomain(ce1)).thenReturn(clan1);
        when(entityMapper.toDomain(ce2)).thenReturn(clan2);
        doNothing().when(retoClanValidator).validarDueloClanes("C1", "C2");

        when(entityMapper.toEntity(clan2)).thenReturn(ce2);
        when(clanRepository.save(ce2)).thenReturn(ce2);

        // Act
        ResultadoRetoClanesResponseDTO resultado = service.compararRetoEntreClanes("C1", "C2", 100);

        // Assert
        assertNotNull(resultado);
        assertEquals("C2", resultado.clanGanadorId());
        assertEquals(550, clan2.getPuntosTotales()); // 450 + 100
        assertFalse(resultado.esEmpate());
        verify(clanRepository).save(ce2);
        verify(clanRepository, never()).save(ce1);
    }

    @Test
    @DisplayName("compararRetoEntreClanes - empate de puntajes no otorga bonificación")
    void compararRetoEntreClanes_empate_noOtorgaBonificacion() {
        // Arrange
        Clan clan1 = new Clan("C1", "Guerreros");
        clan1.setPuntosTotales(400);
        Clan clan2 = new Clan("C2", "Titanes");
        clan2.setPuntosTotales(400);

        ClanEntity ce1 = new ClanEntity();
        ce1.setId("C1");
        ClanEntity ce2 = new ClanEntity();
        ce2.setId("C2");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(ce1));
        when(clanRepository.findById("C2")).thenReturn(Optional.of(ce2));
        when(entityMapper.toDomain(ce1)).thenReturn(clan1);
        when(entityMapper.toDomain(ce2)).thenReturn(clan2);
        doNothing().when(retoClanValidator).validarDueloClanes("C1", "C2");

        // Act
        ResultadoRetoClanesResponseDTO resultado = service.compararRetoEntreClanes("C1", "C2", 100);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.esEmpate());
        assertNull(resultado.clanGanadorId());
        assertEquals(400, clan1.getPuntosTotales());
        assertEquals(400, clan2.getPuntosTotales());
        verify(clanRepository, never()).save(any());
    }

    // ==========================================
    // PRUEBAS PARA RF-33: ABANDONAR CLAN
    // ==========================================

    @Test
    @DisplayName("abandonar - el líder sale y el siguiente miembro toma el liderazgo")
    void abandonar_liderSale_reasignaLiderazgo() {
        // Arrange
        Clan clan = new Clan("C1", "Guerreros ECI");
        clan.agregarMiembro("E1");
        clan.agregarMiembro("E2");
        ClanEntity entidad = new ClanEntity();
        entidad.setId("C1");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(clan);
        when(entityMapper.toEntity(clan)).thenReturn(entidad);
        when(clanRepository.save(entidad)).thenReturn(entidad);

        // Act
        service.abandonar("C1", "E1");

        // Assert
        assertEquals(1, clan.getCantidadMiembros());
        assertEquals("E2", clan.getLiderId());
        verify(clanRepository).save(entidad);
    }

    @Test
    @DisplayName("abandonar - el último miembro sale y el clan se elimina")
    void abandonar_ultimoMiembro_eliminaClan() {
        // Arrange
        Clan clan = new Clan("C1", "Guerreros ECI");
        clan.agregarMiembro("E1");
        ClanEntity entidad = new ClanEntity();
        entidad.setId("C1");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(clan);

        // Act
        Clan resultado = service.abandonar("C1", "E1");

        // Assert
        assertEquals(0, resultado.getCantidadMiembros());
        verify(clanRepository).deleteById("C1");
    }

    @Test
    @DisplayName("abandonar - estudiante que no pertenece al clan lanza ReglaDeNegocioException")
    void abandonar_noEsMiembro_lanzaReglaDeNegocio() {
        // Arrange
        Clan clan = new Clan("C1", "Guerreros ECI");
        clan.agregarMiembro("E1");
        ClanEntity entidad = new ClanEntity();
        entidad.setId("C1");

        when(clanRepository.findById("C1")).thenReturn(Optional.of(entidad));
        when(entityMapper.toDomain(entidad)).thenReturn(clan);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> service.abandonar("C1", "E99"));
        verify(clanRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerRetosPorClan - clan existente retorna lista de retos mapeados")
    void obtenerRetosPorClan_clanExistente_retornaLista() {
        // Arrange
        String clanId = "C1";
        ClanEntity clanEntity = new ClanEntity();
        clanEntity.setId(clanId);
        Clan clan = new Clan(clanId, "Guerreros");

        RetoClanEntity rEntity = new RetoClanEntity();
        rEntity.setId("R1");
        rEntity.setClanId(clanId);
        RetoClan rDomain = new RetoClan("R1", clanId, "Reto", 500, 0, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), false);

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clanEntity));
        when(entityMapper.toDomain(clanEntity)).thenReturn(clan);
        when(retoClanRepository.findByClanId(clanId)).thenReturn(List.of(rEntity));
        when(retoClanEntityMapper.toDomain(rEntity)).thenReturn(rDomain);

        // Act
        List<RetoClan> resultado = service.obtenerRetosPorClan(clanId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("R1", resultado.get(0).getId());
    }
}
