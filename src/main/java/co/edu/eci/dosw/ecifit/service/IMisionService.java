package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.dto.request.CrearMisionRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MisionResponseDTO;
import co.edu.eci.dosw.ecifit.model.Actividad;
import co.edu.eci.dosw.ecifit.model.factory.Mision;

import java.util.List;
import java.util.UUID;

public interface IMisionService {

    Mision generarMisionDiaria(String estudianteId);

    Mision generarMisionDiaria(CrearMisionRequestDTO dto);

    Mision generarMisionSemanal(String estudianteId);

    Mision generarMisionSemanal(CrearMisionRequestDTO dto);

    Mision evaluarCumplimiento(String misionId, Actividad actividad);

    MisionResponseDTO completarMision(UUID id);

    MisionResponseDTO completarMision(String id);

    List<Mision> obtenerPorEstudiante(String estudianteId);
}
