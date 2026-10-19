package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import org.springframework.stereotype.Component;

@Component
public class MenuComedorValidator implements IMenuComedorValidator {

    @Override
    public void validar(MenuComedor menu) {
        if (menu == null) {
            throw new ReglaDeNegocioException("El menú es obligatorio.");
        }
        if (menu.getFecha() == null) {
            throw new ReglaDeNegocioException("La fecha del menú es obligatoria.");
        }
        if (menu.getTipo() == null) {
            throw new ReglaDeNegocioException("El tipo de comida del menú es obligatorio.");
        }
        if (menu.getPlatos().isEmpty()) {
            throw new ReglaDeNegocioException("El menú debe tener al menos un plato.");
        }
        for (PlatoMenu plato : menu.getPlatos()) {
            if (plato == null || plato.getNombre() == null || plato.getNombre().isBlank()) {
                throw new ReglaDeNegocioException("Todos los platos deben tener nombre.");
            }
            if (plato.getPrecio() != null && plato.getPrecio() < 0) {
                throw new ReglaDeNegocioException("El precio de un plato no puede ser negativo.");
            }
        }
    }
}
