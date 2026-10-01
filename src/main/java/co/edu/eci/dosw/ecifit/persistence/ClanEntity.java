package co.edu.eci.dosw.ecifit.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clanes")
public class ClanEntity {

    @Id
    private String id;

    private String nombre;
    private Integer saludTorre = 5000;
    private Integer puntosTotales;

    @ElementCollection
    @CollectionTable(name = "clan_miembros", joinColumns = @JoinColumn(name = "clan_id"))
    @Column(name = "estudiante_id")
    private List<String> miembrosIds = new ArrayList<>();

    public ClanEntity() {
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
        return miembrosIds;
    }

    public void setMiembrosIds(List<String> miembrosIds) {
        this.miembrosIds = miembrosIds;
    }
}
  