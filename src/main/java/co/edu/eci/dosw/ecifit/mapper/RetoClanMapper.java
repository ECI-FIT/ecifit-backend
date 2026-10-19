package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.PublicarRetoClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.RetoClanResponseDTO;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RetoClanMapper {

    default RetoClan toDomain(PublicarRetoClanRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new RetoClan(
                null,
                dto.clanId(),
                dto.descripcion(),
                dto.objetivoPuntos(),
                0,
                dto.recompensa(),
                dto.fechaInicio(),
                dto.fechaFin(),
                false
        );
    }

    default RetoClanResponseDTO toResponse(RetoClan reto) {
        if (reto == null) {
            return null;
        }
        String estado;
        if (Boolean.TRUE.equals(reto.getCompletado())) {
            estado = "SUPERADO: +" + reto.getRecompensa() + " pts asignados al clan";
        } else if (reto.getProgresoActual() != null && reto.getProgresoActual() > 0) {
            estado = "EN_PROGRESO: (" + reto.getProgresoActual() + "/" + reto.getObjetivoPuntos() + " pts)";
        } else {
            estado = "PENDIENTE: (0/" + reto.getObjetivoPuntos() + " pts)";
        }

        return new RetoClanResponseDTO(
                reto.getId(),
                reto.getClanId(),
                reto.getDescripcion(),
                reto.getObjetivoPuntos(),
                reto.getProgresoActual(),
                reto.getRecompensa(),
                reto.getFechaInicio(),
                reto.getFechaFin(),
                reto.getCompletado(),
                estado
        );
    }

    default List<RetoClanResponseDTO> toResponseList(List<RetoClan> retos) {
        if (retos == null) {
            return Collections.emptyList();
        }
        return retos.stream().map(this::toResponse).toList();
    }
}
