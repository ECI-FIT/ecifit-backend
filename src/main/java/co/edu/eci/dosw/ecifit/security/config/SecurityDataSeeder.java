package co.edu.eci.dosw.ecifit.security.config;

import co.edu.eci.dosw.ecifit.security.entity.UsuarioEntity;
import co.edu.eci.dosw.ecifit.security.enums.Rol;
import co.edu.eci.dosw.ecifit.security.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityDataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearUsuarioSiNoExiste("admin@mail.escuelaing.edu.co", "Admin123*", Rol.ADMINISTRADOR);
        crearUsuarioSiNoExiste("estudiante@mail.escuelaing.edu.co", "Estudiante123*", Rol.ESTUDIANTE);
        crearUsuarioSiNoExiste("entrenador@mail.escuelaing.edu.co", "Entrenador123*", Rol.ENTRENADOR);
    }

    private void crearUsuarioSiNoExiste(String email, String rawPassword, Rol rol) {
        if (!usuarioRepository.existsByEmail(email)) {
            UsuarioEntity usuario = UsuarioEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .rol(rol)
                    .activo(true)
                    .build();
            usuarioRepository.save(usuario);
            log.info("Usuario de seguridad sembrado: {}", email);
        }
    }
}
