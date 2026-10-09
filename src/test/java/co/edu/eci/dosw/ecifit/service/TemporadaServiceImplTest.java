package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.RankingTemporadaEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.TemporadaEntityMapper;
import co.edu.eci.dosw.ecifit.model.RankingTemporada;
import co.edu.eci.dosw.ecifit.model.Temporada;
import co.edu.eci.dosw.ecifit.persistence.RankingTemporadaEntity;
import co.edu.eci.dosw.ecifit.repository.RankingTemporadaRepository;
import co.edu.eci.dosw.ecifit.repository.TemporadaRepository;
import co.edu.eci.dosw.ecifit.validator.ITemporadaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemporadaServiceImplTest {

    @Mock
    private TemporadaRepository temporadaRepository;
    @Mock
    private RankingTemporadaRepository rankingTemporadaRepository;
    @Mock
    private TemporadaEntityMapper temporadaEntityMapper;
    @Mock
    private RankingTemporadaEntityMapper rankingTemporadaEntityMapper;
    @Mock
    private ITemporadaValidator temporadaValidator;
    @InjectMocks
    private TemporadaServiceImpl service;

    private final UUID temporadaId = UUID.randomUUID();

    @Test
    @DisplayName("obtenerRankingGeneral - temporada existente devuelve el ranking ordenado")
    void obtenerRankingGeneral_temporadaExistente_retornaRanking() {
        // Arrange
        RankingTemporadaEntity primeroEntity = new RankingTemporadaEntity();
        RankingTemporadaEntity segundoEntity = new RankingTemporadaEntity();
        RankingTemporada primero = new RankingTemporada();
        primero.setPuntosAcumulados(500);
        RankingTemporada segundo = new RankingTemporada();
        segundo.setPuntosAcumulados(300);

        when(temporadaRepository.existsById(temporadaId)).thenReturn(true);
        when(rankingTemporadaRepository.findByTemporadaIdOrderByPuntosAcumuladosDesc(temporadaId))
                .thenReturn(List.of(primeroEntity, segundoEntity));
        when(rankingTemporadaEntityMapper.toDomain(primeroEntity)).thenReturn(primero);
        when(rankingTemporadaEntityMapper.toDomain(segundoEntity)).thenReturn(segundo);

        // Act
        List<RankingTemporada> resultado = service.obtenerRankingGeneral(temporadaId);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(500, resultado.get(0).getPuntosAcumulados());
        assertEquals(300, resultado.get(1).getPuntosAcumulados());
    }

    @Test
    @DisplayName("obtenerRankingGeneral - temporada inexistente lanza RecursoNoEncontradoException")
    void obtenerRankingGeneral_temporadaInexistente_lanzaExcepcion() {
        // Arrange
        when(temporadaRepository.existsById(temporadaId)).thenReturn(false);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtenerRankingGeneral(temporadaId));
        verify(rankingTemporadaRepository, never()).findByTemporadaIdOrderByPuntosAcumuladosDesc(any());
    }

    @Test
    @DisplayName("obtenerRankingGeneral - temporada sin registros devuelve lista vacía, no null")
    void obtenerRankingGeneral_sinRegistros_devuelveListaVacia() {
        // Arrange
        when(temporadaRepository.existsById(temporadaId)).thenReturn(true);
        when(rankingTemporadaRepository.findByTemporadaIdOrderByPuntosAcumuladosDesc(temporadaId))
                .thenReturn(List.of());

        // Act
        List<RankingTemporada> resultado = service.obtenerRankingGeneral(temporadaId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("crear - ya existe una temporada activa lanza ConflictoException")
    void crear_temporadaActivaExistente_lanzaConflicto() {
        // Arrange
        Temporada temporada = new Temporada(null, "2026-2", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 12, 1), true);
        doThrow(new ConflictoException("Ya existe una temporada activa"))
                .when(temporadaValidator).validarTemporadaUnicaActiva(true);

        // Act & Assert
        assertThrows(ConflictoException.class, () -> service.crear(temporada));
        verify(temporadaRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear - fecha fin anterior a fecha inicio lanza ReglaDeNegocioException")
    void crear_fechasInvalidas_lanzaReglaDeNegocio() {
        // Arrange
        LocalDate inicio = LocalDate.of(2026, 12, 1);
        LocalDate fin = LocalDate.of(2026, 8, 1);
        Temporada temporada = new Temporada(null, "2026-2", inicio, fin, true);
        doThrow(new ReglaDeNegocioException("La fecha de fin debe ser posterior a la fecha de inicio"))
                .when(temporadaValidator).validarFechas(inicio, fin);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> service.crear(temporada));
        verify(temporadaRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerActiva - sin temporada activa lanza RecursoNoEncontradoException")
    void obtenerActiva_sinTemporadaActiva_lanzaExcepcion() {
        // Arrange
        when(temporadaRepository.findByEstadoActivoTrue()).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerActiva());
    }
}
