package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.TemporadaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.TemporadaResponseDTO;
import co.edu.eci.dosw.ecifit.model.Temporada;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TemporadaMapperTest {

    private final TemporadaMapper mapper = Mappers.getMapper(TemporadaMapper.class);

    @Test
    void debeMapearSolicitudADominio() {
        // Arrange
        TemporadaRequestDTO request = TemporadaRequestDTO.builder()
                .nombre("Semestre 2026-2")
                .fechaInicio(LocalDate.of(2026, 8, 1))
                .fechaFin(LocalDate.of(2026, 12, 15))
                .build();

        // Act
        Temporada temporada = mapper.toDomain(request);

        // Assert
        assertNull(temporada.getId());
        assertEquals(request.getNombre(), temporada.getNombre());
        assertEquals(request.getFechaInicio(), temporada.getFechaInicio());
        assertEquals(request.getFechaFin(), temporada.getFechaFin());
        assertNull(temporada.getEstadoActivo());
    }

    @Test
    void debeMapearDominioARespuesta() {
        // Arrange
        UUID id = UUID.fromString("124b8a11-b1d6-4f38-9af2-1399a26cfe2d");
        Temporada temporada = new Temporada(
                id,
                "Semestre 2026-2",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 12, 15),
                true
        );

        // Act
        TemporadaResponseDTO response = mapper.toResponse(temporada);

        // Assert
        assertEquals(temporada.getId(), response.getId());
        assertEquals(temporada.getNombre(), response.getNombre());
        assertEquals(temporada.getFechaInicio(), response.getFechaInicio());
        assertEquals(temporada.getFechaFin(), response.getFechaFin());
        assertEquals(temporada.getEstadoActivo(), response.getEstadoActivo());
    }

    @Test
    void debeRetornarNullCuandoElOrigenEsNull() {
        // Arrange
        TemporadaRequestDTO request = null;
        Temporada temporada = null;

        // Act
        Temporada dominio = mapper.toDomain(request);
        TemporadaResponseDTO response = mapper.toResponse(temporada);

        // Assert
        assertNull(dominio);
        assertNull(response);
    }
}
