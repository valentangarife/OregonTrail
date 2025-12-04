package ui;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ManualViewController {

    @FXML
    private void cerrarManual() {
        // Cerrar la ventana del manual
        Stage stage = (Stage) OregonApplication.getManualStage();
        if (stage != null) {
            stage.close();
        }
    }
}
