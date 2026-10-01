package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.model.factory.MisionDiaria;
import co.edu.eci.dosw.ecifit.model.factory.MisionSemanal;
import co.edu.eci.dosw.ecifit.persistence.MisionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MisionEntityMapper {

    String TIPO_DIARIA = "DIARIA";
    String TIPO_SEMANAL = "SEMANAL";

    default MisionEntity toEntity(Mision mision) {
        MisionEntity entity = new MisionEntity();
        entity.setId(mision.getId());
        entity.setDescripcion(mision.getDescripcion());
        entity.setRecompensa(mision.getRecompensa());
        entity.setCompletada(Boolean.TRUE.equals(mision.getCompletada()));
        entity.setEstudianteId(mision.getEstudianteId());
        entity.setTipo(mision instanceof MisionDiaria ? TIPO_DIARIA : TIPO_SEMANAL);
        return entity;
    }

    default Mision toDomain(MisionEntity entity) {
        Mision mision = TIPO_DIARIA.equals(entity.getTipo())
                ? new MisionDiaria(entity.getId(), entity.getDescripcion(), entity.getRecompensa(), 30)
                : new MisionSemanal(entity.getId(), entity.getDescripcion(), entity.getRecompensa(), 150);
        mision.setCompletada(entity.isCompletada());
        mision.setEstudianteId(entity.getEstudianteId());
        return mision;
    }
}
  