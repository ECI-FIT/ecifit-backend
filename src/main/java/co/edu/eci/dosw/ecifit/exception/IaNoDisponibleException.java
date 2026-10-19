package co.edu.eci.dosw.ecifit.exception;

/**
 * Indica que el proveedor de IA no pudo entregar una evaluación válida
 * (sin clave, timeout, límite de uso, error del servidor o respuesta inválida).
 * El servicio la captura y deja la comida pendiente; nunca llega al cliente.
 */
public class IaNoDisponibleException extends RuntimeException {

    public IaNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public IaNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
