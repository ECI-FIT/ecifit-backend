package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarComidaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ComidaResponseDTO;
import co.edu.eci.dosw.ecifit.model.Comida;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComidaMapper {

    String AVISO_NUTRICIONAL =
            "Orientación general generada por IA; no reemplaza la valoración de un profesional de la salud.";

    default Comida toDomain(RegistrarComidaRequestDTO dto) {
        if (dto == null) return null;
        String descripcion = dto.descripcion() != null ? dto.descripcion().trim() : null;
        return new Comida(dto.estudianteId(), dto.tipo(), descripcion, dto.fechaHora());
    }

    @Mapping(target = "aviso", constant = AVISO_NUTRICIONAL)
    ComidaResponseDTO toResponse(Comida comida);

    List<ComidaResponseDTO> toResponseList(List<Comida> comidas);
}
