package co.edu.eci.dosw.ecifit.validator;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para EstudianteValidator (Patrón AAA)")
class EstudianteValidatorTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private EstudianteValidator validator;

    @Test
    @DisplayName("Debe validar exitosamente cuando los datos cumplen todas las reglas")
    void debeValidarExitosamente() {
        // Arrange
        Estudiante estudiante = new Estudiante("E1", "Andres", "andres@mail.escuelaing.edu.co", null);
        String rol = "TANQUE";
        when(estudianteRepository.existsByCorreoInstitucional("andres@mail.escuelaing.edu.co")).thenReturn(false);

        // Act
        org.junit.jupiter.api.function.Executable accion = () -> validator.validar(estudiante, rol);

        // Assert
        assertDoesNotThrow(accion);
    }

    @Test
    @DisplayName("Debe rechazar correos que no pertenezcan al dominio institucional")
    void debeRechazarCorreoNoInstitucional() {
        // Arrange
        Estudiante estudiante = new Estudiante("E1", "Andres", "andres@gmail.com", null);

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> validator.validar(estudiante, "CORREDOR"));
        assertEquals("Solo se permite el dominio institucional @mail.escuelaing.edu.co", ex.getMessage());
    }

    @Test
    @DisplayName("Debe rechazar roles inválidos")
    void debeRechazarRolInvalido() {
        // Arrange
        Estudiante estudiante = new Estudiante("E1", "Andres", "andres@mail.escuelaing.edu.co", null);

        // Act & Assert
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> validator.validar(estudiante, "MAGO"));
        assertTrue(ex.getMessage().contains("Rol inválido"));
    }

    @Test
    @DisplayName("Debe rechazar cuando el correo ya existe en BD")
    void debeRechazarCorreoDuplicado() {
        // Arrange
        Estudiante estudiante = new Estudiante("E1", "Andres", "andres@mail.escuelaing.edu.co", null);
        when(estudianteRepository.existsByCorreoInstitucional("andres@mail.escuelaing.edu.co")).thenReturn(true);

        // Act & Assert
        ConflictoException ex = assertThrows(ConflictoException.class,
                () -> validator.validar(estudiante, "ESTRATEGA"));
        assertEquals("El correo institucional ya se encuentra registrado", ex.getMessage());
    }
}
