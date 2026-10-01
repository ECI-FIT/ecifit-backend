package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Corredor;
import co.edu.eci.dosw.ecifit.model.strategy.Estratega;
import co.edu.eci.dosw.ecifit.model.strategy.RolTemporada;
import co.edu.eci.dosw.ecifit.model.strategy.Tanque;
import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EstudianteEntityMapper {

    @Mapping(target = "rolActivo", source = "rolActivo", qualifiedByName = "rolToString")
    EstudianteEntity toEntity(Estudiante dominio);

    @Mapping(target = "rolActivo", source = "rolActivo", qualifiedByName = "stringToRol")
    Estudiante toDomain(EstudianteEntity entity);

    @Named("stringToRol")
    default RolTemporada stringToRol(String rol) {
        if (rol == null) return null;
        return switch (rol.trim().toUpperCase()) {
            case "TANQUE" -> new Tanque();
            case "CORREDOR" -> new Corredor();
            case "ESTRATEGA" -> new Estratega();
            default -> null;
        };
    }

    @Named("rolToString")
    default String rolToString(RolTemporada rol) {
        return (rol != null) ? rol.getNombreRol().toUpperCase() : null;
    }
}
