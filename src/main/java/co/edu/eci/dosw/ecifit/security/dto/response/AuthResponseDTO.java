package co.edu.eci.dosw.ecifit.security.dto.response;

public record AuthResponseDTO(
        String token,
        String tipo,
        String email,
        String rol,
        long expiresIn
) {
    public AuthResponseDTO(String token, String email, String rol, long expiresIn) {
        this(token, "Bearer", email, rol, expiresIn);
    }
}
