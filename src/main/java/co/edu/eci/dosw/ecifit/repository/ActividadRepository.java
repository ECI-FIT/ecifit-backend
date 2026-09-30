package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.ActividadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<ActividadEntity, String> {

    List<ActividadEntity> findByEstudianteIdOrderByFechaDesc(String estudianteId);
}
