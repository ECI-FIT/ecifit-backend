package co.edu.eci.dosw.ecifit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ia.gemini")
public record GeminiProperties(
        String apiKey,
        String modelo,
        String urlBase,
        Integer timeoutSegundos
) {

    private static final String MODELO_POR_DEFECTO = "gemini-2.5-flash-lite";
    private static final String URL_POR_DEFECTO = "https://generativelanguage.googleapis.com/v1beta";
    private static final int TIMEOUT_POR_DEFECTO = 10;

    public GeminiProperties {
        modelo = (modelo == null || modelo.isBlank()) ? MODELO_POR_DEFECTO : modelo;
        urlBase = (urlBase == null || urlBase.isBlank()) ? URL_POR_DEFECTO : urlBase;
        timeoutSegundos = (timeoutSegundos == null || timeoutSegundos <= 0) ? TIMEOUT_POR_DEFECTO : timeoutSegundos;
    }

    public boolean tieneApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
