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
        if (usuarioRepository.count() == 0) {
            log.info("Inicializando datos de prueba para seguridad en BD...");

            UsuarioEntity admin = UsuarioEntity.builder()
                    .email("admin@mail.escuelaing.edu.co")
                    .password(passwordEncoder.encode("Admin123*"))
                    .rol(Rol.ADMINISTRADOR)
                    .activo(true)
                    .build();

            UsuarioEntity estudiante = UsuarioEntity.builder()
                    .email("estudiante@mail.escuelaing.edu.co")
                    .password(passwordEncoder.encode("Estudiante123*"))
                    .rol(Rol.ESTUDIANTE)
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);
            usuarioRepository.save(estudiante);

            log.info("Usuarios de prueba creados exitosamente: admin y estudiante");
        }
    }
}
