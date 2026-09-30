package co.edu.eci.dosw.ecifit.model;

import co.edu.eci.dosw.ecifit.model.factory.FabricaMisiones;
import co.edu.eci.dosw.ecifit.model.factory.FabricaMisionesEstandar;
import co.edu.eci.dosw.ecifit.model.factory.Mision;
import co.edu.eci.dosw.ecifit.model.factory.MisionDiaria;
import co.edu.eci.dosw.ecifit.model.factory.MisionSemanal;
import co.edu.eci.dosw.ecifit.model.observer.GestorLigas;
import co.edu.eci.dosw.ecifit.model.observer.TorreClan;
import co.edu.eci.dosw.ecifit.model.state.SemanaParciales;
import co.edu.eci.dosw.ecifit.model.state.SemanaRegular;
import co.edu.eci.dosw.ecifit.model.state.Temporada;
import co.edu.eci.dosw.ecifit.model.strategy.Corredor;
import co.edu.eci.dosw.ecifit.model.strategy.Estratega;
import co.edu.eci.dosw.ecifit.model.strategy.Tanque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias del Dominio Puro - ECI FIT")
class DomainTests {

    @Nested
    @DisplayName("Patrón State - Temporadas y Ciclos Académicos")
    class StateTests {

        @Test
        @DisplayName("SemanaRegular debe permitir ataque a torres y tener multiplicador 1.0")
        void testSemanaRegular() {
            SemanaRegular estado = new SemanaRegular();
            assertTrue(estado.permiteAtaqueTorres());
            assertEquals(1.0, estado.obtenerMultiplicador());
        }

        @Test
        @DisplayName("SemanaParciales debe prohibir ataque a torres y tener multiplicador 1.5")
        void testSemanaParciales() {
            SemanaParciales estado = new SemanaParciales();
            assertFalse(estado.permiteAtaqueTorres());
            assertEquals(1.5, estado.obtenerMultiplicador());
        }

        @Test
        @DisplayName("Temporada alterna estados al evaluar fechas o períodos de parciales")
        void testTemporadaTransiciones() {
            Temporada temporada = new Temporada("2026-2");
            assertInstanceOf(SemanaRegular.class, temporada.getEstadoActual());

            // Alternar estado sin calendario explícito
            temporada.evaluarCambioEstado(LocalDate.of(2026, 10, 12));
            assertInstanceOf(SemanaParciales.class, temporada.getEstadoActual());
            assertFalse(temporada.permiteAtaqueTorres());

            temporada.evaluarCambioEstado(LocalDate.of(2026, 10, 19));
            assertInstanceOf(SemanaRegular.class, temporada.getEstadoActual());
            assertTrue(temporada.permiteAtaqueTorres());

            // Con rango explícito de parciales
            LocalDate inicioParciales = LocalDate.of(2026, 11, 2);
            LocalDate finParciales = LocalDate.of(2026, 11, 8);
            temporada.registrarPeriodoParciales(inicioParciales, finParciales);

            temporada.evaluarCambioEstado(LocalDate.of(2026, 11, 4));
            assertInstanceOf(SemanaParciales.class, temporada.getEstadoActual());

            temporada.evaluarCambioEstado(LocalDate.of(2026, 11, 15));
            assertInstanceOf(SemanaRegular.class, temporada.getEstadoActual());
        }
    }

    @Nested
    @DisplayName("Patrón Strategy - Roles de Temporada y Puntuación")
    class StrategyTests {

        @Test
        @DisplayName("Tanque calcula puntos con factor 1.5")
        void testTanquePuntos() {
            Tanque tanque = new Tanque();
            // (60 * 8) * 1.5 = 480 * 1.5 = 720
            assertEquals(720, tanque.calcularPuntosBase(60, 8));
        }

        @Test
        @DisplayName("Corredor calcula puntos con factor 1.4")
        void testCorredorPuntos() {
            Corredor corredor = new Corredor();
            // (45 * 7) * 1.4 = 315 * 1.4 = 441
            assertEquals(441, corredor.calcularPuntosBase(45, 7));
        }

        @Test
        @DisplayName("Estratega calcula puntos con factor 1.3")
        void testEstrategaPuntos() {
            Estratega estratega = new Estratega();
            // (50 * 6) * 1.3 = 300 * 1.3 = 390
            assertEquals(390, estratega.calcularPuntosBase(50, 6));
        }

        @Test
        @DisplayName("Manejo de valores nulos o no positivos retorna 0")
        void testValoresInvalidos() {
            Tanque tanque = new Tanque();
            assertEquals(0, tanque.calcularPuntosBase(null, 5));
            assertEquals(0, tanque.calcularPuntosBase(30, 0));
        }
    }

    @Nested
    @DisplayName("Entidades Core - Estudiante y Actividad")
    class CoreEntityTests {

        @Test
        @DisplayName("Actividad valida correctamente duración e intensidad")
        void testActividadValidez() {
            Actividad valida = new Actividad("A1", "Fuerza", 30, 7, LocalDateTime.now());
            assertTrue(valida.esValida());

            Actividad duracionCorta = new Actividad("A2", "Fuerza", 9, 7, LocalDateTime.now());
            assertFalse(duracionCorta.esValida());

            Actividad intensidadBaja = new Actividad("A3", "Cardio", 20, 0, LocalDateTime.now());
            assertFalse(intensidadBaja.esValida());

            Actividad intensidadAlta = new Actividad("A4", "Cardio", 20, 11, LocalDateTime.now());
            assertFalse(intensidadAlta.esValida());
        }

        @Test
        @DisplayName("Estudiante registra actividad válida, acumula puntos y notifica observadores")
        void testRegistroActividad() {
            Estudiante estudiante = new Estudiante("E1", "Juan Perez", "juan.perez@escuelaing.edu.co", new Tanque());
            Actividad actividad = new Actividad("A1", "Pesas", 40, 5, LocalDateTime.now());

            // Observador de prueba
            final int[] puntosRecibidos = {0};
            actividad.agregarObservador((puntos, e) -> puntosRecibidos[0] = puntos);

            estudiante.registrarActividad(actividad);

            // (40 * 5) * 1.5 = 200 * 1.5 = 300
            assertEquals(300, estudiante.getPuntosAcumulados());
            assertEquals(1, estudiante.getActividadesRealizadas().size());
            assertEquals(300, puntosRecibidos[0]);
        }

        @Test
        @DisplayName("Estudiante rechaza registrar actividad inválida")
        void testRegistroActividadInvalida() {
            Estudiante estudiante = new Estudiante("E1", "Juan Perez", "juan.perez@escuelaing.edu.co", new Corredor());
            Actividad actividadInvalida = new Actividad("A2", "Cardio", 5, 2, LocalDateTime.now());

            assertThrows(IllegalArgumentException.class, () -> estudiante.registrarActividad(actividadInvalida));
        }
    }

    @Nested
    @DisplayName("Patrón Observer - Gestor de Ligas y Torres de Clan")
    class ObserverTests {

        @Test
        @DisplayName("GestorLigas actualiza ranking y asciende ligas según puntos")
        void testGestorLigas() {
            GestorLigas gestorLigas = new GestorLigas();
            Estudiante e = new Estudiante("E1", "Ana Gomez", "ana.gomez@mail.escuelaing.edu.co", new Corredor());

            assertEquals(GestorLigas.NivelLiga.BRONCE, gestorLigas.obtenerLigaEstudiante(e));

            // Notificar 600 puntos -> Ascenso a Plata
            gestorLigas.onActividadRegistrada(600, e);
            assertEquals(GestorLigas.NivelLiga.PLATA, gestorLigas.obtenerLigaEstudiante(e));
            assertEquals(600, gestorLigas.getRankingTotal().get(e));

            // Notificar 1000 puntos más (total 1600) -> Ascenso a Oro
            gestorLigas.onActividadRegistrada(1000, e);
            assertEquals(GestorLigas.NivelLiga.ORO, gestorLigas.obtenerLigaEstudiante(e));

            // Notificar 1500 puntos más (total 3100) -> Ascenso a Diamante
            gestorLigas.onActividadRegistrada(1500, e);
            assertEquals(GestorLigas.NivelLiga.DIAMANTE, gestorLigas.obtenerLigaEstudiante(e));
        }

        @Test
        @DisplayName("TorreClan recibe daño solo si el estudiante atacante es de un clan rival")
        void testTorreClanRival() {
            Clan clanAlfa = new Clan("CLAN_ALFA", "Alfa");
            clanAlfa.setSaludTorre(500);
            TorreClan torre = new TorreClan(clanAlfa);

            Estudiante aliado = new Estudiante("E1", "Carlos", "carlos@escuelaing.edu.co", 0, new Tanque(), "CLAN_ALFA");
            torre.onActividadRegistrada(100, aliado);
            assertEquals(500, torre.getClanDefensor().getSaludTorre(), "No debe recibir daño de un miembro del mismo clan");

            Estudiante rival = new Estudiante("E2", "Diana", "diana@escuelaing.edu.co", 0, new Corredor(), "CLAN_BETA");
            torre.onActividadRegistrada(200, rival);
            assertEquals(300, torre.getClanDefensor().getSaludTorre(), "Debe recibir daño de un miembro de clan rival");

            clanAlfa.recibirDano(400);
            assertEquals(0, torre.getClanDefensor().getSaludTorre(), "La salud no debe bajar de 0");
            assertTrue(torre.estaDestruida());
        }
    }

    @Nested
    @DisplayName("Patrón Abstract Factory & Herencia - Misiones")
    class MissionTests {

        @Test
        @DisplayName("MisionDiaria se completa si la actividad cumple la duración diaria mínima")
        void testMisionDiaria() {
            MisionDiaria misionDiaria = new MisionDiaria("MD1", "Caminar 30 min", 50, 30);
            assertFalse(misionDiaria.getCompletada());

            Actividad corta = new Actividad("A1", "Trote", 20, 5, LocalDateTime.now());
            assertFalse(misionDiaria.verificarCumplimiento(corta));
            assertFalse(misionDiaria.getCompletada());

            Actividad cumple = new Actividad("A2", "Trote", 35, 6, LocalDateTime.now());
            assertTrue(misionDiaria.verificarCumplimiento(cumple));
            assertTrue(misionDiaria.getCompletada());
        }

        @Test
        @DisplayName("MisionSemanal acumula progreso y se completa al alcanzar la meta acumulada")
        void testMisionSemanal() {
            MisionSemanal misionSemanal = new MisionSemanal("MS1", "Semana Activa", 200, 60);
            assertFalse(misionSemanal.getCompletada());

            Actividad sesion1 = new Actividad("A1", "Gym", 30, 6, LocalDateTime.now());
            assertFalse(misionSemanal.verificarCumplimiento(sesion1));
            assertEquals(30, misionSemanal.getProgresoActualMinutos());
            assertFalse(misionSemanal.getCompletada());

            Actividad sesion2 = new Actividad("A2", "Natación", 30, 7, LocalDateTime.now());
            assertTrue(misionSemanal.verificarCumplimiento(sesion2));
            assertEquals(60, misionSemanal.getProgresoActualMinutos());
            assertTrue(misionSemanal.getCompletada());
        }

        @Test
        @DisplayName("FabricaMisiones crea familias de misiones coherentes")
        void testFabricaMisiones() {
            FabricaMisiones fabrica = new FabricaMisionesEstandar();
            Mision diaria = fabrica.crearMisionDiaria();
            Mision semanal = fabrica.crearMisionSemanal();

            assertNotNull(diaria);
            assertNotNull(semanal);
            assertInstanceOf(MisionDiaria.class, diaria);
            assertInstanceOf(MisionSemanal.class, semanal);
            assertTrue(diaria.getRecompensa() > 0);
            assertTrue(semanal.getRecompensa() > 0);
        }
    }
}
