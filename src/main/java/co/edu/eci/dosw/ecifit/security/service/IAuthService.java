package co.edu.eci.dosw.ecifit.security.service;

import co.edu.eci.dosw.ecifit.security.dto.request.LoginRequestDTO;
import co.edu.eci.dosw.ecifit.security.dto.response.AuthResponseDTO;

public interface IAuthService {

    AuthResponseDTO login(LoginRequestDTO loginRequest);
}
