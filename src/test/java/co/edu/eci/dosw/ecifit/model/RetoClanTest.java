package co.edu.eci.dosw.ecifit.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas Unitarias para Entidad de Dominio RetoClan (Patrón AAA)")
class RetoClanTest {

    @Test
    @DisplayName("estaVigente - fecha dentro del rango devuelve true")
    void estaVigente_fechaEnRango_retornaTrue() {
        // Arrange
        LocalDate inicio = LocalDate.of(2026, 10, 10);
        LocalDate fin = LocalDate.of(2026, 10, 20);
        RetoClan reto = new RetoClan("R1", "C1", "Reto", 500, 0, 100, inicio, fin, false);

        // Act
        boolean vigente = reto.estaVigente(LocalDate.of(2026, 10, 15));

        // Assert
        assertTrue(vigente);
    }

    @Test
    @DisplayName("estaVigente - fecha fuera de rango o nula devuelve false")
    void estaVigente_fechaFueraDeRango_retornaFalse() {
        // Arrange
        LocalDate inicio = LocalDate.of(2026, 10, 10);
        LocalDate fin = LocalDate.of(2026, 10, 20);
        RetoClan reto = new RetoClan("R1", "C1", "Reto", 500, 0, 100, inicio, fin, false);

        // Act & Assert
        assertFalse(reto.estaVigente(LocalDate.of(2026, 10, 5)));
        assertFalse(reto.estaVigente(LocalDate.of(2026, 10, 25)));
        assertFalse(reto.estaVigente(null));

        RetoClan retoSinFechas = new RetoClan();
        assertFalse(retoSinFechas.estaVigente(LocalDate.now()));
    }

    @Test
    @DisplayName("evaluarSuperacion - puntos mayores o iguales al objetivo completa el reto")
    void evaluarSuperacion_puntosSuperanObjetivo_completaReto() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto", 500, 0, 100, LocalDate.now(), LocalDate.now().plusDays(7), false);

        // Act
        boolean superado = reto.evaluarSuperacion(550);

        // Assert
        assertTrue(superado);
        assertTrue(reto.getCompletado());
        assertEquals(550, reto.getProgresoActual());
    }

    @Test
    @DisplayName("evaluarSuperacion - puntos menores al objetivo no completa el reto")
    void evaluarSuperacion_puntosMenores_noCompletaReto() {
        // Arrange
        RetoClan reto = new RetoClan("R1", "C1", "Reto", 500, 0, 100, LocalDate.now(), LocalDate.now().plusDays(7), false);

        // Act
        boolean superado = reto.evaluarSuperacion(400);

        // Assert
        assertFalse(superado);
        assertFalse(reto.getCompletado());
        assertEquals(400, reto.getProgresoActual());
    }

    @Test
    @DisplayName("equals y hashCode - igualdad basada en id")
    void equalsYHashCode_mismoId_sonIguales() {
        // Arrange
        RetoClan r1 = new RetoClan("R1", "C1", "A", 100, 0, 10, null, null, false);
        RetoClan r2 = new RetoClan("R1", "C2", "B", 200, 0, 20, null, null, false);
        RetoClan r3 = new RetoClan("R2", "C1", "A", 100, 0, 10, null, null, false);

        // Act & Assert
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r1, null);
        assertNotEquals(r1, new Object());
    }
}
