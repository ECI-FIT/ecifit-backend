package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuComedorValidatorTest {

    private final MenuComedorValidator validator = new MenuComedorValidator();

    private static MenuComedor menuValido() {
        return new MenuComedor(null, LocalDate.now(), TipoComida.ALMUERZO,
                List.of(new PlatoMenu(null, "Pollo a la plancha", "Con arroz y ensalada", 12000)));
    }

    @Test
    void debeAceptarMenuValido() {
        MenuComedor menu = menuValido();

        assertDoesNotThrow(() -> validator.validar(menu));
    }

    @Test
    void debeRechazarMenuNulo() {
        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(null));
    }

    @Test
    void debeRechazarMenuSinFecha() {
        MenuComedor menu = menuValido();
        menu.setFecha(null);

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> validator.validar(menu));

        assertEquals("La fecha del menú es obligatoria.", ex.getMessage());
    }

    @Test
    void debeRechazarMenuSinTipo() {
        MenuComedor menu = menuValido();
        menu.setTipo(null);

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(menu));
    }

    @Test
    void debeRechazarMenuSinPlatos() {
        MenuComedor menu = menuValido();
        menu.setPlatos(List.of());

        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> validator.validar(menu));

        assertEquals("El menú debe tener al menos un plato.", ex.getMessage());
    }

    @Test
    void debeRechazarPlatoSinNombre() {
        MenuComedor menu = menuValido();
        menu.setPlatos(List.of(new PlatoMenu(null, "  ", null, null)));

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(menu));
    }

    @Test
    void debeRechazarPlatoConPrecioNegativo() {
        MenuComedor menu = menuValido();
        menu.setPlatos(List.of(new PlatoMenu(null, "Sopa", null, -1)));

        assertThrows(ReglaDeNegocioException.class, () -> validator.validar(menu));
    }
}
