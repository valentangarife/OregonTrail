package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuPrincipalController {

    @FXML
    private Button btnSilenciar;

    @FXML
    private void onSilenciarClick() {
        OregonApplication.pausarMusica();
    }

    @FXML
    private void onReanudarClick() {
        OregonApplication.reanudarMusica();
    }

    @FXML
    private void abrirJuego(ActionEvent event) {
        // Ahora va al tutorial primero
        OregonApplication.cambiarVista("tutorial-view.fxml");
    }

    @FXML
    private void abrirLogros(ActionEvent event) {
        OregonApplication.cambiarVista("logros-view.fxml");
    }

    @FXML
    private void salir(ActionEvent event) {
        System.exit(0);
    }
}
