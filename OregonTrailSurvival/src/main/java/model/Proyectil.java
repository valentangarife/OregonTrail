package model;

import javafx.scene.image.ImageView;

public class Proyectil {
    private double x;
    private double y;
    private double velocidadX;
    private double velocidadY;
    private int dano;
    private ImageView sprite;
    private boolean activo;
    private boolean persigue; // Si el proyectil persigue al jugador

    public Proyectil(double x, double y, double velocidadX, double velocidadY, int dano) {
        this.x = x;
        this.y = y;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
        this.dano = dano;
        this.activo = true;
        this.persigue = true; // Por defecto persigue
    }

    public void actualizar() {
        x += velocidadX;
        y += velocidadY;
    }
    
    public void actualizarPersiguiendo(double objetivoX, double objetivoY) {
        // Calcular dirección hacia el objetivo
        double dx = objetivoX - x;
        double dy = objetivoY - y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        
        if (distancia > 0) {
            // Normalizar y aplicar velocidad (reducida para que sea más visible)
            double velocidad = 3.0; // Velocidad de persecución más lenta y visible
            velocidadX = (dx / distancia) * velocidad;
            velocidadY = (dy / distancia) * velocidad;
        }
        
        actualizar();
    }

    // Getters y Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getVelocidadX() { return velocidadX; }
    public void setVelocidadX(double velocidadX) { this.velocidadX = velocidadX; }
    
    public double getVelocidadY() { return velocidadY; }
    public void setVelocidadY(double velocidadY) { this.velocidadY = velocidadY; }
    
    public int getDano() { return dano; }
    public void setDano(int dano) { this.dano = dano; }
    
    public ImageView getSprite() { return sprite; }
    public void setSprite(ImageView sprite) { this.sprite = sprite; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public boolean isPersigue() { return persigue; }
    public void setPersigue(boolean persigue) { this.persigue = persigue; }
}
