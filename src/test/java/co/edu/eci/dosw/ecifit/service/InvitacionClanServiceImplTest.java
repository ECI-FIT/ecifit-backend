package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.EstadoInvalidoException;
import co.edu.eci.dosw.ecifit.exception.RecursoNoEncontradoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.InvitacionClanEntityMapper;
import co.edu.eci.dosw.ecifit.model.Clan;
import co.edu.eci.dosw.ecifit.model.EstadoInvitacion;
import co.edu.eci.dosw.ecifit.model.InvitacionClan;
import co.edu.eci.dosw.ecifit.persistence.InvitacionClanEntity;
import co.edu.eci.dosw.ecifit.repository.EstudianteRepository;
import co.edu.eci.dosw.ecifit.repository.InvitacionClanRepository;
import co.edu.eci.dosw.ecifit.validator.IClanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvitacionClanServiceImplTest {

    @Mock
    private InvitacionClanRepository invitacionRepository;
    @Mock
    private EstudianteRepository estudianteRepository;
    @Mock
    private InvitacionClanEntityMapper entityMapper;
    @Mock
    private IClanService clanService;
    @Mock
    private IClanValidator clanValidator;
    @InjectMocks
    private InvitacionClanServiceImpl service;

    private Clan clan;

    @BeforeEach
    void setUp() {
        clan = new Clan("C1", "Guerreros ECI");
        clan.agregarMiembro("E1");
    }

    private InvitacionClan pendiente() {
        return new InvitacionClan("I1", "C1", "E2", "E1", EstadoInvitacion.PENDIENTE, LocalDateTime.now());
    }

    @Test
    @DisplayName("invitar - datos válidos crea la invitación pendiente")
    void invitar_datosValidos_creaInvitacion() {
        // Arrange
        when(clanService.obtenerPorId("C1")).thenReturn(clan);
        when(estudianteRepository.existsById("E2")).thenReturn(true);

        // Act
        InvitacionClan resultado = service.invitar("C1", "E2", "E1");

        // Assert
        assertNotNull(resultado.getId());
        assertEquals(EstadoInvitacion.PENDIENTE, resultado.getEstado());
        verify(clanValidator).validarEstudianteSinClan("E2");
        verify(clanValidator).validarCupoDisponible(clan);
        verify(invitacionRepository).save(any());
    }

    @Test
    @DisplayName("invitar - quien invita no es miembro lanza ReglaDeNegocioException")
    void invitar_invitadorNoMiembro_lanzaReglaDeNegocio() {
        // Arrange
        when(clanService.obtenerPorId("C1")).thenReturn(clan);

        // Act & Assert
        assertThrows(ReglaDeNegocioException.class, () -> service.invitar("C1", "E2", "E99"));
        verify(invitacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("invitar - estudiante inexistente lanza RecursoNoEncontradoException")
    void invitar_estudianteInexistente_lanzaNoEncontrado() {
        // Arrange
        when(clanService.obtenerPorId("C1")).thenReturn(clan);
        when(estudianteRepository.existsById("E2")).thenReturn(false);

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> service.invitar("C1", "E2", "E1"));
    }

    @Test
    @DisplayName("invitar - estudiante que ya pertenece a un clan lanza ConflictoException")
    void invitar_estudianteConClan_lanzaConflicto() {
        // Arrange
        when(clanService.obtenerPorId("C1")).thenReturn(clan);
        when(estudianteRepository.existsById("E2")).thenReturn(true);
        doThrow(new ConflictoException("El estudiante ya pertenece a un clan"))
                .when(clanValidator).validarEstudianteSinClan("E2");

        // Act & Assert
        assertThrows(ConflictoException.class, () -> service.invitar("C1", "E2", "E1"));
        verify(invitacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("invitar - invitación pendiente repetida lanza ConflictoException")
    void invitar_invitacionPendienteRepetida_lanzaConflicto() {
        // Arrange
        when(clanService.obtenerPorId("C1")).thenReturn(clan);
        when(estudianteRepository.existsById("E2")).thenReturn(true);
        when(invitacionRepository.existsByClanIdAndEstudianteIdAndEstado("C1", "E2", EstadoInvitacion.PENDIENTE))
                .thenReturn(true);

        // Act & Assert
        assertThrows(ConflictoException.class, () -> service.invitar("C1", "E2", "E1"));
        verify(invitacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("responder - aceptar une al estudiante al clan")
    void responder_aceptar_uneAlClan() {
        // Arrange
        InvitacionClanEntity entity = new InvitacionClanEntity();
        when(invitacionRepository.findById("I1")).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(pendiente());

        // Act
        InvitacionClan resultado = service.responder("I1", true);

        // Assert
        assertEquals(EstadoInvitacion.ACEPTADA, resultado.getEstado());
        verify(clanService).unirse("C1", "E2");
        verify(invitacionRepository).save(any());
    }

    @Test
    @DisplayName("responder - rechazar no une al estudiante")
    void responder_rechazar_noUneAlClan() {
        // Arrange
        InvitacionClanEntity entity = new InvitacionClanEntity();
        when(invitacionRepository.findById("I1")).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(pendiente());

        // Act
        InvitacionClan resultado = service.responder("I1", false);

        // Assert
        assertEquals(EstadoInvitacion.RECHAZADA, resultado.getEstado());
        verify(clanService, never()).unirse(any(), any());
    }

    @Test
    @DisplayName("responder - invitación ya respondida lanza EstadoInvalidoException")
    void responder_yaRespondida_lanzaEstadoInvalido() {
        // Arrange
        InvitacionClanEntity entity = new InvitacionClanEntity();
        InvitacionClan invitacion = pendiente();
        invitacion.setEstado(EstadoInvitacion.RECHAZADA);
        when(invitacionRepository.findById("I1")).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(invitacion);

        // Act & Assert
        assertThrows(EstadoInvalidoException.class, () -> service.responder("I1", true));
        verify(clanService, never()).unirse(any(), any());
    }

    @Test
    @DisplayName("responder - invitación inexistente lanza RecursoNoEncontradoException")
    void responder_inexistente_lanzaNoEncontrado() {
        // Arrange
        when(invitacionRepository.findById("I9")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> service.responder("I9", true));
    }

    @Test
    @DisplayName("obtenerPendientes - devuelve las invitaciones pendientes del estudiante")
    void obtenerPendientes_retornaPendientes() {
        // Arrange
        InvitacionClanEntity entity = new InvitacionClanEntity();
        when(invitacionRepository.findByEstudianteIdAndEstado("E2", EstadoInvitacion.PENDIENTE))
                .thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(pendiente());

        // Act
        List<InvitacionClan> resultado = service.obtenerPendientes("E2");

        // Assert
        assertEquals(1, resultado.size());
    }
}
