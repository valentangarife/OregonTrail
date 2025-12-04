package ui;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class TutorialController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private void initialize() {
        // Hacer que la imagen de fondo se adapte al tamaño del StackPane / ventana
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    @FXML
    private void irAProfesion() {
        System.out.println("=== BOTÓN CLICKEADO: irAProfesion ===");
        // Ir a la pantalla de profesiones
        OregonApplication.cambiarVista("profesion-view.fxml");
    }
}
