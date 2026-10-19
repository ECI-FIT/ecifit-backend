package co.edu.eci.dosw.ecifit.model;

public enum LigaEnum {
    BRONCE(0),
    PLATA(1000),
    ORO(2000),
    PLATINO(3000),
    DIAMANTE(4000);

    private final int umbralPuntos;

    LigaEnum(int umbralPuntos) {
        this.umbralPuntos = umbralPuntos;
    }

    public int getUmbralPuntos() {
        return umbralPuntos;
    }

    public static LigaEnum desdePuntos(int puntos) {
        LigaEnum liga = BRONCE;
        for (LigaEnum candidata : values()) {
            if (puntos >= candidata.umbralPuntos) {
                liga = candidata;
            }
        }
        return liga;
    }
}
