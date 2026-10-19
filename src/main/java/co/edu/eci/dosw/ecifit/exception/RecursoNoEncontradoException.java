package co.edu.eci.dosw.ecifit.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(String.format("%s no encontrado con ID: %s", recurso, id));
    }
}
