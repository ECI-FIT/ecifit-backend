package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.persistence.ClanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClanRepository extends JpaRepository<ClanEntity, String> {

    boolean existsByNombre(String nombre);

    @Query("select count(c) > 0 from ClanEntity c join c.miembrosIds m where m = :estudianteId")
    boolean perteneceAAlgunClan(@Param("estudianteId") String estudianteId);
}