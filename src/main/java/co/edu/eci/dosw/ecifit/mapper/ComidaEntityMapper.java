package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.persistence.ComidaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComidaEntityMapper {

    ComidaEntity toEntity(Comida comida);

    Comida toDomain(ComidaEntity entity);
}
