package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.RankingTemporada;
import co.edu.eci.dosw.ecifit.model.Temporada;

import java.util.List;
import java.util.UUID;

public interface ITemporadaService {

    Temporada crear(Temporada temporada);

    Temporada obtenerPorId(UUID id);

    Temporada obtenerActiva();

    List<RankingTemporada> obtenerRankingGeneral(UUID temporadaId);
}
