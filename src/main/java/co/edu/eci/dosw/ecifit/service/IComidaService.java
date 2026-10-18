package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.Comida;

import java.time.LocalDate;
import java.util.List;

public interface IComidaService {

    Comida registrar(Comida comida);

    Comida obtenerPorId(String id);

    List<Comida> obtenerHistorial(String estudianteId, LocalDate fecha);
}
