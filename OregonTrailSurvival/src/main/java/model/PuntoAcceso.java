package model;

public class PuntoAcceso {

    private Escenario destino;
    private String descripcion;
    private boolean accesible;

    public PuntoAcceso(Escenario destino, String descripcion, boolean accesible) {
        this.destino = destino;
        this.descripcion = descripcion;
        this.accesible = accesible;
    }

    public Escenario getDestino() {
        return destino;
    }

    public void setDestino(Escenario destino) {
        this.destino = destino;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean getAccesible() {
        return accesible;
    }

    public void setAccesible(boolean accesible) {
        this.accesible = accesible;
    }

    // METODOS:

    public void desbloquear() {
        if (accesible == false) {
            accesible = true;
        }
    }


    public boolean esAccesible() {
        if(accesible == true){ //true si se puede usar, false si está bloqueado.
            return true;
        } else {
            return false;
        }
    }
}
