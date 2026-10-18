package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import co.edu.eci.dosw.ecifit.persistence.MenuComedorEntity;
import co.edu.eci.dosw.ecifit.persistence.PlatoMenuEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuComedorEntityMapper {

    MenuComedorEntity toEntity(MenuComedor menu);

    MenuComedor toDomain(MenuComedorEntity entity);

    PlatoMenuEntity toEntity(PlatoMenu plato);

    PlatoMenu toDomain(PlatoMenuEntity entity);
}
