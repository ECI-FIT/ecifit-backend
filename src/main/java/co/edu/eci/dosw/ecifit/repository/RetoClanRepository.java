package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.RetoClanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetoClanRepository extends JpaRepository<RetoClanEntity, String> {

    List<RetoClanEntity> findByClanId(String clanId);
}
