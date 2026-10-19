package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.response.AsistenciaGymResponseDTO;
import co.edu.eci.dosw.ecifit.model.AsistenciaGym;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AsistenciaGymMapperTest {

    private final AsistenciaGymMapper mapper = Mappers.getMapper(AsistenciaGymMapper.class);

    @Test
    void debeMapearDominioARespuestaSinPerderInformacion() {
        // Arrange
        LocalDateTime fechaHora = LocalDateTime.of(2026, 10, 5, 18, 30);
        AsistenciaGym asistencia = new AsistenciaGym(
                "ASIS-1", "EST-1", "ENT-1", fechaHora);

        // Act
        AsistenciaGymResponseDTO response = mapper.toResponse(asistencia);

        // Assert
        assertEquals(asistencia.getId(), response.id());
        assertEquals(asistencia.getEstudianteId(), response.estudianteId());
        assertEquals(asistencia.getEntrenadorId(), response.entrenadorId());
        assertEquals(asistencia.getFechaHora(), response.fechaHora());
        assertEquals(asistencia.getPuntosOtorgados(), response.puntosOtorgados());
    }

    @Test
    void debeRetornarNullCuandoElDominioEsNull() {
        // Arrange
        AsistenciaGym asistencia = null;

        // Act
        AsistenciaGymResponseDTO response = mapper.toResponse(asistencia);

        // Assert
        assertNull(response);
    }
}
