package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.CrearMenuComedorRequestDTO;
import co.edu.eci.dosw.ecifit.dto.request.PlatoMenuRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MenuComedorResponseDTO;
import co.edu.eci.dosw.ecifit.dto.response.PlatoMenuResponseDTO;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuComedorMapper {

    @Mapping(target = "id", ignore = true)
    MenuComedor toDomain(CrearMenuComedorRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    PlatoMenu toDomain(PlatoMenuRequestDTO dto);

    MenuComedorResponseDTO toResponse(MenuComedor menu);

    PlatoMenuResponseDTO toResponse(PlatoMenu plato);

    List<MenuComedorResponseDTO> toResponseList(List<MenuComedor> menus);
}
