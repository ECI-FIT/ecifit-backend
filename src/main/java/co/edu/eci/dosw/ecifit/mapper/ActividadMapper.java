package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarActividadRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ActividadResponseDTO;
import co.edu.eci.dosw.ecifit.model.Actividad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, LocalDateTime.class})
public interface ActividadMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "fecha", expression = "java(LocalDateTime.now())")
    Actividad toDomain(RegistrarActividadRequestDTO dto);

    @Mapping(target = "id", source = "dominio.id")
    @Mapping(target = "tipo", source = "dominio.tipo")
    @Mapping(target = "duracionMinutos", source = "dominio.duracionMinutos")
    @Mapping(target = "intensidad", source = "dominio.intensidad")
    @Mapping(target = "fecha", source = "dominio.fecha")
    @Mapping(target = "estudianteId", source = "estudianteId")
    @Mapping(target = "puntosOtorgados", source = "puntosOtorgados")
    ActividadResponseDTO toResponse(Actividad dominio, String estudianteId, Integer puntosOtorgados);

    default ActividadResponseDTO toResponse(Actividad dominio) {
        if (dominio == null) return null;
        return toResponse(dominio, null, null);
    }

    default List<ActividadResponseDTO> toResponseList(List<Actividad> dominios) {
        if (dominios == null) return List.of();
        return dominios.stream().map(this::toResponse).toList();
    }

    default List<ActividadResponseDTO> toResponseList(List<Actividad> dominios, String estudianteId) {
        if (dominios == null) return List.of();
        return dominios.stream().map(d -> toResponse(d, estudianteId, null)).toList();
    }
}
