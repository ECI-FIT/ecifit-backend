package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.TemporadaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.TemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.model.Temporada;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TemporadaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoActivo", ignore = true)
    Temporada toDomain(TemporadaRequestDTO dto);

    TemporadaResponseDTO toResponse(Temporada temporada);
}
