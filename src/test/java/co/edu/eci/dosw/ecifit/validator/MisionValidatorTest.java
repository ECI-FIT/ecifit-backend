package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.factory.MisionDiaria;
import co.edu.eci.dosw.ecifit.persistence.MisionEntity;
import co.edu.eci.dosw.ecifit.repository.MisionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MisionValidatorTest {

    @Mock
    private MisionRepository misionRepository;
    @InjectMocks
    private MisionValidator validator;

    @Test
    @DisplayName("validarNoCompletada - misión pendiente no lanza excepción")
    void validarNoCompletada_misionPendiente_noLanzaExcepcion() {
        MisionDiaria mision = new MisionDiaria("M1", "Caminar 30 min", 50, 30);

        assertDoesNotThrow(() -> validator.validarNoCompletada(mision));
    }

    @Test
    @DisplayName("validarNoCompletada - misión completada lanza EstadoInvalidoException")
    void validarNoCompletada_misionCompletada_lanzaEstadoInvalido() {
        MisionDiaria mision = new MisionDiaria("M1", "Caminar 30 min", 50, 30);
        mision.setCompletada(true);

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarNoCompletada(mision));
    }

    @Test
    @DisplayName("validarLimiteMisionesActivas - estudiante con cupo no lanza excepción")
    void validarLimiteMisionesActivas_conCupo_noLanzaExcepcion() {
        MisionEntity activa = new MisionEntity();
        activa.setCompletada(false);
        when(misionRepository.findByEstudianteId("E1")).thenReturn(List.of(activa));

        assertDoesNotThrow(() -> validator.validarLimiteMisionesActivas("E1"));
    }

    @Test
    @DisplayName("validarLimiteMisionesActivas - tres misiones activas lanza ReglaDeNegocioException")
    void validarLimiteMisionesActivas_limiteAlcanzado_lanzaReglaDeNegocio() {
        MisionEntity activa1 = new MisionEntity();
        activa1.setCompletada(false);
        MisionEntity activa2 = new MisionEntity();
        activa2.setCompletada(false);
        MisionEntity activa3 = new MisionEntity();
        activa3.setCompletada(false);
        when(misionRepository.findByEstudianteId("E1")).thenReturn(List.of(activa1, activa2, activa3));

        assertThrows(ReglaDeNegocioException.class,
                () -> validator.validarLimiteMisionesActivas("E1"));
    }
}