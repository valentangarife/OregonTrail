package enums;

public enum PROFESION {
    GRANJERO(10),       // Bono de habilidad base: 10
    BANQUERO(25),        // Bono de habilidad base: 25
    CARPINTERO(15),;         // Bono de habilidad base: 15
       // Bono de habilidad base: 5

    private final int habilidadBase;

    PROFESION(int habilidadBase) {
        this.habilidadBase = habilidadBase;
    }

    public int getHabilidadBase() {
        return habilidadBase;
    }
}