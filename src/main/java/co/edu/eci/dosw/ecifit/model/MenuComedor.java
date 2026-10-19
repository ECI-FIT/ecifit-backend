package co.edu.eci.dosw.ecifit.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Menú publicado por el comedor de la Escuela para una fecha y un tipo de comida.
 */
public class MenuComedor {

    private String id;
    private LocalDate fecha;
    private TipoComida tipo;
    private List<PlatoMenu> platos = new ArrayList<>();

    public MenuComedor() {
    }

    public MenuComedor(String id, LocalDate fecha, TipoComida tipo, List<PlatoMenu> platos) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        setPlatos(platos);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoComida getTipo() {
        return tipo;
    }

    public void setTipo(TipoComida tipo) {
        this.tipo = tipo;
    }

    public List<PlatoMenu> getPlatos() {
        return platos;
    }

    public void setPlatos(List<PlatoMenu> platos) {
        this.platos = platos != null ? new ArrayList<>(platos) : new ArrayList<>();
    }
}
