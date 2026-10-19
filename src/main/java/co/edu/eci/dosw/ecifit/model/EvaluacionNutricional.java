package co.edu.eci.dosw.ecifit.model;

import java.util.List;

/**
 * Resultado de la evaluación nutricional de una comida, generado por el proveedor de IA.
 * Es solo una orientación general; los puntos los calcula el dominio, nunca la IA.
 *
 * @param balanceada      si la comida se considera balanceada
 * @param calificacion    calificación general de la comida
 * @param puntaje         puntaje nutricional entre 0 y 100
 * @param consejo         retroalimentación breve sobre la comida
 * @param recomendaciones sugerencias concretas para mejorarla
 */
public record EvaluacionNutricional(
        boolean balanceada,
        CalificacionComida calificacion,
        int puntaje,
        String consejo,
        List<String> recomendaciones
) {

    public EvaluacionNutricional {
        recomendaciones = recomendaciones != null ? List.copyOf(recomendaciones) : List.of();
    }
}
