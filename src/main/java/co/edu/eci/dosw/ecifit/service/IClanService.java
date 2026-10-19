package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.dto.response.ResultadoRetoClanesResponseDTO;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.RetoClan;

import java.util.List;

public interface IClanService {

    Clan crear(Clan clan);

    Clan unirse(String clanId, String estudianteId);

    Clan obtenerPorId(String id);

    List<Clan> obtenerTodos();

    Clan atacarTorreRival(String clanId, int dano);

    RetoClan publicarRetoSemanal(RetoClan reto);

    RetoClan calcularResultadoReto(String clanId, String retoId);

    ResultadoRetoClanesResponseDTO compararRetoEntreClanes(String clanId1, String clanId2, Integer bonificacion);

    List<RetoClan> obtenerRetosPorClan(String clanId);
}