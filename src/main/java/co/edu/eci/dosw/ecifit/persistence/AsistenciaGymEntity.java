package co.edu.eci.dosw.ecifit.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "asistencias_gym")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaGymEntity {

    @Id
    private String id;

    private String estudianteId;

    private String entrenadorId;

    private LocalDateTime fechaHora;

    private Integer puntosOtorgados;
}
