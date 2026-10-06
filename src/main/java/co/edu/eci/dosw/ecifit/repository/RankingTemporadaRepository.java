package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.RankingTemporadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RankingTemporadaRepository extends JpaRepository<RankingTemporadaEntity, UUID> {

    Optional<RankingTemporadaEntity> findByEstudianteIdAndTemporadaId(UUID estudianteId, UUID temporadaId);

    List<RankingTemporadaEntity> findByTemporadaIdOrderByPuntosAcumuladosDesc(UUID temporadaId);
}
