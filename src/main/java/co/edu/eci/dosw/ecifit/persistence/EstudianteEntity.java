package co.edu.eci.dosw.ecifit.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteEntity {

    @Id
    private String id;

    private String nombre;

    private String correoInstitucional;

    private Integer puntosAcumulados;

    private String rolActivo;

    private String clanId;
}
