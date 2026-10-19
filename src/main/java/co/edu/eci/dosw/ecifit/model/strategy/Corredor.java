package co.edu.eci.dosw.ecifit.model.strategy;

/**
 * Estrategia concreta de rol: Corredor.
 * Especializado en actividades de cardio, velocidad y agilidad, aplica un factor multiplicador de 1.4.
 */
public class Corredor implements RolTemporada {

    private static final double FACTOR_MULTIPLICADOR = 1.4;

    @Override
    public Integer calcularPuntosBase(Integer duracion, Integer intensidad) {
        if (duracion == null || duracion <= 0 || intensidad == null || intensidad <= 0) {
            return 0;
        }
        return (int) Math.round((duracion * intensidad) * FACTOR_MULTIPLICADOR);
    }

    @Override
    public String getNombreRol() {
        return "Corredor";
    }

    @Override
    public String toString() {
        return getNombreRol();
    }
}
