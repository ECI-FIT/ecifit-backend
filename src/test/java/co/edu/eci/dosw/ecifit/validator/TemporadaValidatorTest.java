package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.repository.TemporadaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para TemporadaValidator (Patrón AAA)")
class TemporadaValidatorTest {

    @Mock
    private TemporadaRepository temporadaRepository;

    @InjectMocks
    private TemporadaValidator validator;

    @Test
    @DisplayName("validarFechas - fechas válidas no lanzan excepción")
    void validarFechas_fechasValidas_noLanzaExcepcion() {
        // Arrange
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plusMonths(3);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarFechas(inicio, fin));
    }

    @Test
    @DisplayName("validarFechas - fecha fin anterior o igual a fecha inicio lanza ReglaDeNegocioException")
    void validarFechas_fechaFinInvalida_lanzaReglaDeNegocio() {
        // Arrange
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.minusDays(1);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarFechas(inicio, fin));
    }

    @Test
    @DisplayName("validarFechas - fecha nula lanza ReglaDeNegocioException")
    void validarFechas_fechasNulas_lanzaReglaDeNegocio() {
        // Arrange
        LocalDate inicio = null;
        LocalDate fin = LocalDate.now();

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarFechas(inicio, fin));
    }

    @Test
    @DisplayName("validarTemporadaUnicaActiva - sin temporada activa previa no lanza excepción")
    void validarTemporadaUnicaActiva_sinActivaPrevia_noLanzaExcepcion() {
        // Arrange
        when(temporadaRepository.existsByEstadoActivoTrue()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarTemporadaUnicaActiva(true));
    }

    @Test
    @DisplayName("validarTemporadaUnicaActiva - con temporada activa previa lanza ConflictoException")
    void validarTemporadaUnicaActiva_conActivaPrevia_lanzaConflicto() {
        // Arrange
        when(temporadaRepository.existsByEstadoActivoTrue()).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictoException.class, () -> validator.validarTemporadaUnicaActiva(true));
    }

    @Test
    @DisplayName("validarTemporadaUnicaActiva - estado falso no valida existencia de activa")
    void validarTemporadaUnicaActiva_estadoFalso_noValida() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validarTemporadaUnicaActiva(false));
    }

    @Test
    @DisplayName("validarPuntosPositivos - puntos positivos no lanzan excepción")
    void validarPuntosPositivos_puntosPositivos_noLanzaExcepcion() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validarPuntosPositivos(50));
    }

    @Test
    @DisplayName("validarPuntosPositivos - puntos negativos lanzan ReglaDeNegocioException")
    void validarPuntosPositivos_puntosNegativos_lanzaReglaDeNegocio() {
        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPuntosPositivos(-10));
    }
}
