package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.Clan;

import java.util.List;

public interface IClanService {

    Clan crear(Clan clan);

    Clan unirse(String clanId, String estudianteId);

    Clan obtenerPorId(String id);

    List<Clan> obtenerTodos();

    Clan atacarTorreRival(String clanId, int dano);
}