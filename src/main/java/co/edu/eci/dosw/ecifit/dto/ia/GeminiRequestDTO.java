package co.edu.eci.dosw.ecifit.dto.ia;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiRequestDTO(
        Contenido systemInstruction,
        List<Contenido> contents,
        ConfiguracionGeneracion generationConfig
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Contenido(String role, List<Parte> parts) {

        public static Contenido deTexto(String role, String texto) {
            return new Contenido(role, List.of(new Parte(texto)));
        }
    }

    public record Parte(String text) {
    }

    public record ConfiguracionGeneracion(
            String responseMimeType,
            Map<String, Object> responseSchema,
            Double temperature
    ) {
    }
}
