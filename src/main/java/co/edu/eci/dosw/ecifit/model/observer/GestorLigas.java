package co.edu.eci.dosw.ecifit.model.observer;

import co.edu.eci.dosw.ecifit.model.Estudiante;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Observador concreto que gestiona las clasificaciones y ascensos/descensos de ligas
 * de los estudiantes con base en el puntaje generado por sus actividades.
 */
public class GestorLigas implements ActividadObserver {

    public enum NivelLiga {
        BRONCE(0),
        PLATA(500),
        ORO(1500),
        DIAMANTE(3000);

        private final int umbralPuntos;

        NivelLiga(int umbralPuntos) {
            this.umbralPuntos = umbralPuntos;
        }

        public int getUmbralPuntos() {
            return umbralPuntos;
        }

        public static NivelLiga calcularNivel(int puntos) {
            if (puntos >= DIAMANTE.umbralPuntos) return DIAMANTE;
            if (puntos >= ORO.umbralPuntos) return ORO;
            if (puntos >= PLATA.umbralPuntos) return PLATA;
            return BRONCE;
        }
    }

    private final Map<Estudiante, Integer> rankingTotal;
    private final Map<Estudiante, NivelLiga> ligasEstudiantes;

    public GestorLigas() {
        this.rankingTotal = new HashMap<>();
        this.ligasEstudiantes = new HashMap<>();
    }

    @Override
    public void onActividadRegistrada(Integer puntosGenerados, Estudiante e) {
        if (e == null || puntosGenerados == null) {
            return;
        }

        this.rankingTotal.merge(e, puntosGenerados, Integer::sum);
        evaluarAscensoDescenso();
    }

    /**
     * Evalúa los umbrales de liga para cada estudiante registrado en el ranking
     * y actualiza su nivel competitivo (Bronce, Plata, Oro, Diamante) en consecuencia.
     */
    public void evaluarAscensoDescenso() {
        for (Map.Entry<Estudiante, Integer> entry : rankingTotal.entrySet()) {
            Estudiante estudiante = entry.getKey();
            Integer puntos = entry.getValue();
            NivelLiga nuevoNivel = NivelLiga.calcularNivel(puntos != null ? puntos : 0);
            this.ligasEstudiantes.put(estudiante, nuevoNivel);
        }
    }

    /**
     * Obtiene el nivel de liga actual de un estudiante.
     *
     * @param e Estudiante a consultar.
     * @return {@link NivelLiga} asignado.
     */
    public NivelLiga obtenerLigaEstudiante(Estudiante e) {
        return ligasEstudiantes.getOrDefault(e, NivelLiga.BRONCE);
    }

    public Map<Estudiante, Integer> getRankingTotal() {
        return Collections.unmodifiableMap(rankingTotal);
    }

    public Map<Estudiante, NivelLiga> getLigasEstudiantes() {
        return Collections.unmodifiableMap(ligasEstudiantes);
    }
}
