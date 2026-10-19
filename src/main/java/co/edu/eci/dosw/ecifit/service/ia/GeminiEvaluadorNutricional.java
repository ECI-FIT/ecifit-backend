package co.edu.eci.dosw.ecifit.service.ia;

import co.edu.eci.dosw.ecifit.config.GeminiProperties;
import co.edu.eci.dosw.ecifit.dto.ia.EvaluacionGeminiDTO;
import co.edu.eci.dosw.ecifit.dto.ia.GeminiRequestDTO;
import co.edu.eci.dosw.ecifit.dto.ia.GeminiResponseDTO;
import co.edu.eci.dosw.ecifit.exception.IaNoDisponibleException;
import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.EvaluacionNutricional;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class GeminiEvaluadorNutricional implements IEvaluadorNutricional {

    private static final String HEADER_API_KEY = "x-goog-api-key";

    private final RestClient restClient;
    private final GeminiProperties properties;
    private final ObjectMapper objectMapper;

    public GeminiEvaluadorNutricional(@Qualifier("geminiRestClient") RestClient restClient,
                                      GeminiProperties properties,
                                      ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public EvaluacionNutricional evaluar(TipoComida tipo, String descripcion) {
        if (!properties.tieneApiKey()) {
            throw new IaNoDisponibleException("La clave de Gemini no está configurada (GEMINI_API_KEY).");
        }

        GeminiResponseDTO respuesta = llamarGemini(construirPeticion(tipo, descripcion));
        EvaluacionGeminiDTO evaluacion = leerEvaluacion(respuesta);
        return convertirADominio(evaluacion);
    }

    private GeminiRequestDTO construirPeticion(TipoComida tipo, String descripcion) {
        return new GeminiRequestDTO(
                GeminiRequestDTO.Contenido.deTexto(null, PromptsNutricion.INSTRUCCION_SISTEMA),
                List.of(GeminiRequestDTO.Contenido.deTexto("user",
                        PromptsNutricion.mensajeUsuario(tipo, descripcion))),
                new GeminiRequestDTO.ConfiguracionGeneracion(
                        MediaType.APPLICATION_JSON_VALUE,
                        PromptsNutricion.ESQUEMA_RESPUESTA,
                        PromptsNutricion.TEMPERATURA)
        );
    }

    private GeminiResponseDTO llamarGemini(GeminiRequestDTO peticion) {
        try {
            return restClient.post()
                    .uri("/models/{modelo}:generateContent", properties.modelo())
                    .header(HEADER_API_KEY, properties.apiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(peticion)
                    .retrieve()
                    .body(GeminiResponseDTO.class);
        } catch (HttpStatusCodeException e) {
            log.warn("Gemini respondió con error HTTP {}", e.getStatusCode().value());
            throw new IaNoDisponibleException("Gemini respondió con error HTTP " + e.getStatusCode().value(), e);
        } catch (RestClientException e) {
            log.warn("No fue posible comunicarse con Gemini: {}", e.getMessage());
            throw new IaNoDisponibleException("No fue posible comunicarse con Gemini.", e);
        }
    }

    private EvaluacionGeminiDTO leerEvaluacion(GeminiResponseDTO respuesta) {
        String texto = extraerTexto(respuesta);
        try {
            return objectMapper.readValue(texto, EvaluacionGeminiDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("Gemini devolvió un JSON inválido");
            throw new IaNoDisponibleException("Gemini devolvió un JSON inválido.", e);
        }
    }

    private String extraerTexto(GeminiResponseDTO respuesta) {
        if (respuesta == null || respuesta.candidates() == null || respuesta.candidates().isEmpty()) {
            throw new IaNoDisponibleException("Gemini no devolvió candidatos.");
        }
        GeminiResponseDTO.Contenido contenido = respuesta.candidates().get(0).content();
        if (contenido == null || contenido.parts() == null || contenido.parts().isEmpty()
                || contenido.parts().get(0).text() == null || contenido.parts().get(0).text().isBlank()) {
            throw new IaNoDisponibleException("Gemini devolvió una respuesta vacía.");
        }
        return contenido.parts().get(0).text();
    }

    private EvaluacionNutricional convertirADominio(EvaluacionGeminiDTO dto) {
        if (dto.balanceada() == null || dto.calificacion() == null || dto.puntaje() == null
                || dto.consejo() == null || dto.consejo().isBlank()) {
            throw new IaNoDisponibleException("La evaluación de Gemini está incompleta.");
        }
        if (dto.puntaje() < 0 || dto.puntaje() > 100) {
            throw new IaNoDisponibleException("El puntaje de Gemini está fuera de rango: " + dto.puntaje());
        }
        CalificacionComida calificacion;
        try {
            calificacion = CalificacionComida.valueOf(dto.calificacion().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IaNoDisponibleException("Calificación desconocida de Gemini: " + dto.calificacion(), e);
        }
        return new EvaluacionNutricional(
                dto.balanceada(), calificacion, dto.puntaje(), dto.consejo().trim(), dto.recomendaciones());
    }
}
