package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.mapper.ClanEntityMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import co.edu.eci.dosw.ecifit.repository.ClanRepository;
import co.edu.eci.dosw.ecifit.validator.IClanValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClanServiceImpl implements IClanService {

    private final ClanRepository clanRepository;
    private final ClanEntityMapper entityMapper;
    private final IClanValidator validator;

    @Override
    public Clan crear(Clan clan) {
        log.info("Creando clan: nombre={}", clan.getNombre());
        validator.validarNombreUnico(clan.getNombre());

        clan.setId(UUID.randomUUID().toString());
        ClanEntity guardado = clanRepository.save(entityMapper.toEntity(clan));

        log.info("Clan creado exitosamente: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return entityMapper.toDomain(guardado);
    }

    @Override
    public Clan unirse(String clanId, String estudianteId) {
        log.info("Estudiante {} intentando unirse al clan {}", estudianteId, clanId);
        Clan clan = obtenerPorId(clanId);

        validator.validarCupoDisponible(clan);
        clan.agregarMiembro(estudianteId);

        ClanEntity actualizado = clanRepository.save(entityMapper.toEntity(clan));
        log.info("Estudiante {} unido al clan {}. Miembros actuales: {}",
                estudianteId, clanId, actualizado.getMiembrosIds().size());
        return entityMapper.toDomain(actualizado);
    }

    @Override
    public Clan obtenerPorId(String id) {
        ClanEntity entity = clanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clan no encontrado: id={}", id);
                    return new RecursoNoEncontradoException("Clan con id " + id + " no encontrado");
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    public List<Clan> obtenerTodos() {
        return clanRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Clan atacarTorreRival(String clanId, int dano) {
        Clan clan = obtenerPorId(clanId);
        clan.recibirDano(dano);

        ClanEntity actualizado = clanRepository.save(entityMapper.toEntity(clan));
        log.info("Clan {} recibió {} de daño. Salud restante: {}", clanId, dano, actualizado.getSaludTorre());
        return entityMapper.toDomain(actualizado);
    }
}