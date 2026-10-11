package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.AsistenciaGym;

public interface IAsistenciaGymService {

    AsistenciaGym registrarAsistencia(String estudianteId, String entrenadorId);
}
