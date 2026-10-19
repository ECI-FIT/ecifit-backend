package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.InvitacionClan;

import java.util.List;

public interface IInvitacionClanService {

    InvitacionClan invitar(String clanId, String estudianteId, String invitadoPorId);

    InvitacionClan responder(String invitacionId, boolean aceptar);

    List<InvitacionClan> obtenerPendientes(String estudianteId);
}
