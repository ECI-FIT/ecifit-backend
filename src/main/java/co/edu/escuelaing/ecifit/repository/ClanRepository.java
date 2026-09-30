package co.edu.escuelaing.ecifit.repository;

import co.edu.escuelaing.ecifit.model.entity.ClanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClanRepository extends JpaRepository<ClanEntity, String> {

    boolean existsByNombre(String nombre);
}