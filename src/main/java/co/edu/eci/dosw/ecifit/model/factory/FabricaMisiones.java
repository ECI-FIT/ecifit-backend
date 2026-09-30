package co.edu.eci.dosw.ecifit.model.factory;

/**
 * Interfaz Abstract Factory para la creación desacoplada de familias de misiones
 * (diarias, semanales, o temáticas según el periodo de gamificación).
 */
public interface FabricaMisiones {

    /**
     * Crea una instancia de misión de periodicidad diaria.
     *
     * @return {@link Mision} diaria configurada.
     */
    Mision crearMisionDiaria();

    /**
     * Crea una instancia de misión de periodicidad semanal.
     *
     * @return {@link Mision} semanal configurada.
     */
    Mision crearMisionSemanal();
}
