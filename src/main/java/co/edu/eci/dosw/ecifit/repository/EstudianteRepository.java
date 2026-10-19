package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, String> {

    boolean existsByCorreoInstitucional(String correoInstitucional);

    Optional<EstudianteEntity> findByCorreoInstitucional(String correoInstitucional);
}
