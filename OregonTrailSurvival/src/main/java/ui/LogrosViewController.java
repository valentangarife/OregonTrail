package ui;

import model.ArbolLogros;
import model.Logro;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class LogrosViewController {

    @FXML
    private ListView<String> listViewLogros;

    private ArbolLogros arbolLogros;

    @FXML
    public void initialize() {
        // Obtener el árbol global desde OregonApplication
        arbolLogros = OregonApplication.getArbolLogros();
        cargarLogros();
    }

    // (Opcional) por si algún día quieres inyectarlo manualmente
    public void setArbolLogros(ArbolLogros arbolLogros) {
        this.arbolLogros = arbolLogros;
        cargarLogros();
    }
    private void cargarLogros() {
        if (listViewLogros == null || arbolLogros == null) {
            return;
        }

        listViewLogros.getItems().clear();

        String idsEnOrden = arbolLogros.obtenerIdsEnOrden();
        if (idsEnOrden == null || idsEnOrden.isBlank()) {
            return;
        }

        String[] ids = idsEnOrden.split(" ");

        for (String idTexto : ids) {
            if (idTexto.isBlank()) continue;

            int id = Integer.parseInt(idTexto);
            Logro l = arbolLogros.buscarLogroPorId(id);

            if (l != null) {

                String icono = l.isObtenido() ? "✔" : "✖";
                String estadoTexto = l.isObtenido() ? "OBTENIDO" : "PENDIENTE";

                // Primera línea
                String linea1 = icono + "  [" + l.getId() + "] " + l.getNombre() +
                        "  –  (" + estadoTexto + ")";

                // Segunda línea (sangrada)
                String linea2 = "    " + l.getDescripcion();

                // Agregar ambas líneas juntas
                listViewLogros.getItems().add(linea1);
                listViewLogros.getItems().add(linea2);
                listViewLogros.getItems().add(""); // línea vacía separadora
            }
        }
    }


    @FXML
    private void volverAlJuego(javafx.event.ActionEvent event) {
        OregonApplication.cambiarVista("game-view.fxml");
    }
    
    @FXML
    private void volverAlMenu(javafx.event.ActionEvent event) {
        OregonApplication.cambiarVista("menu-view.fxml");
    }
}
