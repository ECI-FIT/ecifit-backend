package co.edu.eci.dosw.ecifit.dto.response;

import co.edu.eci.dosw.ecifit.model.TipoComida;

import java.time.LocalDate;
import java.util.List;

public record MenuComedorResponseDTO(
        String id,
        LocalDate fecha,
        TipoComida tipo,
        List<PlatoMenuResponseDTO> platos
) {
}
