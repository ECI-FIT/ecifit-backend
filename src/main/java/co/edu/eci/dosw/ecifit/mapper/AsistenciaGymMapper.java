package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.response.AsistenciaGymResponseDTO;
import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AsistenciaGymMapper {

    AsistenciaGymResponseDTO toResponse(AsistenciaGym asistenciaGym);
}
