package co.edu.eci.dosw.ecifit.dto.response;

import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.TipoComida;

import java.time.LocalDateTime;
import java.util.List;

public record ComidaResponseDTO(
        String id,
        String estudianteId,
        TipoComida tipo,
        String descripcion,
        LocalDateTime fechaHora,
        EstadoEvaluacion estado,
        Boolean balanceada,
        CalificacionComida calificacion,
        Integer puntaje,
        String consejo,
        List<String> recomendaciones,
        Integer puntosOtorgados,
        String aviso
) {
}
