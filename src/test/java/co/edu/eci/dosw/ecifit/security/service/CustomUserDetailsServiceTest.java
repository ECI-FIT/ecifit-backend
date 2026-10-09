package co.edu.eci.dosw.ecifit.security.service;

import co.edu.eci.dosw.ecifit.security.entity.UsuarioEntity;
import co.edu.eci.dosw.ecifit.security.enums.Rol;
import co.edu.eci.dosw.ecifit.security.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Pruebas Unitarias para CustomUserDetailsService (Patrón AAA)")
class CustomUserDetailsServiceTest {

    private UsuarioRepository usuarioRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        userDetailsService = new CustomUserDetailsService(usuarioRepository);
    }

    @Test
    @DisplayName("Debe cargar UserDetails correctamente cuando el usuario existe en BD")
    void debeCargarUsuarioExistente() {
        // Arrange
        String email = "estudiante@mail.escuelaing.edu.co";
        UsuarioEntity usuario = UsuarioEntity.builder()
                .id("USR-10")
                .email(email)
                .password("$2a$10$encoded")
                .rol(Rol.ESTUDIANTE)
                .activo(true)
                .build();
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails resultado = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(resultado);
        assertEquals(email, resultado.getUsername());
        assertEquals("$2a$10$encoded", resultado.getPassword());
        assertTrue(resultado.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE")));
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException cuando el usuario no existe en BD")
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        // Arrange
        String email = "inexistente@mail.escuelaing.edu.co";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
        verify(usuarioRepository).findByEmail(email);
    }
}
