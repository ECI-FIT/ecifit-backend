package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.PublicarRetoClanRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.RetoClanResponseDTO;
import co.edu.eci.dosw.ecifit.model.RetoClan;
import co.edu.eci.dosw.ecifit.persistence.RetoClanEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas Unitarias para RetoClanMapper y RetoClanEntityMapper (Patrón AAA)")
class RetoClanMapperTest {

    private RetoClanMapper mapper;
    private RetoClanEntityMapper entityMapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RetoClanMapper.class);
        entityMapper = Mappers.getMapper(RetoClanEntityMapper.class);
    }

    @Test
    @DisplayName("RetoClanMapper.toDomain - mapeo correcto de request a dominio")
    void toDomain_requestValido_mapeaCorrectamente() {
        // Arrange
        PublicarRetoClanRequestDTO dto = new PublicarRetoClanRequestDTO(
                "CLAN-01",
                "Acumular 500 puntos colectivos",
                500,
                150,
                LocalDate.of(2026, 10, 18),
                LocalDate.of(2026, 10, 25)
        );

        // Act
        RetoClan domain = mapper.toDomain(dto);

        // Assert
        assertNotNull(domain);
        assertEquals("CLAN-01", domain.getClanId());
        assertEquals("Acumular 500 puntos colectivos", domain.getDescripcion());
        assertEquals(500, domain.getObjetivoPuntos());
        assertEquals(0, domain.getProgresoActual());
        assertEquals(150, domain.getRecompensa());
        assertFalse(domain.getCompletado());
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("RetoClanMapper.toResponse - estados pendiente, en progreso y superado")
    void toResponse_diferentesEstados_mapeaCorrectamente() {
        // Arrange
        RetoClan pendiente = new RetoClan("R1", "C1", "Meta", 500, 0, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        RetoClan enProgreso = new RetoClan("R2", "C1", "Meta", 500, 250, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), false);
        RetoClan superado = new RetoClan("R3", "C1", "Meta", 500, 520, 100,
                LocalDate.now(), LocalDate.now().plusDays(7), true);

        // Act
        RetoClanResponseDTO resPendiente = mapper.toResponse(pendiente);
        RetoClanResponseDTO resEnProgreso = mapper.toResponse(enProgreso);
        RetoClanResponseDTO resSuperado = mapper.toResponse(superado);

        // Assert
        assertNotNull(resPendiente);
        assertTrue(resPendiente.estadoResultado().contains("PENDIENTE"));
        assertTrue(resEnProgreso.estadoResultado().contains("EN_PROGRESO"));
        assertTrue(resSuperado.estadoResultado().contains("SUPERADO"));
        assertNull(mapper.toResponse(null));
    }

    @Test
    @DisplayName("RetoClanMapper.toResponseList - maneja null y listas pobladas")
    void toResponseList_listaValida_retornaDTOs() {
        // Arrange
        RetoClan r = new RetoClan("R1", "C1", "Meta", 500, 0, 100, LocalDate.now(), LocalDate.now().plusDays(7), false);

        // Act & Assert
        assertTrue(mapper.toResponseList(null).isEmpty());
        List<RetoClanResponseDTO> list = mapper.toResponseList(List.of(r));
        assertEquals(1, list.size());
        assertEquals("R1", list.get(0).id());
    }

    @Test
    @DisplayName("RetoClanEntityMapper.toEntity y toDomain - mapeo bidireccional")
    void entityMapper_bidireccional_mapeaCorrectamente() {
        // Arrange
        RetoClan domain = new RetoClan("R1", "C1", "Meta", 500, 100, 150,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 8), true);

        // Act
        RetoClanEntity entity = entityMapper.toEntity(domain);
        RetoClan backToDomain = entityMapper.toDomain(entity);

        // Assert
        assertNotNull(entity);
        assertEquals("R1", entity.getId());
        assertEquals("C1", entity.getClanId());
        assertEquals(500, entity.getObjetivoPuntos());
        assertEquals(100, entity.getProgresoActual());
        assertEquals(150, entity.getRecompensa());
        assertTrue(entity.getCompletado());

        assertNotNull(backToDomain);
        assertEquals(domain.getId(), backToDomain.getId());
        assertEquals(domain.getClanId(), backToDomain.getClanId());

        assertNull(entityMapper.toEntity(null));
        assertNull(entityMapper.toDomain(null));

        // Probar asignación de UUID cuando domain.getId() es null
        RetoClan sinId = new RetoClan(null, "C1", "Meta", 500, 0, 100, null, null, false);
        RetoClanEntity entityGenerado = entityMapper.toEntity(sinId);
        assertNotNull(entityGenerado.getId());
    }
}
