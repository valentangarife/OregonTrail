package ui;

import javafx.fxml.FXML;

public class VictoriaController {

    @FXML
    private void volverAlMenu() {
        OregonApplication.cambiarVista("menu-view.fxml");
    }
}
