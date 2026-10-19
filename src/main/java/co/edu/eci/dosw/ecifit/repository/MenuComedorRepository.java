package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.persistence.MenuComedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MenuComedorRepository extends JpaRepository<MenuComedorEntity, String> {

    List<MenuComedorEntity> findByFecha(LocalDate fecha);

    boolean existsByFechaAndTipo(LocalDate fecha, TipoComida tipo);
}
