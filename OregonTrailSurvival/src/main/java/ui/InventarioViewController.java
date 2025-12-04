package ui;

import enums.TIPORECURSO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Inventario;
import model.Jugador;
import model.Recurso;

public class InventarioViewController {

    @FXML
    private ListView<String> listViewRecursos;

    @FXML
    private Label capacidadLabel;

    @FXML
    private Label dineroLabel;

    private Jugador jugador;

    @FXML
    public void initialize() {
        jugador = OregonApplication.getJugadorActual();
        cargarInventario();
    }

    private void cargarInventario() {
        if (listViewRecursos == null || jugador == null) {
            return;
        }

        listViewRecursos.getItems().clear();

        Inventario inventario = jugador.getInventario();
        if (inventario == null) {
            listViewRecursos.getItems().add("❌ Inventario vacío");
            return;
        }

        // Actualizar labels de capacidad y dinero
        int cantidadItems = inventario.getRecursos().size();
        int capacidadMax = inventario.getCapacidadMaxima();
        capacidadLabel.setText("Capacidad: " + cantidadItems + "/" + capacidadMax);
        dineroLabel.setText("Dinero: $" + jugador.getDinero());

        // Mostrar recursos agrupados por tipo
        if (inventario.getRecursos().isEmpty()) {
            listViewRecursos.getItems().add("📭 No tienes recursos en el inventario");
        } else {
            for (Recurso recurso : inventario.getRecursos()) {
                String icono = obtenerIconoRecurso(recurso.getTipo());
                String nombre = obtenerNombreRecurso(recurso.getTipo());
                String linea = icono + " " + nombre + " x" + recurso.getCantidad();
                listViewRecursos.getItems().add(linea);
            }
        }

        // Agregar línea separadora
        listViewRecursos.getItems().add("");
        listViewRecursos.getItems().add("━━━━━━━━━━━━━━━━━━━━━━━━━━");
        listViewRecursos.getItems().add("");

        // Mostrar resumen por tipo
        listViewRecursos.getItems().add("📊 RESUMEN:");
        int comida = inventario.obtenerCantidad(TIPORECURSO.COMIDA);
        int municion = inventario.obtenerCantidad(TIPORECURSO.MUNICION);
        int medicina = inventario.obtenerCantidad(TIPORECURSO.MEDICINA);

        if (comida > 0) {
            listViewRecursos.getItems().add("  🍖 Comida: " + comida);
        }
        if (municion > 0) {
            listViewRecursos.getItems().add("  🔫 Munición: " + municion);
        }
        if (medicina > 0) {
            listViewRecursos.getItems().add("  💊 Medicina: " + medicina);
        }
    }

    private String obtenerIconoRecurso(TIPORECURSO tipo) {
        if (tipo == null) return "📦";
        return switch (tipo) {
            case COMIDA -> "🍖";
            case MUNICION -> "🔫";
            case MEDICINA -> "💊";
            default -> "📦";
        };
    }

    private String obtenerNombreRecurso(TIPORECURSO tipo) {
        if (tipo == null) return "Recurso";
        return switch (tipo) {
            case COMIDA -> "Comida";
            case MUNICION -> "Munición";
            case MEDICINA -> "Medicina";
            default -> "Recurso";
        };
    }

    @FXML
    private void volverAlJuego(javafx.event.ActionEvent event) {
        OregonApplication.cambiarVista("game-view.fxml");
    }
}