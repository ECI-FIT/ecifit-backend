package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.model.EstadoEvaluacion;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.persistence.ComidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComidaRepository extends JpaRepository<ComidaEntity, String> {

    List<ComidaEntity> findByEstudianteIdOrderByFechaHoraDesc(String estudianteId);

    List<ComidaEntity> findByEstudianteIdAndFechaHoraBetweenOrderByFechaHoraDesc(
            String estudianteId, LocalDateTime inicio, LocalDateTime fin);

    boolean existsByEstudianteIdAndTipoAndFechaHoraBetween(
            String estudianteId, TipoComida tipo, LocalDateTime inicio, LocalDateTime fin);

    List<ComidaEntity> findTop50ByEstadoAndIntentosEvaluacionLessThanOrderByFechaHoraAsc(
            EstadoEvaluacion estado, Integer intentosMaximos);
}
