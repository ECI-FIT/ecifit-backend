package co.edu.eci.dosw.ecifit.dto.ia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponseDTO(List<Candidato> candidates) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidato(Contenido content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contenido(List<Parte> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parte(String text) {
    }
}
