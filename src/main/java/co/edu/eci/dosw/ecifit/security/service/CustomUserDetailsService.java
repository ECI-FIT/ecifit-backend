package co.edu.eci.dosw.ecifit.security.service;

import co.edu.eci.dosw.ecifit.security.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ObjectProvider<UsuarioRepository> usuarioRepositoryProvider;
    private final UsuarioRepository directRepository;

    @Autowired
    public CustomUserDetailsService(ObjectProvider<UsuarioRepository> usuarioRepositoryProvider) {
        this.usuarioRepositoryProvider = usuarioRepositoryProvider;
        this.directRepository = null;
    }

    public CustomUserDetailsService(UsuarioRepository directRepository) {
        this.usuarioRepositoryProvider = null;
        this.directRepository = directRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando usuario por correo: {}", email);
        UsuarioRepository repository = directRepository != null ? directRepository :
                (usuarioRepositoryProvider != null ? usuarioRepositoryProvider.getIfAvailable() : null);

        if (repository == null) {
            throw new UsernameNotFoundException("Repositorio de usuarios no disponible en este contexto");
        }
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con correo: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con correo: " + email);
                });
    }
}
