package co.edu.eci.dosw.ecifit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Pruebas unitarias de GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest crearRequest(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    @Test
    @DisplayName("Debe manejar recurso no encontrado con estado 404")
    void debeManejarRecursoNoEncontrado() {

        RecursoNoEncontradoException exception =
                new RecursoNoEncontradoException("Estudiante no encontrado");

        ErrorResponseDTO response = handler.handleNoEncontrado(
                exception,
                crearRequest("/api/estudiantes/E1")
        );

        assertEquals(404, response.status());
        assertEquals("Estudiante no encontrado", response.mensaje());
        assertEquals("/api/estudiantes/E1", response.ruta());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe manejar conflicto con estado 409")
    void debeManejarConflicto() {

        ConflictoException exception =
                new ConflictoException("El estudiante ya existe");

        ErrorResponseDTO response = handler.handleConflicto(
                exception,
                crearRequest("/api/estudiantes")
        );

        assertEquals(409, response.status());
        assertEquals("El estudiante ya existe", response.mensaje());
        assertEquals("/api/estudiantes", response.ruta());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe manejar regla de negocio con estado 422")
    void debeManejarReglaDeNegocio() {

        ReglaDeNegocioException exception =
                new ReglaDeNegocioException("La actividad no cumple la regla");

        ErrorResponseDTO response = handler.handleReglaDeNegocio(
                exception,
                crearRequest("/api/actividades")
        );

        assertEquals(422, response.status());
        assertEquals("La actividad no cumple la regla", response.mensaje());
        assertEquals("/api/actividades", response.ruta());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe manejar estado inválido con estado 422")
    void debeManejarEstadoInvalido() {

        EstadoInvalidoException exception =
                new EstadoInvalidoException("La temporada no está activa");

        ErrorResponseDTO response = handler.handleReglaDeNegocio(
                exception,
                crearRequest("/api/temporadas")
        );

        assertEquals(422, response.status());
        assertEquals("La temporada no está activa", response.mensaje());
        assertEquals("/api/temporadas", response.ruta());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe construir mensaje con los errores de validación")
    void debeManejarValidacion() {

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError errorCorreo = new FieldError(
                "crearEstudianteRequestDTO",
                "correoInstitucional",
                "El correo es obligatorio"
        );

        FieldError errorNombre = new FieldError(
                "crearEstudianteRequestDTO",
                "nombre",
                "El nombre es obligatorio"
        );

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(errorCorreo, errorNombre));

        MethodParameter methodParameter = mock(MethodParameter.class);

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        methodParameter,
                        bindingResult
                );

        ErrorResponseDTO response = handler.handleValidacion(
                exception,
                crearRequest("/api/estudiantes")
        );

        assertEquals(400, response.status());
        assertEquals(
                "correoInstitucional: El correo es obligatorio, nombre: El nombre es obligatorio",
                response.mensaje()
        );
        assertEquals("/api/estudiantes", response.ruta());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe manejar solicitud mal formada con estado 400")
    void debeManejarSolicitudMalFormada() {

        HttpInputMessage inputMessage = mock(HttpInputMessage.class);

        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "JSON inválido",
                        inputMessage
                );

        ErrorResponseDTO response = handler.handleSolicitudMalFormada(
                exception,
                crearRequest("/api/actividades")
        );

        assertEquals(400, response.status());
        assertEquals(
                "La solicitud tiene un formato inválido",
                response.mensaje()
        );
        assertEquals("/api/actividades", response.ruta());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Debe manejar error general con estado 500")
    void debeManejarErrorGeneral() {

        RuntimeException exception =
                new RuntimeException("Error inesperado");

        ErrorResponseDTO response = handler.handleGeneral(
                exception,
                crearRequest("/api/error")
        );

        assertEquals(500, response.status());
        assertEquals(
                "Error interno del servidor",
                response.mensaje()
        );
        assertEquals("/api/error", response.ruta());
        assertNotNull(response.timestamp());
    }
}
