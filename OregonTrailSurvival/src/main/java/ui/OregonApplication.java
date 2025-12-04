package ui;
import javafx.scene.Parent;
import model.Jugador;
import model.SistemaJuego;
import model.ArbolLogros;
import model.Logro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.IOException;
import java.net.URL;

public class OregonApplication extends Application {

    private static MediaPlayer musicaFondo;
    private static Stage primaryStage;
    private static Stage manualStage; // Ventana independiente para el manual

    private static Jugador jugadorActual;
    private static SistemaJuego sistemaActual;

    // Árbol de logros del juego
    private static ArbolLogros arbolLogros = new ArbolLogros();

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Inicializamos los logros definidos del juego
        inicializarLogros();

        // Primera pantalla
        cambiarVista("bienvenida-view.fxml");
        primaryStage.setTitle("Oregon Trail");
        primaryStage.setResizable(false);

        // EL TAMAÑO FINAL DE TU JUEGO (juegas con estos valores)
        stage.setWidth(1000);
        stage.setHeight(700);

        stage.setResizable(false);
        stage.show();

        iniciarMusicaFondo();
    }



    //Crea y agrega los logros base del juego al árbol.

    private void inicializarLogros() {
        arbolLogros.agregarLogro(new Logro(1, "Primer paso", "Iniciaste tu viaje a Oregón"));
        arbolLogros.agregarLogro(new Logro(2, "Cazador novato", "Derrotaste tu primer autómata"));
        arbolLogros.agregarLogro(new Logro(3, "Superviviente", "Sobreviviste a las montañas rocosas"));
        arbolLogros.agregarLogro(new Logro(4, "Leyenda de Oregón", "Completaste el viaje hasta el Valle de Willamette"));
    }

    // Permite a cualquier controlador acceder al árbol de logros global.
    public static ArbolLogros getArbolLogros() {
        return arbolLogros;
    }


    //Inicia la música de fondo del menú.
    private void iniciarMusicaFondo() {
        try {
            if (musicaFondo != null) {
                return; // Ya está sonando
            }

            URL recurso = getClass().getResource("/audio/oregon_theme.mp3");
            if (recurso == null) {
                System.out.println("ERROR: No se encontró el archivo de audio /audio/oregon_theme.mp3");
                return;
            }

            Media media = new Media(recurso.toExternalForm());
            musicaFondo = new MediaPlayer(media);

            musicaFondo.setCycleCount(MediaPlayer.INDEFINITE); // Repetir en loop
            musicaFondo.setVolume(0.3); // Volumen al 30%
            musicaFondo.play();

        } catch (Exception e) {
            System.out.println(" ERROR al intentar reproducir música:");
            e.printStackTrace();
        }
    }

    // === CONTROL DE MÚSICA ===
    public static void pausarMusica() {
        if (musicaFondo != null) musicaFondo.pause();
    }

    public static void reanudarMusica() {
        if (musicaFondo != null) musicaFondo.play();
    }

    public static void detenerMusica() {
        if (musicaFondo != null) musicaFondo.stop();
    }


    //Cambiar de vista a otro FXML dentro de resources/ui/
    public static void cambiarVista(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    OregonApplication.class.getResource("/ui/" + fxmlName)
            );

            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println(" ERROR cargando FXML: " + fxmlName);
            e.printStackTrace();
        }
    }

    // === MANUAL DE USUARIO ===
    /**
     * Abre el manual de usuario en una ventana independiente
     */
    public static void abrirManual() {
        try {
            // Si ya existe una ventana del manual, solo la traemos al frente
            if (manualStage != null && manualStage.isShowing()) {
                manualStage.toFront();
                return;
            }

            // Crear nueva ventana
            FXMLLoader loader = new FXMLLoader(
                    OregonApplication.class.getResource("/ui/manual-view.fxml")
            );

            Scene scene = new Scene(loader.load());
            manualStage = new Stage();
            manualStage.setTitle("📖 Manual de Usuario - Oregon Trail");
            manualStage.setScene(scene);
            manualStage.setResizable(false);
            
            // Hacer que la ventana sea modal (opcional)
            // manualStage.initModality(Modality.APPLICATION_MODAL);
            
            manualStage.show();

            System.out.println("✅ Manual de usuario abierto");

        } catch (IOException e) {
            System.err.println("❌ ERROR cargando manual-view.fxml");
            e.printStackTrace();
        }
    }

    public static Stage getManualStage() {
        return manualStage;
    }

    // === GETTERS / SETTERS ===
    public static Jugador getJugadorActual() { return jugadorActual; }
    public static void setJugadorActual(Jugador jugador) { jugadorActual = jugador; }

    public static SistemaJuego getSistemaActual() { return sistemaActual; }
    public static void setSistemaActual(SistemaJuego sistema) { sistemaActual = sistema; }

    public static void main(String[] args) {
        launch();
    }
}
