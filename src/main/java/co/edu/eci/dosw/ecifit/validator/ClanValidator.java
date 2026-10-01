package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClanValidator implements IClanValidator {

    private static final int LIMITE_MIEMBROS = 5;

    private final ClanRepository clanRepository;

    @Override
    public void validarNombreUnico(String nombre) {
        if (clanRepository.existsByNombre(nombre)) {
            throw new ConflictoException("Ya existe un clan con el nombre '" + nombre + "'");
        }
    }

    @Override
    public void validarCupoDisponible(Clan clan) {
        if (clan.getCantidadMiembros() >= LIMITE_MIEMBROS) {
            throw new ReglaDeNegocioException("El clan '" + clan.getNombre() + "' ya alcanzo el limite de "
                    + LIMITE_MIEMBROS + " miembros");
        }
    }
}