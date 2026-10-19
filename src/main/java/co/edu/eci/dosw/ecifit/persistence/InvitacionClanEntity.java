package co.edu.eci.dosw.ecifit.persistence;

import co.edu.eci.dosw.ecifit.model.EstadoInvitacion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitaciones_clan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitacionClanEntity {

    @Id
    private String id;

    private String clanId;

    private String estudianteId;

    private String invitadoPorId;

    @Enumerated(EnumType.STRING)
    private EstadoInvitacion estado;

    private LocalDateTime fecha;
}
