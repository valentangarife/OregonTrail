package model;

/**
 * Representa un portal que conecta dos escenarios en el juego.
 * Cuando el jugador está encima de un portal, cambia de escenario.
 * Solo hay un portal por escenario y la meta de cada escenario es llegar al portal.
 */
public class Portal {

    private String id;
    private String nodoOrigen;
    private String nodoDestino;
    private int posicionX;
    private int posicionY;
    private boolean activo;
    /** Indica si este portal lleva al siguiente escenario / meta. */
    private boolean llevaAEscenario;
    /** Zona o región donde está el portal (por si quieres agruparlos). */
    private String zona;

    public Portal(String id, String nodoOrigen, String nodoDestino, int posicionX, int posicionY) {
        this.id = id;
        this.nodoOrigen = nodoOrigen;
        this.nodoDestino = nodoDestino;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.activo = true;
        this.llevaAEscenario = false;
        this.zona = "";
    }

    // Getters y setters básicos

    public String getId() {
        return id;
    }

    public String getNodoOrigen() {
        return nodoOrigen;
    }

    public String getNodoDestino() {
        return nodoDestino;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public void setPosicionX(int posicionX) {
        this.posicionX = posicionX;
    }

    public int getPosicionY() {
        return posicionY;
    }

    public void setPosicionY(int posicionY) {
        this.posicionY = posicionY;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isLlevaAEscenario() {
        return llevaAEscenario;
    }

    public void setLlevaAEscenario(boolean llevaAEscenario) {
        this.llevaAEscenario = llevaAEscenario;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
}
