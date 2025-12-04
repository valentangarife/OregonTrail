package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// Asumo que Jugador existe.

public class Enemigo {

    private String id;
    private String nombre;
    private int salud;
    private int dano; // Daño que hace el enemigo
    private int posicionX;
    private int posicionY;
    private Object userData;
    private ImageView sprite;

    // Constructor completo
    public Enemigo(String id, String nombre, int salud, int dano, int posicionX, int posicionY) {
        this.id = id;
        this.nombre = nombre;
        this.salud = salud;
        this.dano = dano;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        iniciarSpriteDefault();
    }

    // Constructor mínimo
    public Enemigo(String id) {
        this(id, "Desconocido", 0, 0, 0, 0);
    }

    // Inicializa sprite con imagen por defecto
    private void iniciarSpriteDefault() {
        try {
            // Nota: La ruta es relativa a la carpeta de recursos de tu proyecto
            Image img = new Image(getClass().getResource("/images/enemigos/osoEnemie.png").toExternalForm());
            sprite = new ImageView(img);
        } catch (Exception ex) {
            sprite = new ImageView();
            System.err.println("Error cargando sprite de enemigo: " + ex.getMessage());
        }
        sprite.setFitWidth(40);
        sprite.setFitHeight(40);
        // NO posicionamos el sprite aquí, solo lo inicializamos.
        this.userData = sprite;
    }

    public ImageView getSprite() {
        return sprite;
    }

    // Getters y setters (solo actualizan la posición lógica)
    public String getId() { return id; }


    public int getPosicionX() { return posicionX; }
    public void setPosicionX(int posicionX) {
        this.posicionX = posicionX;
        
    }

    public int getPosicionY() { return posicionY; }
    public void setPosicionY(int posicionY) {
        this.posicionY = posicionY;

    }

    public Object getUserData() { return userData; }
    public void setUserData(Object userData) { this.userData = userData; }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getDano() {
        return dano;
    }

    public void setDano(int dano) {
        this.dano = dano;
    }

    public void atacar(Jugador jugador) {
        if (jugador != null && estaVivo()) {
            jugador.modificarSalud(-this.dano);
        }
    }

    public void recibirDano(int cantidad) {
        this.salud = Math.max(0, this.salud - cantidad);
    }

    public boolean estaVivo() {
        return this.salud > 0;
    }
}