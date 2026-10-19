package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.response.InvitacionClanResponseDTO;
import co.edu.eci.dosw.ecifit.model.InvitacionClan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvitacionClanMapper {

    InvitacionClanResponseDTO toResponse(InvitacionClan invitacion);
}
