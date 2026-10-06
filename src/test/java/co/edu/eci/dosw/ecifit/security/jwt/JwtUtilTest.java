package co.edu.eci.dosw.ecifit.security.jwt;

import co.edu.eci.dosw.ecifit.security.entity.UsuarioEntity;
import co.edu.eci.dosw.ecifit.security.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas Unitarias para JwtUtil (JJWT 0.12.3 - Patrón AAA)")
class JwtUtilTest {

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final String ALTERNATIVE_SECRET = "515E635266556A586E3272357538782F413F4428472B4B6250645367566B5971";
    private static final long EXPIRATION_MS = 3600000; // 1 hora

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("Debe generar token válido y extraer correctamente subject y role")
    void debeGenerarTokenYExtraerClaimsCorrectamente() {
        // Arrange
        String email = "juan.perez@mail.escuelaing.edu.co";
        Rol rol = Rol.ESTUDIANTE;

        // Act
        String token = jwtUtil.generateToken(email, rol);
        String extractedSubject = jwtUtil.extractUsername(token);
        String extractedRole = jwtUtil.extractRole(token);

        // Assert
        assertNotNull(token);
        assertEquals(email, extractedSubject);
        assertEquals(rol.name(), extractedRole);
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Debe validar exitosamente token activo para el UserDetails correspondiente")
    void debeValidarTokenActivoParaUsuarioCorrecto() {
        // Arrange
        String email = "admin@mail.escuelaing.edu.co";
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id("USR-1")
                .email(email)
                .password("$2a$10$encodedpassword")
                .rol(Rol.ADMINISTRADOR)
                .activo(true)
                .build();
        String token = jwtUtil.generateToken(usuario);

        // Act
        boolean esValido = jwtUtil.validateToken(token, usuario);

        // Assert
        assertTrue(esValido);
    }

    @Test
    @DisplayName("Debe detectar y rechazar token cuando ha expirado")
    void debeRechazarTokenExpirado() {
        // Arrange
        String email = "entrenador@mail.escuelaing.edu.co";
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id("USR-2")
                .email(email)
                .password("$2a$10$encodedpassword")
                .rol(Rol.ENTRENADOR)
                .activo(true)
                .build();
        // Generar token con expiración en el pasado (-10000 ms)
        String expiredToken = jwtUtil.generateToken(email, Rol.ENTRENADOR, -10000);

        // Act
        boolean expirado = jwtUtil.isTokenExpired(expiredToken);
        boolean esValidoParaUsuario = jwtUtil.validateToken(expiredToken, usuario);
        boolean esValidoGeneral = jwtUtil.validateToken(expiredToken);

        // Assert
        assertTrue(expirado);
        assertFalse(esValidoParaUsuario);
        assertFalse(esValidoGeneral);
    }

    @Test
    @DisplayName("Debe rechazar token firmado con una clave diferente o manipulada")
    void debeRechazarTokenConFirmaInvalida() {
        // Arrange
        JwtUtil alterJwtUtil = new JwtUtil(ALTERNATIVE_SECRET, EXPIRATION_MS);
        String email = "intruso@mail.escuelaing.edu.co";
        String tokenConOtraFirma = alterJwtUtil.generateToken(email, Rol.ESTUDIANTE);

        UsuarioEntity usuario = UsuarioEntity.builder()
                .id("USR-3")
                .email(email)
                .password("password")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();

        // Act
        boolean esValido = jwtUtil.validateToken(tokenConOtraFirma, usuario);
        boolean esValidoGeneral = jwtUtil.validateToken(tokenConOtraFirma);

        // Assert
        assertFalse(esValido);
        assertFalse(esValidoGeneral);
    }

    @Test
    @DisplayName("Debe rechazar token si el subject no coincide con el UserDetails")
    void debeRechazarTokenCuandoUsuarioNoCoincide() {
        // Arrange
        String token = jwtUtil.generateToken("usuario.a@mail.escuelaing.edu.co", Rol.ESTUDIANTE);
        UsuarioEntity otroUsuario = UsuarioEntity.builder()
                .id("USR-4")
                .email("usuario.b@mail.escuelaing.edu.co")
                .password("password")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();

        // Act
        boolean esValido = jwtUtil.validateToken(token, otroUsuario);

        // Assert
        assertFalse(esValido);
    }

    @Test
    @DisplayName("Debe rechazar un token malformado o corrupto")
    void debeRechazarTokenMalformado() {
        // Arrange
        String tokenMalformado = "eyJhbGciOiJIUzI1NiJ9.payloadinvalido.firmainvalida";
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id("USR-5")
                .email("usuario@mail.escuelaing.edu.co")
                .password("password")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();

        // Act
        boolean esValido = jwtUtil.validateToken(tokenMalformado, usuario);
        boolean esValidoGeneral = jwtUtil.validateToken(tokenMalformado);

        // Assert
        assertFalse(esValido);
        assertFalse(esValidoGeneral);
    }
}
