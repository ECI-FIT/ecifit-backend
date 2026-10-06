package co.edu.eci.dosw.ecifit.dto.response;

import co.edu.eci.dosw.ecifit.model.LigaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingTemporadaResponseDTO {

    private UUID id;
    private UUID estudianteId;
    private Integer puntosAcumulados;
    private LigaEnum ligaActual;
}
