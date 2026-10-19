package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.MisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MisionRepository extends JpaRepository<MisionEntity, String> {

    List<MisionEntity> findByEstudianteId(String estudianteId);
}