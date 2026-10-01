package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.factory.Mision;

import java.util.List;

public interface IMisionService {

    Mision generarMisionDiaria(String estudianteId);

    Mision generarMisionSemanal(String estudianteId);

    Mision evaluarCumplimiento(String misionId, Actividad actividad);

    List<Mision> obtenerPorEstudiante(String estudianteId);
}
