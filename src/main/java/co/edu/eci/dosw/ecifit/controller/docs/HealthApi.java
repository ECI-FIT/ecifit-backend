package co.edu.eci.dosw.ecifit.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Health", description = "Estado del servicio")
public interface HealthApi {

    @Operation(summary = "Verificar que el backend está corriendo")
    @ApiResponse(responseCode = "200", description = "El servicio está activo")
    ResponseEntity<String> health();
}
