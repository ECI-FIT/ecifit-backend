package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.Actividad;

import java.util.List;

public interface IActividadService {

    Actividad registrarActividad(Actividad actividad, String estudianteId);

    List<Actividad> obtenerHistorialPorEstudiante(String estudianteId);
}
