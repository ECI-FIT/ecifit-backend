package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.CrearClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ClanResponseDTO;
import co.edu.eci.dosw.ecifit.model.Clan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClanMapper {

    default Clan toDomain(CrearClanRequestDTO dto) {
        Clan clan = new Clan();
        clan.setNombre(dto.nombre());
        clan.agregarMiembro(dto.liderEstudianteId());
        return clan;
    }

    default ClanResponseDTO toResponse(Clan clan) {
        return new ClanResponseDTO(
                clan.getId(),
                clan.getNombre(),
                clan.getSaludTorre(),
                clan.getPuntosTotales(),
                clan.getCantidadMiembros()
        );
    }
}
  