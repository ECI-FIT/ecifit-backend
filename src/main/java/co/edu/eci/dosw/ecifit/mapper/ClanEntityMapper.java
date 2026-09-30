package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClanEntityMapper {

    default ClanEntity toEntity(Clan clan) {
        ClanEntity entity = new ClanEntity();
        entity.setId(clan.getId() != null ? clan.getId() : UUID.randomUUID().toString());
        entity.setNombre(clan.getNombre());
        entity.setSaludTorre(clan.getSaludTorre());
        entity.setPuntosTotales(clan.getPuntosTotales());
        entity.setMiembrosIds(new ArrayList<>(clan.getMiembrosIds()));
        return entity;
    }

    default Clan toDomain(ClanEntity entity) {
        Clan clan = new Clan(entity.getId(), entity.getNombre());
        clan.setSaludTorre(entity.getSaludTorre());
        clan.setPuntosTotales(entity.getPuntosTotales());
        if (entity.getMiembrosIds() != null) {
            entity.getMiembrosIds().forEach(clan::agregarMiembro);
        }
        return clan;
    }
}