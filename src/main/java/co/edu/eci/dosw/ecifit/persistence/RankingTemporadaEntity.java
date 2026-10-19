package co.edu.eci.dosw.ecifit.persistence;

import co.edu.eci.dosw.ecifit.model.LigaEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "rankings_temporada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingTemporadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID estudianteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "temporada_id", nullable = false)
    private TemporadaEntity temporada;

    private Integer puntosAcumulados;

    @Enumerated(EnumType.STRING)
    private LigaEnum ligaActual;
}
