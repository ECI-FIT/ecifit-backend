package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.persistence.ActividadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActividadEntityMapper {

    default ActividadEntity toEntity(Actividad dominio) {
        if (dominio == null) return null;
        return toEntity(dominio, null, null);
    }

    @Mapping(target = "id", source = "dominio.id")
    @Mapping(target = "tipo", source = "dominio.tipo")
    @Mapping(target = "duracionMinutos", source = "dominio.duracionMinutos")
    @Mapping(target = "intensidad", source = "dominio.intensidad")
    @Mapping(target = "fecha", source = "dominio.fecha")
    @Mapping(target = "estudianteId", source = "estudianteId")
    @Mapping(target = "puntosOtorgados", source = "puntosOtorgados")
    ActividadEntity toEntity(Actividad dominio, String estudianteId, Integer puntosOtorgados);

    Actividad toDomain(ActividadEntity entity);
}
