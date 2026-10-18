package co.edu.eci.dosw.ecifit.mapper;

import co.edu.eci.dosw.ecifit.dto.request.RegistrarComidaRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.ComidaResponseDTO;
import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.Comida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.EvaluacionNutricional;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComidaMapperTest {

    private final ComidaMapper mapper = Mappers.getMapper(ComidaMapper.class);

    @Test
    void debeConvertirLaSolicitudADominioRecortandoLaDescripcion() {
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 20, 7, 30);
        RegistrarComidaRequestDTO dto = new RegistrarComidaRequestDTO(
                "EST-1", TipoComida.DESAYUNO, "  Arepa con huevo  ", fecha);

        Comida comida = mapper.toDomain(dto);

        assertEquals("EST-1", comida.getEstudianteId());
        assertEquals(TipoComida.DESAYUNO, comida.getTipo());
        assertEquals("Arepa con huevo", comida.getDescripcion());
        assertEquals(fecha, comida.getFechaHora());
        assertEquals(EstadoEvaluacion.PENDIENTE, comida.getEstado());
    }

    @Test
    void debeRetornarNuloCuandoLaSolicitudEsNula() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void debeConvertirComidaEvaluadaARespuestaConAviso() {
        Comida comida = new Comida("EST-1", TipoComida.ALMUERZO, "Pollo con arroz", LocalDateTime.now());
        comida.setId("C-1");
        comida.aplicarEvaluacion(new EvaluacionNutricional(
                true, CalificacionComida.BUENA, 80, "Buen almuerzo.", List.of("Toma agua")));

        ComidaResponseDTO respuesta = mapper.toResponse(comida);

        assertEquals("C-1", respuesta.id());
        assertEquals(EstadoEvaluacion.EVALUADA, respuesta.estado());
        assertTrue(respuesta.balanceada());
        assertEquals(CalificacionComida.BUENA, respuesta.calificacion());
        assertEquals("Buen almuerzo.", respuesta.consejo());
        assertEquals(List.of("Toma agua"), respuesta.recomendaciones());
        assertEquals(20, respuesta.puntosOtorgados());
        assertEquals(ComidaMapper.AVISO_NUTRICIONAL, respuesta.aviso());
    }

    @Test
    void debeConvertirListaDeComidas() {
        Comida comida = new Comida("EST-1", TipoComida.CENA, "Sopa", LocalDateTime.now());

        List<ComidaResponseDTO> respuestas = mapper.toResponseList(List.of(comida));

        assertEquals(1, respuestas.size());
        assertEquals(TipoComida.CENA, respuestas.get(0).tipo());
    }
}
