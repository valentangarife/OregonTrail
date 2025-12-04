package model;

import enums.TIPORECURSO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Recurso {

    private TIPORECURSO tipo;
    private int cantidad;
    private Object userData;
    private ImageView sprite;

    // Coordenadas LÓGICAS (unidades de juego)
    private int x;
    private int y;
    private String spritePath;

    // 1. CONSTRUCTOR COMPLETO (5 ARGUMENTOS) - El compilador lo requiere.
    public Recurso(TIPORECURSO tipo, int cantidad, int x, int y, String spritePath) {
        this.tipo = tipo;
        this.cantidad = Math.max(cantidad, 0);
        this.x = x;
        this.y = y;
        this.spritePath = spritePath;
        iniciarSprite();
    }

    // 2. CONSTRUCTOR SIMPLIFICADO (2 ARGUMENTOS) - ¡EL QUE TE FALTABA O SE ROMPIÓ!
    public Recurso(TIPORECURSO tipo, int cantidad) {
        // Llama al constructor completo, usando -1 para coordenadas y null para la ruta.
        this(tipo, cantidad, -1, -1, null);
    }

    // (Opcional) Constructor auxiliar que quizás estabas usando
    public Recurso(String s, int x, int y) {
        // Asume un tipo y cantidad por defecto si solo se dan coordenadas
        this(TIPORECURSO.COMIDA, 1, x, y, "/images/recursos/comida.png");
    }

    public void iniciarSprite() {
        String ruta = spritePath;
        if (ruta == null) {
            // fallback según tipo (lógica de tu código original)
            if (tipo == null) {
                ruta = "/images/recursos/recurso.png";
            } else {
                switch (tipo) {
                    case MEDICINA -> ruta = "/images/recursos/medicina.png";
                    case COMIDA -> ruta = "/images/recursos/comida.png";
                    case MUNICION -> ruta = "/images/recursos/municion.png";
                    default -> ruta = "/images/recursos/recurso.png";
                }
            }
        }

        try {
            Image img = new Image(getClass().getResource(ruta).toExternalForm());
            sprite = new ImageView(img);
        } catch (Exception ex) {
            sprite = new ImageView();
            System.err.println("Error cargando sprite de recurso: " + ex.getMessage());
        }

        sprite.setFitWidth(30);
        sprite.setFitHeight(30);
        // NO posicionamos el sprite aquí.
        this.userData = sprite;
    }

    // --- Getters y Setters ---
    public TIPORECURSO getTipo() { return tipo; }
    public void setTipo(TIPORECURSO tipo) { this.tipo = tipo; }

    public ImageView getSprite() { return sprite; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = Math.max(cantidad, 0); }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; } // Solo actualiza lógica

    public int getY() { return y; }
    public void setY(int y) { this.y = y; } // Solo actualiza lógica

    public String getSpritePath() { return spritePath; }
    public void setSpritePath(String spritePath) { this.spritePath = spritePath; }

    public void aumentarCantidad(int extra) {
        if (extra <= 0) return;
        cantidad += extra;
    }

    public void usarRecurso(int c) {
        if (c <= 0) return;
        cantidad = Math.max(cantidad - c, 0);
    }

    // El tipo del parámetro debe coincidir con la definición de tu clase Recurso
    public void setUserData(Object userData) {
        this.userData = userData;
    }
}