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
@Table(name = "actividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadEntity {

    @Id
    private String id;

    private String tipo;

    private Integer duracionMinutos;

    private Integer intensidad;

    private LocalDateTime fecha;

    private String estudianteId;

    private Integer puntosOtorgados;
}
