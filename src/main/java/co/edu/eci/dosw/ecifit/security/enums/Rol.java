package co.edu.eci.dosw.ecifit.security.enums;

public enum Rol {
    ESTUDIANTE,
    ENTRENADOR,
    ADMINISTRADOR;

    public String getPrefixedName() {
        return "ROLE_" + this.name();
    }
}
