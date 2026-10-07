package co.edu.eci.dosw.ecifit.security.service;

import co.edu.eci.dosw.ecifit.security.dto.request.LoginRequestDTO;
import co.edu.eci.dosw.ecifit.security.dto.response.AuthResponseDTO;
import co.edu.eci.dosw.ecifit.security.entity.UsuarioEntity;
import co.edu.eci.dosw.ecifit.security.jwt.JwtUtil;
import co.edu.eci.dosw.ecifit.security.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Procesando autenticación para el usuario: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        UsuarioEntity usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + loginRequest.email()));

        String token = jwtUtil.generateToken(usuario);
        log.info("Autenticación exitosa y token JWT generado para el usuario: {}", usuario.getEmail());

        return new AuthResponseDTO(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRol().name(),
                expiration
        );
    }
}
