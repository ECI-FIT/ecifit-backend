package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.InvitacionClanEntityMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.EstadoInvitacion;
import co.edu.eci.dosw.ecifit.model.InvitacionClan;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.repository.InvitacionClanRepository;
import co.edu.eci.dosw.ecifit.validator.IClanValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitacionClanServiceImpl implements IInvitacionClanService {

    private final InvitacionClanRepository invitacionRepository;
    private final EstudianteRepository estudianteRepository;
    private final InvitacionClanEntityMapper entityMapper;
    private final IClanService clanService;
    private final IClanValidator clanValidator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvitacionClan invitar(String clanId, String estudianteId, String invitadoPorId) {
        log.info("Estudiante {} invitando a {} al clan {}", invitadoPorId, estudianteId, clanId);
        Clan clan = clanService.obtenerPorId(clanId);

        if (!clan.getMiembrosIds().contains(invitadoPorId)) {
            throw new ReglaDeNegocioException("Solo un miembro del clan puede enviar invitaciones");
        }
        if (!estudianteRepository.existsById(estudianteId)) {
            throw new RecursoNoEncontradoException("Estudiante", estudianteId);
        }
        clanValidator.validarEstudianteSinClan(estudianteId);
        clanValidator.validarCupoDisponible(clan);
        if (invitacionRepository.existsByClanIdAndEstudianteIdAndEstado(clanId, estudianteId, EstadoInvitacion.PENDIENTE)) {
            throw new ConflictoException("El estudiante ya tiene una invitación pendiente a este clan");
        }

        InvitacionClan invitacion = new InvitacionClan(UUID.randomUUID().toString(), clanId, estudianteId,
                invitadoPorId, EstadoInvitacion.PENDIENTE, LocalDateTime.now());
        invitacionRepository.save(entityMapper.toEntity(invitacion));
        log.info("Invitación {} creada", invitacion.getId());
        return invitacion;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvitacionClan responder(String invitacionId, boolean aceptar) {
        InvitacionClan invitacion = invitacionRepository.findById(invitacionId)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new RecursoNoEncontradoException("Invitación no encontrada con ID: " + invitacionId));

        if (aceptar) {
            invitacion.aceptar();
            clanService.unirse(invitacion.getClanId(), invitacion.getEstudianteId());
        } else {
            invitacion.rechazar();
        }

        invitacionRepository.save(entityMapper.toEntity(invitacion));
        log.info("Invitación {} {}", invitacionId, invitacion.getEstado());
        return invitacion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitacionClan> obtenerPendientes(String estudianteId) {
        return invitacionRepository.findByEstudianteIdAndEstado(estudianteId, EstadoInvitacion.PENDIENTE).stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
