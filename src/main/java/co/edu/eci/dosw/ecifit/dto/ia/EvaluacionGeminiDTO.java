package co.edu.eci.dosw.ecifit.dto.ia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Estructura JSON que Gemini devuelve dentro del texto de su respuesta.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EvaluacionGeminiDTO(
        Boolean balanceada,
        String calificacion,
        Integer puntaje,
        String consejo,
        List<String> recomendaciones
) {
}
