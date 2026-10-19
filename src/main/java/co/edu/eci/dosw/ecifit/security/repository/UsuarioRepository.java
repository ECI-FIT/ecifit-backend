package co.edu.eci.dosw.ecifit.security.repository;

import co.edu.eci.dosw.ecifit.security.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
