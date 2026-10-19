package co.edu.eci.dosw.ecifit.repository;

import co.edu.eci.dosw.ecifit.model.EstadoInvitacion;
import co.edu.eci.dosw.ecifit.persistence.InvitacionClanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitacionClanRepository extends JpaRepository<InvitacionClanEntity, String> {

    boolean existsByClanIdAndEstudianteIdAndEstado(String clanId, String estudianteId, EstadoInvitacion estado);

    List<InvitacionClanEntity> findByEstudianteIdAndEstado(String estudianteId, EstadoInvitacion estado);
}
