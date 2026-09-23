package co.edu.escuelaing.ecifit.repository;

import co.edu.escuelaing.ecifit.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
