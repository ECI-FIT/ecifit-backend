package co.edu.eci.dosw.ecifit.security.controller;

import co.edu.eci.dosw.ecifit.controller.docs.AuthApi;
import co.edu.eci.dosw.ecifit.security.dto.request.LoginRequestDTO;
import co.edu.eci.dosw.ecifit.security.dto.response.AuthResponseDTO;
import co.edu.eci.dosw.ecifit.security.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final IAuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
