package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.RetoClan;
import co.edu.eci.dosw.ecifit.persistence.RetoClanEntity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RetoClanEntityMapper {

    default RetoClanEntity toEntity(RetoClan domain) {
        if (domain == null) {
            return null;
        }
        return RetoClanEntity.builder()
                .id(domain.getId() != null ? domain.getId() : UUID.randomUUID().toString())
                .clanId(domain.getClanId())
                .descripcion(domain.getDescripcion())
                .objetivoPuntos(domain.getObjetivoPuntos())
                .progresoActual(domain.getProgresoActual() != null ? domain.getProgresoActual() : 0)
                .recompensa(domain.getRecompensa())
                .fechaInicio(domain.getFechaInicio())
                .fechaFin(domain.getFechaFin())
                .completado(Boolean.TRUE.equals(domain.getCompletado()))
                .build();
    }

    default RetoClan toDomain(RetoClanEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RetoClan(
                entity.getId(),
                entity.getClanId(),
                entity.getDescripcion(),
                entity.getObjetivoPuntos(),
                entity.getProgresoActual(),
                entity.getRecompensa(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getCompletado()
        );
    }
}
