package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import co.edu.eci.dosw.ecifit.persistence.AsistenciaGymEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AsistenciaGymEntityMapper {

    AsistenciaGymEntity toEntity(AsistenciaGym asistenciaGym);

    @Mapping(target = "puntosOtorgados", ignore = true)
    AsistenciaGym toDomain(AsistenciaGymEntity entity);
}
