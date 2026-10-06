package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.Temporada;
import co.edu.eci.dosw.ecifit.persistence.TemporadaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TemporadaEntityMapper {

    TemporadaEntity toEntity(Temporada temporada);

    Temporada toDomain(TemporadaEntity entity);
}
