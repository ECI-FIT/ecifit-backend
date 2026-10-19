package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.InvitacionClan;
import co.edu.eci.dosw.ecifit.persistence.InvitacionClanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvitacionClanEntityMapper {

    InvitacionClanEntity toEntity(InvitacionClan invitacion);

    InvitacionClan toDomain(InvitacionClanEntity entity);
}
