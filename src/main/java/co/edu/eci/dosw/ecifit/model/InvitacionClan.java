package co.edu.eci.dosw.ecifit.model;

import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitacionClan {

    private String id;
    private String clanId;
    private String estudianteId;
    private String invitadoPorId;
    private EstadoInvitacion estado;
    private LocalDateTime fecha;

    public void aceptar() {
        validarPendiente();
        this.estado = EstadoInvitacion.ACEPTADA;
    }

    public void rechazar() {
        validarPendiente();
        this.estado = EstadoInvitacion.RECHAZADA;
    }

    private void validarPendiente() {
        if (estado != EstadoInvitacion.PENDIENTE) {
            throw new EstadoInvalidoException("La invitación ya fue respondida");
        }
    }
}
