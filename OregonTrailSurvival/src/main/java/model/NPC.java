package model;

import javafx.scene.image.ImageView;
import service.GeminiService;

/**
 * Clase para NPCs (Non-Player Characters) que generan diálogos con Gemini
 */
public class NPC {
    
    private String id;
    private String nombre;
    private String tipo; // "comerciante", "viajero", "guia"
    private int posicionX;
    private int posicionY;
    private ImageView sprite;
    private GeminiService geminiService;
    private String ultimoDialogo;

    public NPC(String id, String nombre, String tipo, int posicionX, int posicionY) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicionX = posicionX;
        this.posicionY = posicionY;
        this.geminiService = new GeminiService();
        this.ultimoDialogo = "";
    }

    /**
     * Genera un diálogo basado en el contexto actual
     */
    public String hablar(String contexto) {
        ultimoDialogo = geminiService.generarDialogo(contexto, tipo);
        System.out.println("💬 " + nombre + ": " + ultimoDialogo);
        return ultimoDialogo;
    }

    /**
     * Genera un saludo inicial
     */
    public String saludar() {
        String contexto = "El jugador se acerca por primera vez";
        return hablar(contexto);
    }

    /**
     * Da un consejo basado en el estado del jugador
     */
    public String darConsejo(int salud, int comida, int municion, String escenario) {
        ultimoDialogo = geminiService.generarConsejo(salud, comida, municion, escenario);
        System.out.println("💬 " + nombre + ": " + ultimoDialogo);
        return ultimoDialogo;
    }

    /**
     * Reacciona a un evento del juego
     */
    public String reaccionarEvento(String evento, String escenario, int salud, int recursos) {
        ultimoDialogo = geminiService.generarDialogoEvento(evento, escenario, salud, recursos);
        System.out.println("💬 " + nombre + ": " + ultimoDialogo);
        return ultimoDialogo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getPosicionX() { return posicionX; }
    public void setPosicionX(int posicionX) { this.posicionX = posicionX; }
    public int getPosicionY() { return posicionY; }
    public void setPosicionY(int posicionY) { this.posicionY = posicionY; }
    public ImageView getSprite() { return sprite; }
    public void setSprite(ImageView sprite) { this.sprite = sprite; }
    public String getUltimoDialogo() { return ultimoDialogo; }
}
