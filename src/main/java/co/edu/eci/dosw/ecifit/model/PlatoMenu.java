package co.edu.eci.dosw.ecifit.model;

/**
 * Plato ofrecido en un menú del comedor de la Escuela.
 */
public class PlatoMenu {

    private String id;
    private String nombre;
    private String descripcion;
    private Integer precio;

    public PlatoMenu() {
    }

    public PlatoMenu(String id, String nombre, String descripcion, Integer precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }
}
