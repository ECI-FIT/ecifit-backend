package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.response.RankingTemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.model.RankingTemporada;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RankingTemporadaMapper {

    RankingTemporadaResponseDTO toResponse(RankingTemporada rankingTemporada);
}
