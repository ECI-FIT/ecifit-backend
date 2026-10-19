package co.edu.eci.dosw.ecifit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporadaRequestDTO {

    @NotBlank(message = "El nombre de la temporada es obligatorio")
    @JsonAlias({"nombre", "nombreTemporada"})
    @Schema(description = "Nombre descriptivo de la temporada", example = "Temporada 2026-1")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonAlias({"fechaInicio", "fecha_inicio", "inicio"})
    @Schema(description = "Fecha inicial de la temporada (YYYY-MM-DD)", example = "2026-01-15")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonAlias({"fechaFin", "fecha_fin", "fin"})
    @Schema(description = "Fecha de finalización de la temporada (YYYY-MM-DD)", example = "2026-06-15")
    private LocalDate fechaFin;
}
