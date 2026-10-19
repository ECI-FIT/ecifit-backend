package co.edu.eci.dosw.ecifit.model.strategy;

/**
 * Interfaz Strategy para el cálculo de puntos según el rol asumido por el estudiante en la temporada.
 * Cada rol premia diferentes atributos de esfuerzo físico y disciplina.
 */
public interface RolTemporada {

    /**
     * Calcula los puntos base generados por una actividad según la fórmula del rol.
     *
     * @param duracion   Duración de la actividad en minutos.
     * @param intensidad Nivel de intensidad percibida (1 a 10).
     * @return Puntos base calculados.
     */
    Integer calcularPuntosBase(Integer duracion, Integer intensidad);

    /**
     * Retorna el nombre del rol asignado.
     *
     * @return Nombre descriptivo del rol.
     */
    String getNombreRol();
}
