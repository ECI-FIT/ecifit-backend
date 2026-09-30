package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClanRepository extends JpaRepository<ClanEntity, String> {

    boolean existsByNombre(String nombre);
}