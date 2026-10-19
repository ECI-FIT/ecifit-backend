package co.edu.eci.dosw.ecifit.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "retos_clan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetoClanEntity {

    @Id
    private String id;

    @Column(name = "clan_id", nullable = false)
    private String clanId;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "objetivo_puntos", nullable = false)
    private Integer objetivoPuntos;

    @Column(name = "progreso_actual", nullable = false)
    private Integer progresoActual;

    @Column(nullable = false)
    private Integer recompensa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean completado;
}
