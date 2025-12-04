package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BienvenidaController {

    @FXML
    private void abrirMenu(ActionEvent event) {
        OregonApplication.cambiarVista("menu-view.fxml");
    }
}
