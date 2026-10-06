package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.RankingTemporada;
import co.edu.eci.dosw.ecifit.persistence.RankingTemporadaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = TemporadaEntityMapper.class)
public interface RankingTemporadaEntityMapper {

    RankingTemporadaEntity toEntity(RankingTemporada rankingTemporada);

    RankingTemporada toDomain(RankingTemporadaEntity entity);
}
