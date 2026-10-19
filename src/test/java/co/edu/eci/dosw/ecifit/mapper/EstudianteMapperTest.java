package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.CrearEstudianteRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.EstudianteResponseDTO;
import co.edu.eci.dosw.ecifit.model.Estudiante;
import co.edu.eci.dosw.ecifit.model.strategy.Corredor;
import co.edu.eci.dosw.ecifit.model.strategy.Estratega;
import co.edu.eci.dosw.ecifit.model.strategy.RolTemporada;
import co.edu.eci.dosw.ecifit.model.strategy.Tanque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de EstudianteMapper")
class EstudianteMapperTest {

    /**
     * Implementación mínima para probar los métodos default
     * de la interfaz EstudianteMapper sin depender de MapStruct.
     */
    private final EstudianteMapper mapper = new EstudianteMapper() {

        @Override
        public Estudiante toDomain(CrearEstudianteRequestDTO dto) {
            return null;
        }

        @Override
        public EstudianteResponseDTO toResponse(Estudiante dominio) {
            return null;
        }

        @Override
        public List<EstudianteResponseDTO> toResponseList(List<Estudiante> dominios) {
            return null;
        }
    };

    @Test
    @DisplayName("Debe convertir TANQUE a la estrategia Tanque")
    void debeConvertirTanque() {
        RolTemporada rol = mapper.stringToRol("TANQUE");

        assertNotNull(rol);
        assertInstanceOf(Tanque.class, rol);
    }

    @Test
    @DisplayName("Debe convertir CORREDOR a la estrategia Corredor")
    void debeConvertirCorredor() {
        RolTemporada rol = mapper.stringToRol("CORREDOR");

        assertNotNull(rol);
        assertInstanceOf(Corredor.class, rol);
    }

    @Test
    @DisplayName("Debe convertir ESTRATEGA a la estrategia Estratega")
    void debeConvertirEstratega() {
        RolTemporada rol = mapper.stringToRol("ESTRATEGA");

        assertNotNull(rol);
        assertInstanceOf(Estratega.class, rol);
    }

    @Test
    @DisplayName("Debe aceptar espacios y diferencias de mayúsculas en el rol")
    void debeNormalizarRol() {
        RolTemporada rol = mapper.stringToRol("  tanque  ");

        assertNotNull(rol);
        assertInstanceOf(Tanque.class, rol);
    }

    @Test
    @DisplayName("Debe retornar null cuando el rol es null")
    void debeRetornarNullConRolNulo() {
        assertNull(mapper.stringToRol(null));
    }

    @Test
    @DisplayName("Debe retornar null cuando el rol no existe")
    void debeRetornarNullConRolDesconocido() {
        assertNull(mapper.stringToRol("DEPORTISTA"));
    }

    @Test
    @DisplayName("Debe convertir una estrategia a su nombre")
    void debeConvertirRolAString() {
        assertEquals("Tanque", mapper.rolToString(new Tanque()));
        assertEquals("Corredor", mapper.rolToString(new Corredor()));
        assertEquals("Estratega", mapper.rolToString(new Estratega()));
    }

    @Test
    @DisplayName("Debe retornar null cuando la estrategia es null")
    void debeRetornarNullConEstrategiaNula() {
        assertNull(mapper.rolToString(null));
    }
}