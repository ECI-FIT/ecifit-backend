package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.factory.Mision;

import java.util.List;
import java.util.UUID;

public interface IMisionService {

    Mision generarMisionDiaria(String estudianteId);

    Mision generarMisionDiaria(String estudianteId, String descripcion, Integer recompensa);

    Mision generarMisionSemanal(String estudianteId);

    Mision generarMisionSemanal(String estudianteId, String descripcion, Integer recompensa);

    Mision evaluarCumplimiento(String misionId, Actividad actividad);

    Mision completarMision(UUID id);

    Mision completarMision(String id);

    Mision registrarCumplimiento(String id, String actividadId, String evidencia);

    List<Mision> obtenerPorEstudiante(String estudianteId);
}
