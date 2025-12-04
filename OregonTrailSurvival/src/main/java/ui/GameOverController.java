package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.Jugador;

public class GameOverController {

    @FXML
    private ImageView gameOverImage;
    
    @FXML
    private Label mensajeLabel;

    @FXML
    public void initialize() {
        System.out.println(" Pantalla de Game Over cargada");
        
        // Obtener información del jugador si está disponible
        Jugador jugador = OregonApplication.getJugadorActual();
        
        if (jugador != null && mensajeLabel != null) {
            String mensaje = "Has muerto en tu viaje hacia Oregón\n";
            mensaje += "Profesión: " + jugador.getProfesion() + "\n";
            mensaje += "Dinero restante: $" + jugador.getDinero();
            
            mensajeLabel.setText(mensaje);
            System.out.println("📊 " + mensaje);
        }
    }

    @FXML
    public void volverAlMenu(ActionEvent event) {
        System.out.println("Volviendo al menú principal...");
        OregonApplication.cambiarVista("menu-view.fxml");
    }
}
