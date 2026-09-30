package co.edu.eci.dosw.ecifit.model.strategy;

/**
 * Estrategia concreta de rol: Estratega.
 * Presenta una distribución balanceada con multiplicador táctico de 1.3 para planes mixtos y consistencia.
 */
public class Estratega implements RolTemporada {

    private static final double FACTOR_MULTIPLICADOR = 1.3;

    @Override
    public Integer calcularPuntosBase(Integer duracion, Integer intensidad) {
        if (duracion == null || duracion <= 0 || intensidad == null || intensidad <= 0) {
            return 0;
        }
        return (int) Math.round((duracion * intensidad) * FACTOR_MULTIPLICADOR);
    }

    @Override
    public String getNombreRol() {
        return "Estratega";
    }

    @Override
    public String toString() {
        return getNombreRol();
    }
}
