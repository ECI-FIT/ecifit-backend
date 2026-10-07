package co.edu.eci.dosw.ecifit.controller.docs;

import co.edu.eci.dosw.ecifit.dto.response.ErrorResponseDTO;
import co.edu.eci.dosw.ecifit.security.dto.request.LoginRequestDTO;
import co.edu.eci.dosw.ecifit.security.dto.response.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y gestión de credenciales JWT")
public interface AuthApi {

    @Operation(summary = "Iniciar sesión en el sistema y obtener token Bearer JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token emitido",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Formato de credenciales inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario no autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest);
}
