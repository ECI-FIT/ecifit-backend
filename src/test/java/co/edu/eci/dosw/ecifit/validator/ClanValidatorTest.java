package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.repository.ClanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClanValidatorTest {

    @Mock
    private ClanRepository clanRepository;
    @InjectMocks
    private ClanValidator validator;

    @Test
    @DisplayName("validarNombreUnico - nombre disponible no lanza excepción")
    void validarNombreUnico_nombreDisponible_noLanzaExcepcion() {
        // Arrange
        when(clanRepository.existsByNombre("Guerreros ECI")).thenReturn(false);

        // Act
        org.junit.jupiter.api.function.Executable accion = () -> validator.validarNombreUnico("Guerreros ECI");

        // Assert
        assertDoesNotThrow(accion);
    }

    @Test
    @DisplayName("validarNombreUnico - nombre duplicado lanza ConflictoException")
    void validarNombreUnico_nombreDuplicado_lanzaConflicto() {
        // Arrange
        when(clanRepository.existsByNombre("Guerreros ECI")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictoException.class,
                () -> validator.validarNombreUnico("Guerreros ECI"));
    }

    @Test
    @DisplayName("validarCupoDisponible - clan con cupo no lanza excepción")
    void validarCupoDisponible_clanConCupo_noLanzaExcepcion() {
        // Arrange
        Clan clan = new Clan("C1", "Guerreros ECI");
        clan.agregarMiembro("E1");

        // Act
        org.junit.jupiter.api.function.Executable accion = () -> validator.validarCupoDisponible(clan);

        // Assert
        assertDoesNotThrow(accion);
    }

    @Test
    @DisplayName("validarCupoDisponible - clan lleno lanza ReglaDeNegocioException")
    void validarCupoDisponible_clanLleno_lanzaReglaDeNegocio() {
        // Arrange
        Clan clan = new Clan("C1", "Guerreros ECI");
        for (int i = 0; i < 5; i++) {
            clan.agregarMiembro("E" + i);
        }

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class,
                () -> validator.validarCupoDisponible(clan));
    }
}