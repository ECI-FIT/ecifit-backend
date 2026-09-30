package co.edu.eci.dosw.ecifit.model.state;

/**
 * Interfaz que define el contrato del patrón State para el ciclo académico.
 * Permite variar el comportamiento del sistema (como ataques a torres o multiplicadores de puntos)
 * según el estado actual de la temporada universitaria.
 */
public interface EstadoTemporada {

    /**
     * Determina si durante este estado de la temporada se permite atacar torres en las guerras de clanes.
     *
     * @return true si los ataques están habilitados, false de lo contrario.
     */
    Boolean permiteAtaqueTorres();

    /**
     * Obtiene el multiplicador de puntos aplicable a las actividades durante este estado.
     *
     * @return factor multiplicador numérico.
     */
    Double obtenerMultiplicador();
}
