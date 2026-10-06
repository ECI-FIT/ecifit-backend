package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.TemporadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemporadaRepository extends JpaRepository<TemporadaEntity, UUID> {

    Optional<TemporadaEntity> findByEstadoActivoTrue();

    boolean existsByEstadoActivoTrue();
}
