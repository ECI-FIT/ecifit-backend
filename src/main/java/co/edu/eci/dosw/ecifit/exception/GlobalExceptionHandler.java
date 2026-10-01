package co.edu.eci.dosw.ecifit.exception;

import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({RecursoNoEncontradoException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNoEncontrado(Exception ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: ruta={}, mensaje={}", request.getRequestURI(), ex.getMessage());
        return new ErrorResponseDTO(404, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConflictoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleConflicto(ConflictoException ex, HttpServletRequest request) {
        log.warn("Conflicto: ruta={}, mensaje={}", request.getRequestURI(), ex.getMessage());
        return new ErrorResponseDTO(409, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({ReglaDeNegocioException.class, EstadoInvalidoException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handleReglaDeNegocio(RuntimeException ex, HttpServletRequest request) {
        log.warn("Regla de negocio rechazada: ruta={}, mensaje={}", request.getRequestURI(), ex.getMessage());
        return new ErrorResponseDTO(422, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Datos inválidos: ruta={}, errores={}", request.getRequestURI(), mensaje);
        return new ErrorResponseDTO(400, mensaje, request.getRequestURI());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleSolicitudMalFormada(Exception ex, HttpServletRequest request) {
        log.warn("Solicitud mal formada: ruta={}", request.getRequestURI());
        return new ErrorResponseDTO(400, "La solicitud tiene un formato inválido", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado: ruta={}", request.getRequestURI(), ex);
        return new ErrorResponseDTO(500, "Error interno del servidor", request.getRequestURI());
    }
}
