package model;

import enums.PROFESION;
import enums.TIPORECURSO;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Representa al jugador del juego.
 */
public class Jugador {

    // ================= CAMPOS BÁSICOS =================
    private String nombre;
    private int salud;
    private Inventario inventario;
    private Arma armaActual;
    private Escenario escenarioActual;

    // Posición en el mundo (puedes tratarlo como píxeles o tiles según tu lógica)
    private double posicionX;
    private double posicionY;

    private PROFESION profesion;
    private int dinero;

    // Velocidad para movimiento “fluido” (si llegas a usar deltaTime)
    private double velocidad = 100.0;

    // ================= SPRITE =================
    private ImageView sprite;

    // ================= CONSTRUCTORES =================

    /**
     * Constructor usado en algunos tests: solo nombre y salud.
     */
    public Jugador(String nombre, int salud) {
        this.nombre = nombre;
        this.salud = salud;
        this.profesion = null;
        this.posicionX = 0;
        this.posicionY = 0;
        this.inventario = new Inventario(10);
        this.dinero = 0;
        iniciarSprite(); // usa sprite genérico
    }

    /**
     * Constructor principal para el juego: nombre vacío + profesión.
     */
    public Jugador(String nombre, PROFESION profesion) {
        this.nombre = nombre;
        this.profesion = profesion;
        this.salud = 100; //  Cambiado de 3 a 100 para sistema de vida porcentual
        this.posicionX = 0;
        this.posicionY = 0;
        this.inventario = new Inventario(10);
        this.dinero = calcularDineroInicial(profesion);
        iniciarSprite(); // sprite según profesión
    }

    // ================= INICIALIZACIÓN DE SPRITE =================

    /**
     * Crea el sprite del jugador usando la profesión.
     */
    private void iniciarSprite() {
        String ruta = switch (profesion) {
            case BANQUERO   -> "/images/player/banquero/banquero_down1.png";
            case CARPINTERO -> "/images/player/carpintero/carpintero_down1.png";
            case GRANJERO   -> "/images/player/granjero/granjero_down1.png";
        };

        Image img = null;
        try {
            var is = getClass().getResourceAsStream(ruta);
            if (is != null) {
                img = new Image(is);
            } else {
                System.err.println("No se encontró sprite del jugador en: " + ruta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (img != null) {
            sprite = new ImageView(img);
        } else {
            sprite = new ImageView(); // fallback vacío
        }

        sprite.setFitWidth(40);
        sprite.setFitHeight(40);
        sprite.setPreserveRatio(true);

        sprite.setLayoutX(posicionX);
        sprite.setLayoutY(posicionY);
    }

    /**
     * Devuelve el nodo gráfico real del jugador (por si lo necesitas en algún Pane).
     */
    public Node getSprite() {
        return sprite;
    }

    // ================= LÓGICA DE DINERO / PROFESIÓN =================

    private int calcularDineroInicial(PROFESION profesion) {
        if (profesion == null) return 0;

        switch (profesion) {
            case GRANJERO:   return 400;
            case BANQUERO:   return 1600;
            case CARPINTERO: return 800;
            default:         return 0;
        }
    }

    // ================= POSICIÓN / MOVIMIENTO =================

    public int getPosicionX() {
        return (int) Math.round(posicionX);
    }

    public int getPosicionY() {
        return (int) Math.round(posicionY);
    }

    public void setPosicionX(int x) {
        this.posicionX = x;
        if (sprite != null) {
            sprite.setLayoutX(x);
        }
    }

    public void setPosicionY(int y) {
        this.posicionY = y;
        if (sprite != null) {
            sprite.setLayoutY(y);
        }
    }


    public void actualizarMovimiento(boolean up, boolean down, boolean left, boolean right, double deltaTime) {
        double deltaX = 0.0;
        double deltaY = 0.0;

        if (up)    deltaY -= velocidad * deltaTime;
        if (down)  deltaY += velocidad * deltaTime;
        if (left)  deltaX -= velocidad * deltaTime;
        if (right) deltaX += velocidad * deltaTime;

        // Normalizar diagonal
        if (deltaX != 0 && deltaY != 0) {
            double factor = 1.0 / Math.sqrt(2);
            deltaX *= factor;
            deltaY *= factor;
        }

        posicionX += deltaX;
        posicionY += deltaY;

        if (sprite != null) {
            sprite.setLayoutX(posicionX);
            sprite.setLayoutY(posicionY);
        }
    }

    // ================= ESCENARIO =================

    public void cambiaEscenario(Escenario nuevoEscenario) {
        if (nuevoEscenario == null) return;
        this.escenarioActual = nuevoEscenario;
        // Si algún día quieres cambiar posición inicial al cambiar de mapa, hazlo aquí.
    }

    public Escenario getEscenarioActual() {
        return escenarioActual;
    }

    public void setEscenarioActual(Escenario escenarioActual) {
        this.escenarioActual = escenarioActual;
    }

    // ================= ACCIONES DE JUEGO =================

    public void atacar(Enemigo objetivo) {
        if (objetivo != null && armaActual != null && estaVivo()) {
            armaActual.disparar(objetivo);
        }
    }

    public void recolectar(Recurso recurso) {
        if (recurso != null && inventario != null) {
            inventario.agregarRecurso(recurso);
        }
    }

    public boolean agregarRecurso(Recurso r) {
        if (r == null || inventario == null) return false;
        inventario.agregarRecurso(r);
        return true;
    }

    public void agregarRecurso(TIPORECURSO tipo, int cantidad) {
        if (inventario == null) return;
        inventario.agregarRecurso(new Recurso(tipo, cantidad));
    }

    public void modificarSalud(int cantidad) {
        int nueva = salud + cantidad;
        salud = Math.max(nueva, 0);
    }

    public boolean estaVivo() {
        return salud > 0;
    }



    // ================= GETTERS / SETTERS SIMPLES =================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public PROFESION getProfesion() {
        return profesion;
    }

    public void setProfesion(PROFESION profesion) {
        this.profesion = profesion;
    }

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    public Arma getArmaActual() {
        return armaActual;
    }

    public void setArmaActual(Arma armaActual) {
        this.armaActual = armaActual;
    }

    // ================= DEBUG =================

    @Override
    public String toString() {
        return nombre + " - Salud: " + salud +
                " - Dinero: " + dinero +
                " - Prof: " + profesion +
                " - Pos: (" + getPosicionX() + "," + getPosicionY() + ")";
    }
}
