package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MisionMapper {

    default MisionResponseDTO toResponse(Mision mision) {
        return new MisionResponseDTO(
                mision.getId(),
                mision.getDescripcion(),
                mision.getRecompensa(),
                Boolean.TRUE.equals(mision.getCompletada()),
                mision.getEstudianteId()
        );
    }
}