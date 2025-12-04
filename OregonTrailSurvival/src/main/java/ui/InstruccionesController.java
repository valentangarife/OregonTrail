package ui;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.ArbolLogros;
import model.Logro;

public class InstruccionesController {

    @FXML
    private StackPane rootPane;
    
    @FXML
    private Label espacioLabel;
    
    @FXML
    private Button continuarButton;

    @FXML
    private void initialize() {
        System.out.println("📜 Pantalla de instrucciones cargada");
        
        // Configurar animación del texto "Presiona ESPACIO"
        if (espacioLabel != null) {
            animarTextoEspacio();
        }
        
        // Configurar captura de tecla ESPACIO
        Platform.runLater(() -> {
            if (rootPane != null) {
                rootPane.setFocusTraversable(true);
                rootPane.requestFocus();
                
                rootPane.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.SPACE) {
                        System.out.println("⌨️ Tecla ESPACIO presionada");
                        continuarAJuego(null);
                    }
                });
                
                System.out.println("✅ Captura de tecla ESPACIO configurada");
            }
        });
    }
    
    /**
     * Anima el texto "Presiona ESPACIO" con efecto de parpadeo
     */
    private void animarTextoEspacio() {
        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), espacioLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }

    /**
     * Continúa al juego después de leer las instrucciones
     */
    @FXML
    private void continuarAJuego(ActionEvent event) {
        System.out.println("✅ Usuario leyó las instrucciones, iniciando juego...");
        
        // Marcar logro de inicio de viaje
        marcarLogroInicioViaje();
        
        // Ir a la vista del juego
        OregonApplication.cambiarVista("game-view.fxml");
    }

    /**
     * Vuelve a la pantalla de preparar viaje
     */
    @FXML
    private void volverAtras(ActionEvent event) {
        System.out.println("← Volviendo a preparar viaje...");
        OregonApplication.cambiarVista("prepararViaje-view.fxml");
    }

    /**
     * Marca el logro "Primer paso" cuando el jugador inicia su viaje
     */
    private void marcarLogroInicioViaje() {
        ArbolLogros arbolLogros = OregonApplication.getArbolLogros();
        if (arbolLogros == null) {
            System.err.println("❌ ERROR: ArbolLogros es null");
            return;
        }

        // Marcar logro ID 1: "Primer paso"
        Logro primerPaso = arbolLogros.buscarLogroPorId(1);
        if (primerPaso != null && !primerPaso.isObtenido()) {
            arbolLogros.marcarLogroComoObtenido(1);
            System.out.println("🏆 Logro desbloqueado: Primer paso - Iniciaste tu viaje a Oregón!");
        }
    }
}
