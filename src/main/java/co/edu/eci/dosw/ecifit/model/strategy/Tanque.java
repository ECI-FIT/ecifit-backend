package co.edu.eci.dosw.ecifit.model.strategy;

/**
 * Estrategia concreta de rol: Tanque.
 * Especializado en fuerza y resistencia, aplica un factor multiplicador elevado de 1.5.
 */
public class Tanque implements RolTemporada {

    private static final double FACTOR_MULTIPLICADOR = 1.5;

    @Override
    public Integer calcularPuntosBase(Integer duracion, Integer intensidad) {
        if (duracion == null || duracion <= 0 || intensidad == null || intensidad <= 0) {
            return 0;
        }
        return (int) Math.round((duracion * intensidad) * FACTOR_MULTIPLICADOR);
    }

    @Override
    public String getNombreRol() {
        return "Tanque";
    }

    @Override
    public String toString() {
        return getNombreRol();
    }
}
