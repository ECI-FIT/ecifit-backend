package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.dto.response.ResultadoRetoClanesResponseDTO;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.ClanEntityMapper;
import co.edu.eci.dosw.ecifit.mapper.RetoClanEntityMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import co.edu.eci.dosw.ecifit.persistence.RetoClanEntity;
import co.edu.eci.dosw.ecifit.repository.ClanRepository;
import co.edu.eci.dosw.ecifit.repository.RetoClanRepository;
import co.edu.eci.dosw.ecifit.validator.IClanValidator;
import co.edu.eci.dosw.ecifit.validator.IRetoClanValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClanServiceImpl implements IClanService {

    private final ClanRepository clanRepository;
    private final ClanEntityMapper entityMapper;
    private final IClanValidator validator;
    private final RetoClanRepository retoClanRepository;
    private final RetoClanEntityMapper retoClanEntityMapper;
    private final IRetoClanValidator retoClanValidator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Clan crear(Clan clan) {
        log.info("Creando clan: nombre={}", clan.getNombre());
        validator.validarNombreUnico(clan.getNombre());

        clan.setId(UUID.randomUUID().toString());
        ClanEntity guardado = clanRepository.save(entityMapper.toEntity(clan));

        log.info("Clan creado exitosamente: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return entityMapper.toDomain(guardado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Clan unirse(String clanId, String estudianteId) {
        log.info("Estudiante {} intentando unirse al clan {}", estudianteId, clanId);
        Clan clan = obtenerPorId(clanId);

        validator.validarEstudianteSinClan(estudianteId);
        validator.validarCupoDisponible(clan);
        clan.agregarMiembro(estudianteId);

        ClanEntity actualizado = clanRepository.save(entityMapper.toEntity(clan));
        log.info("Estudiante {} unido al clan {}. Miembros actuales: {}",
                estudianteId, clanId, actualizado.getMiembrosIds().size());
        return entityMapper.toDomain(actualizado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Clan abandonar(String clanId, String estudianteId) {
        log.info("Estudiante {} abandonando el clan {}", estudianteId, clanId);
        Clan clan = obtenerPorId(clanId);
        clan.removerMiembro(estudianteId);

        if (clan.getCantidadMiembros() == 0) {
            clanRepository.deleteById(clanId);
            log.info("Clan {} eliminado porque se quedó sin miembros", clanId);
            return clan;
        }

        ClanEntity actualizado = clanRepository.save(entityMapper.toEntity(clan));
        log.info("Estudiante {} salió del clan {}. Líder actual: {}", estudianteId, clanId, clan.getLiderId());
        return entityMapper.toDomain(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Clan obtenerPorId(String id) {
        ClanEntity entity = clanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Clan no encontrado: id={}", id);
                    return new RecursoNoEncontradoException("Clan con id " + id + " no encontrado");
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Clan> obtenerTodos() {
        return clanRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Clan atacarTorreRival(String clanId, int dano) {
        Clan clan = obtenerPorId(clanId);
        clan.recibirDano(dano);

        ClanEntity actualizado = clanRepository.save(entityMapper.toEntity(clan));
        log.info("Clan {} recibió {} de daño. Salud restante: {}", clanId, dano, actualizado.getSaludTorre());
        return entityMapper.toDomain(actualizado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RetoClan publicarRetoSemanal(RetoClan reto) {
        log.info("Publicando reto semanal para clan: clanId={}", reto.getClanId());
        retoClanValidator.validarPublicacionReto(reto);

        if (!clanRepository.existsById(reto.getClanId())) {
            log.warn("No se puede publicar reto: clan no encontrado id={}", reto.getClanId());
            throw new RecursoNoEncontradoException("Clan con id " + reto.getClanId() + " no encontrado");
        }

        reto.setId(UUID.randomUUID().toString());
        reto.setProgresoActual(0);
        reto.setCompletado(false);

        RetoClanEntity guardado = retoClanRepository.save(retoClanEntityMapper.toEntity(reto));
        log.info("Reto semanal publicado exitosamente: id={}, clanId={}", guardado.getId(), guardado.getClanId());
        return retoClanEntityMapper.toDomain(guardado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RetoClan calcularResultadoReto(String clanId, String retoId) {
        log.info("Calculando resultado del reto {} para el clan {}", retoId, clanId);

        Clan clan = obtenerPorId(clanId);
        RetoClanEntity retoEntity = retoClanRepository.findById(retoId)
                .orElseThrow(() -> {
                    log.warn("Reto no encontrado: id={}", retoId);
                    return new RecursoNoEncontradoException("Reto de clan con id " + retoId + " no encontrado");
                });

        RetoClan reto = retoClanEntityMapper.toDomain(retoEntity);

        if (!clanId.equals(reto.getClanId())) {
            throw new ReglaDeNegocioException("El reto " + retoId + " no pertenece al clan " + clanId);
        }

        retoClanValidator.validarEvaluacionReto(reto);

        int puntosActualesClan = (clan.getPuntosTotales() != null) ? clan.getPuntosTotales() : 0;
        boolean superado = reto.evaluarSuperacion(puntosActualesClan);

        if (superado) {
            log.info("¡Reto {} superado por clan {}! Otorgando bonificación de {} pts", retoId, clanId, reto.getRecompensa());
            clan.setPuntosTotales(puntosActualesClan + reto.getRecompensa());
            clanRepository.save(entityMapper.toEntity(clan));
        } else {
            log.info("Reto {} no superado por clan {}. Puntos actuales: {}, Meta: {}",
                    retoId, clanId, puntosActualesClan, reto.getObjetivoPuntos());
        }

        RetoClanEntity actualizado = retoClanRepository.save(retoClanEntityMapper.toEntity(reto));
        return retoClanEntityMapper.toDomain(actualizado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultadoRetoClanesResponseDTO compararRetoEntreClanes(String clanId1, String clanId2, Integer bonificacion) {
        log.info("Iniciando duelo / comparativa entre clanes {} y {}", clanId1, clanId2);
        retoClanValidator.validarDueloClanes(clanId1, clanId2);

        Clan clan1 = obtenerPorId(clanId1);
        Clan clan2 = obtenerPorId(clanId2);

        int puntos1 = (clan1.getPuntosTotales() != null) ? clan1.getPuntosTotales() : 0;
        int puntos2 = (clan2.getPuntosTotales() != null) ? clan2.getPuntosTotales() : 0;
        int bonus = (bonificacion != null && bonificacion > 0) ? bonificacion : 0;

        String clanGanadorId = null;
        boolean esEmpate = false;
        String mensaje;

        if (puntos1 > puntos2) {
            clanGanadorId = clan1.getId();
            clan1.setPuntosTotales(puntos1 + bonus);
            clanRepository.save(entityMapper.toEntity(clan1));
            mensaje = "¡Victoria para " + clan1.getNombre() + "! +" + bonus + " pts otorgados.";
            log.info("Ganador del duelo de clanes: {} (+{} pts)", clan1.getNombre(), bonus);
        } else if (puntos2 > puntos1) {
            clanGanadorId = clan2.getId();
            clan2.setPuntosTotales(puntos2 + bonus);
            clanRepository.save(entityMapper.toEntity(clan2));
            mensaje = "¡Victoria para " + clan2.getNombre() + "! +" + bonus + " pts otorgados.";
            log.info("Ganador del duelo de clanes: {} (+{} pts)", clan2.getNombre(), bonus);
        } else {
            esEmpate = true;
            mensaje = "¡Empate técnico entre " + clan1.getNombre() + " y " + clan2.getNombre() + "! No se asignan puntos de bonificación.";
            log.info("Duelo entre {} y {} finalizó en empate", clan1.getNombre(), clan2.getNombre());
        }

        return new ResultadoRetoClanesResponseDTO(
                clan1.getId(),
                clan1.getNombre(),
                puntos1,
                clan2.getId(),
                clan2.getNombre(),
                puntos2,
                clanGanadorId,
                bonus,
                esEmpate,
                mensaje
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RetoClan> obtenerRetosPorClan(String clanId) {
        obtenerPorId(clanId);
        return retoClanRepository.findByClanId(clanId).stream()
                .map(retoClanEntityMapper::toDomain)
                .toList();
    }
}