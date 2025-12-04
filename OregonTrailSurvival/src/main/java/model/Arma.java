package model;

import enums.NOMBRE_ARMA;

public class Arma {
    private NOMBRE_ARMA arma;
    private int daño;
    private int capacidadCargador;
    private int municionRestante;
    private double tiempoDeRecarga;
    private double velocidadDeDisparo;

    public Arma(NOMBRE_ARMA arma, int daño, int capacidadCargador, int municionRestante, double tiempoDeRecarga, double velocidadDeDisparo) {
        this.arma = arma;
        this.daño = daño;
        this.capacidadCargador = capacidadCargador;
        this.municionRestante = municionRestante;
        this.tiempoDeRecarga = tiempoDeRecarga;
        this.velocidadDeDisparo = velocidadDeDisparo;
    }

    public NOMBRE_ARMA getArma() {
        return arma;
    }

    public void setArma(NOMBRE_ARMA arma) {
        this.arma = arma;
    }

    public int getDaño() {
        return daño;
    }

    public void setDaño(int daño) {
        this.daño = daño;
    }

    public int getCapacidadCargador() {
        return capacidadCargador;
    }

    public void setCapacidadCargador(int capacidadCargador) {
        this.capacidadCargador = capacidadCargador;
    }

    public int getMunicionRestante() {
        return municionRestante;
    }

    public void setMunicionRestante(int municionRestante) {
        this.municionRestante = municionRestante;
    }

    public double getTiempoDeRecarga() {
        return tiempoDeRecarga;
    }

    public void setTiempoDeRecarga(double tiempoDeRecarga) {
        this.tiempoDeRecarga = tiempoDeRecarga;
    }

    public double getVelocidadDeDisparo() {
        return velocidadDeDisparo;
    }

    public void setVelocidadDeDisparo(double velocidadDeDisparo) {
        this.velocidadDeDisparo = velocidadDeDisparo;
    }

    //METODOS
    public void disparar(Enemigo objetivo){
        if (objetivo == null) {
            return;
        }
        if (!estaDisponible()) {
            return;
        }
        municionRestante = municionRestante - 1;
        int nuevaSalud = objetivo.getSalud() - daño;// aplicar daño y no permitir salud negativa
        if (nuevaSalud < 0) {
            nuevaSalud = 0;
        }
        objetivo.setSalud(nuevaSalud);
    }



    public boolean estaDisponible(){
        if (municionRestante > 0) {
            return true;
        } else {
            return false;
        }
    }


    public void recargar(){
        municionRestante = capacidadCargador;

    }

}
