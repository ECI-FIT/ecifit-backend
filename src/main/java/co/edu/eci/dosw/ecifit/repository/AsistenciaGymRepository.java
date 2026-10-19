package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.AsistenciaGymEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AsistenciaGymRepository extends JpaRepository<AsistenciaGymEntity, String> {

    boolean existsByEstudianteIdAndFechaHoraBetween(
            String estudianteId, LocalDateTime inicioDia, LocalDateTime finDia);
}
