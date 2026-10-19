package co.edu.eci.dosw.ecifit.dto.request;

import co.edu.eci.dosw.ecifit.model.TipoComida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CrearMenuComedorRequestDTO(
        @NotNull(message = "La fecha del menú es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El tipo de comida es obligatorio")
        TipoComida tipo,

        @NotEmpty(message = "El menú debe tener al menos un plato")
        List<@Valid PlatoMenuRequestDTO> platos
) {
}
