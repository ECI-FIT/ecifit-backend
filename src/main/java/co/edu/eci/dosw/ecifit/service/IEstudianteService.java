package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.Estudiante;

import java.util.List;

public interface IEstudianteService {

    Estudiante crearEstudiante(Estudiante estudiante, String rolString);

    Estudiante obtenerPorId(String id);

    List<Estudiante> obtenerTodos();
}
