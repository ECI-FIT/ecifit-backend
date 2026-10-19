package co.edu.eci.dosw.ecifit.config;

import co.edu.eci.dosw.ecifit.model.LigaEnum;
import co.edu.eci.dosw.ecifit.persistence.*;
import co.edu.eci.dosw.ecifit.repository.*;
import co.edu.eci.dosw.ecifit.security.entity.UsuarioEntity;
import co.edu.eci.dosw.ecifit.security.enums.Rol;
import co.edu.eci.dosw.ecifit.security.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Inicializador de datos de demostración y precarga para stand y feria de proyectos DOSW.
 * Siembra de forma atómica estudiantes, clanes, retos colectivos, temporadas, rankings, misiones y actividades.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final ClanRepository clanRepository;
    private final RetoClanRepository retoClanRepository;
    private final TemporadaRepository temporadaRepository;
    private final RankingTemporadaRepository rankingTemporadaRepository;
    private final MisionRepository misionRepository;
    private final ActividadRepository actividadRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (estudianteRepository.count() == 0) {
            log.info("Inicializando semillero de datos de negocio para la Feria de Proyectos DOSW...");

            // 1. Estudiantes de demostración
            EstudianteEntity e1 = EstudianteEntity.builder()
                    .id("EST-01")
                    .nombre("Laura Torres")
                    .correoInstitucional("laura.torres@mail.escuelaing.edu.co")
                    .rolActivo("TANQUE")
                    .puntosAcumulados(450)
                    .clanId("CLAN-01")
                    .fechaUltimoBonoRacha(LocalDate.now().minusDays(2))
                    .build();

            EstudianteEntity e2 = EstudianteEntity.builder()
                    .id("EST-02")
                    .nombre("Santiago Ruiz")
                    .correoInstitucional("santiago.ruiz@mail.escuelaing.edu.co")
                    .rolActivo("CORREDOR")
                    .puntosAcumulados(320)
                    .clanId("CLAN-01")
                    .build();

            EstudianteEntity e3 = EstudianteEntity.builder()
                    .id("EST-03")
                    .nombre("Juan Perez")
                    .correoInstitucional("juan.perez@mail.escuelaing.edu.co")
                    .rolActivo("ESTRATEGA")
                    .puntosAcumulados(210)
                    .clanId("CLAN-02")
                    .build();

            estudianteRepository.saveAll(List.of(e1, e2, e3));

            // Credenciales de autenticación asociadas para login en Stand
            crearUsuarioSiNoExiste("laura.torres@mail.escuelaing.edu.co", "Estudiante123*", Rol.ESTUDIANTE);
            crearUsuarioSiNoExiste("santiago.ruiz@mail.escuelaing.edu.co", "Estudiante123*", Rol.ESTUDIANTE);
            crearUsuarioSiNoExiste("juan.perez@mail.escuelaing.edu.co", "Estudiante123*", Rol.ESTUDIANTE);

            // 2. Clanes de demostración
            ClanEntity c1 = new ClanEntity();
            c1.setId("CLAN-01");
            c1.setNombre("Guerreros ECI");
            c1.setSaludTorre(1000);
            c1.setPuntosTotales(770);
            c1.setMiembrosIds(new ArrayList<>(List.of("EST-01", "EST-02")));

            ClanEntity c2 = new ClanEntity();
            c2.setId("CLAN-02");
            c2.setNombre("Titanes Gym");
            c2.setSaludTorre(850);
            c2.setPuntosTotales(210);
            c2.setMiembrosIds(new ArrayList<>(List.of("EST-03")));

            clanRepository.saveAll(List.of(c1, c2));

            // 3. Retos de clan de demostración (RF-34 y RF-36)
            RetoClanEntity reto1 = RetoClanEntity.builder()
                    .id("RETO-DEMO-01")
                    .clanId("CLAN-01")
                    .descripcion("Acumular 600 puntos colectivos en actividades de fuerza")
                    .objetivoPuntos(600)
                    .progresoActual(770)
                    .recompensa(200)
                    .fechaInicio(LocalDate.now().minusDays(3))
                    .fechaFin(LocalDate.now().plusDays(4))
                    .completado(false)
                    .build();

            RetoClanEntity reto2 = RetoClanEntity.builder()
                    .id("RETO-DEMO-02")
                    .clanId("CLAN-02")
                    .descripcion("Alcanzar 500 puntos en retos de resistencia cardiovascular")
                    .objetivoPuntos(500)
                    .progresoActual(210)
                    .recompensa(150)
                    .fechaInicio(LocalDate.now().minusDays(2))
                    .fechaFin(LocalDate.now().plusDays(5))
                    .completado(false)
                    .build();

            retoClanRepository.saveAll(List.of(reto1, reto2));

            // 4. Temporada Activa
            TemporadaEntity temporada = TemporadaEntity.builder()
                    .nombre("2026-2")
                    .fechaInicio(LocalDate.now().minusWeeks(4))
                    .fechaFin(LocalDate.now().plusWeeks(8))
                    .estadoActivo(true)
                    .build();
            temporada = temporadaRepository.save(temporada);

            // 5. Rankings de la Temporada
            RankingTemporadaEntity r1 = RankingTemporadaEntity.builder()
                    .estudianteId(UUID.randomUUID())
                    .temporada(temporada)
                    .puntosAcumulados(450)
                    .ligaActual(LigaEnum.ORO)
                    .build();

            RankingTemporadaEntity r2 = RankingTemporadaEntity.builder()
                    .estudianteId(UUID.randomUUID())
                    .temporada(temporada)
                    .puntosAcumulados(320)
                    .ligaActual(LigaEnum.PLATA)
                    .build();

            RankingTemporadaEntity r3 = RankingTemporadaEntity.builder()
                    .estudianteId(UUID.randomUUID())
                    .temporada(temporada)
                    .puntosAcumulados(210)
                    .ligaActual(LigaEnum.BRONCE)
                    .build();

            rankingTemporadaRepository.saveAll(List.of(r1, r2, r3));

            // 6. Misiones de Demostración
            MisionEntity m1 = new MisionEntity();
            m1.setId("MIS-01");
            m1.setDescripcion("Completar 30 minutos de Cardio a intensidad media");
            m1.setRecompensa(50);
            m1.setTipo("DIARIA");
            m1.setCompletada(false);
            m1.setEstudianteId("EST-01");

            MisionEntity m2 = new MisionEntity();
            m2.setId("MIS-02");
            m2.setDescripcion("Acumular 120 minutos de Fuerza en la semana");
            m2.setRecompensa(150);
            m2.setTipo("SEMANAL");
            m2.setCompletada(false);
            m2.setEstudianteId("EST-01");

            MisionEntity m3 = new MisionEntity();
            m3.setId("MIS-03");
            m3.setDescripcion("Sesión de movilidad articular y flexibilidad de 20 min");
            m3.setRecompensa(40);
            m3.setTipo("DIARIA");
            m3.setCompletada(false);
            m3.setEstudianteId("EST-02");

            misionRepository.saveAll(List.of(m1, m2, m3));

            // 7. Actividades registradas en historial
            ActividadEntity a1 = ActividadEntity.builder()
                    .id("ACT-DEMO-01")
                    .tipo("CARDIO")
                    .duracionMinutos(30)
                    .intensidad(8)
                    .puntosOtorgados(336)
                    .estudianteId("EST-01")
                    .fecha(LocalDateTime.now().minusDays(1))
                    .build();

            ActividadEntity a2 = ActividadEntity.builder()
                    .id("ACT-DEMO-02")
                    .tipo("FUERZA")
                    .duracionMinutos(45)
                    .intensidad(7)
                    .puntosOtorgados(450)
                    .estudianteId("EST-01")
                    .fecha(LocalDateTime.now().minusDays(2))
                    .build();

            ActividadEntity a3 = ActividadEntity.builder()
                    .id("ACT-DEMO-03")
                    .tipo("CARDIO")
                    .duracionMinutos(40)
                    .intensidad(6)
                    .puntosOtorgados(336)
                    .estudianteId("EST-02")
                    .fecha(LocalDateTime.now().minusDays(1))
                    .build();

            actividadRepository.saveAll(List.of(a1, a2, a3));

            log.info("Datos de negocio inicializados exitosamente (Estudiantes, Clanes, Retos de Clan, Temporada, Rankings, Misiones y Actividades).");
        }
    }

    private void crearUsuarioSiNoExiste(String email, String rawPassword, Rol rol) {
        if (!usuarioRepository.existsByEmail(email)) {
            UsuarioEntity usuario = UsuarioEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .rol(rol)
                    .activo(true)
                    .build();
            usuarioRepository.save(usuario);
        }
    }
}
