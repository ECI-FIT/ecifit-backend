package co.edu.eci.dosw.ecifit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa un clan de estudiantes en ECI FIT.
 * Agrupa miembros, acumula puntos y participa en las Guerras de Campus
 * exponiendo la salud de su torre como blanco de ataque.
 */
public class Clan {

    private static final int LIMITE_MIEMBROS = 5;
    private static final int SALUD_TORRE_INICIAL = 5000;

    private String id;
    private String nombre;
    private Integer saludTorre;
    private Integer puntosTotales;
    private final List<String> miembrosIds;

    public Clan() {
        this.saludTorre = SALUD_TORRE_INICIAL;
        this.puntosTotales = 0;
        this.miembrosIds = new ArrayList<>();
    }

    public Clan(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.saludTorre = SALUD_TORRE_INICIAL;
        this.puntosTotales = 0;
        this.miembrosIds = new ArrayList<>();
    }

    /**
     * Agrega un estudiante al clan, respetando el limite maximo de miembros.
     *
     * @param estudianteId identificador del estudiante a unir.
     * @throws IllegalStateException si el clan ya alcanzo el limite de miembros.
     */
    public void agregarMiembro(String estudianteId) {
        if (miembrosIds.size() >= LIMITE_MIEMBROS) {
            throw new IllegalStateException("El clan ya alcanzo el limite de " + LIMITE_MIEMBROS + " miembros");
        }
        if (!miembrosIds.contains(estudianteId)) {
            miembrosIds.add(estudianteId);
        }
    }

    /**
     * Aplica dano a la torre del clan, sin bajar de cero.
     *
     * @param dano cantidad de dano a infligir.
     */
    public void recibirDano(int dano) {
        this.saludTorre = Math.max(0, this.saludTorre - dano);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getSaludTorre() {
        return saludTorre;
    }

    public void setSaludTorre(Integer saludTorre) {
        this.saludTorre = saludTorre;
    }

    public Integer getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(Integer puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public List<String> getMiembrosIds() {
        return Collections.unmodifiableList(miembrosIds);
    }

    public int getCantidadMiembros() {
        return miembrosIds.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clan clan)) return false;
        return Objects.equals(id, clan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}