package co.edu.eci.dosw.ecifit.model;

import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;

import java.util.UUID;

public class RankingTemporada {

    private UUID id;
    private UUID estudianteId;
    private Temporada temporada;
    private Integer puntosAcumulados;
    private LigaEnum ligaActual;

    public RankingTemporada() {
        this.puntosAcumulados = 0;
        this.ligaActual = LigaEnum.BRONCE;
    }

    public RankingTemporada(UUID id, UUID estudianteId, Temporada temporada, Integer puntosAcumulados,
                           LigaEnum ligaActual) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.temporada = temporada;
        this.puntosAcumulados = puntosAcumulados != null ? puntosAcumulados : 0;
        this.ligaActual = ligaActual;
    }

    public void agregarPuntos(int puntos) {
        if (puntos < 0) {
            throw new ReglaDeNegocioException("Los puntos a agregar no pueden ser negativos");
        }
        this.puntosAcumulados = (this.puntosAcumulados != null ? this.puntosAcumulados : 0) + puntos;
        actualizarLiga();
    }

    public void reducirPuntos(int puntos) {
        if (puntos < 0) {
            throw new ReglaDeNegocioException("Los puntos a reducir no pueden ser negativos");
        }
        int puntosActuales = this.puntosAcumulados != null ? this.puntosAcumulados : 0;
        this.puntosAcumulados = Math.max(0, puntosActuales - puntos);
        actualizarLiga();
    }

    public void actualizarLiga() {
        int puntos = this.puntosAcumulados != null ? this.puntosAcumulados : 0;
        this.ligaActual = LigaEnum.desdePuntos(puntos);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(UUID estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
    }

    public Integer getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(Integer puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public LigaEnum getLigaActual() {
        return ligaActual;
    }

    public void setLigaActual(LigaEnum ligaActual) {
        this.ligaActual = ligaActual;
    }
}
