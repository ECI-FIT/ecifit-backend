package co.edu.eci.dosw.ecifit.persistence;

import co.edu.eci.dosw.ecifit.model.CalificacionComida;
import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComidaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String estudianteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComida tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEvaluacion estado;

    private Integer intentosEvaluacion;

    private Boolean otorgaPuntos;

    private Boolean balanceada;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CalificacionComida calificacion;

    private Integer puntaje;

    @Column(length = 1000)
    private String consejo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comida_recomendaciones", joinColumns = @JoinColumn(name = "comida_id"))
    @Column(name = "recomendacion", length = 300)
    @Builder.Default
    private List<String> recomendaciones = new ArrayList<>();

    private Integer puntosOtorgados;
}
