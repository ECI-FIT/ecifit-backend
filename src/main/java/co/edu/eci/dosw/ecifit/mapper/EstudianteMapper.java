package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.CrearEstudianteRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.EstudianteResponseDTO;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Corredor;
import co.edu.eci.dosw.ecifit.model.strategy.Estratega;
import co.edu.eci.dosw.ecifit.model.strategy.RolTemporada;
import co.edu.eci.dosw.ecifit.model.strategy.Tanque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class})
public interface EstudianteMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "puntosAcumulados", constant = "0")
    @Mapping(target = "rolActivo", source = "rol", qualifiedByName = "stringToRol")
    @Mapping(target = "clanId", ignore = true)
    Estudiante toDomain(CrearEstudianteRequestDTO dto);

    @Mapping(target = "rolActivo", source = "rolActivo", qualifiedByName = "rolToString")
    EstudianteResponseDTO toResponse(Estudiante dominio);

    List<EstudianteResponseDTO> toResponseList(List<Estudiante> dominios);

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
        return (rol != null) ? rol.getNombreRol() : null;
    }
}
