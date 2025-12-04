package ui;

import enums.TIPORECURSO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Inventario;
import model.Jugador;
import model.Recurso;

public class TiendaViewController {

    @FXML
    private Label dineroLabel;

    @FXML
    private Label mensajeLabel;

    private Jugador jugador;

    @FXML
    public void initialize() {
        jugador = OregonApplication.getJugadorActual();
        actualizarDinero();
    }

    private void actualizarDinero() {
        if (jugador != null && dineroLabel != null) {
            dineroLabel.setText("💰 Dinero disponible: $" + jugador.getDinero());
        }
    }

    private void mostrarMensaje(String mensaje, boolean exito) {
        if (mensajeLabel == null) return;

        mensajeLabel.setText(mensaje);
        mensajeLabel.setVisible(true);

        if (exito) {
            mensajeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            mensajeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        // Ocultar después de 3 segundos
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> mensajeLabel.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void comprarComida(javafx.event.ActionEvent event) {
        int precio = 3;
        int cantidad = 5;

        if (jugador.getDinero() >= precio) {
            Inventario inv = jugador.getInventario();
            if (inv == null) {
                mostrarMensaje("❌ Error: Inventario no disponible", false);
                return;
            }

            Recurso comida = new Recurso(TIPORECURSO.COMIDA, cantidad);
            boolean agregado = inv.agregarRecurso(comida);

            if (agregado) {
                jugador.setDinero(jugador.getDinero() - 3);
                actualizarDinero();
                mostrarMensaje("✅ Compraste 🍖 Comida x" + cantidad + " por $" + precio, true);
                System.out.println("✅ Compra exitosa: Comida x" + cantidad);
            } else {
                mostrarMensaje("❌ Inventario lleno!", false);
            }
        } else {
            mostrarMensaje("❌ Dinero insuficiente! Necesitas $" + precio, false);
            return;
        }

    }

    @FXML
    private void comprarMunicion(javafx.event.ActionEvent event) {
        int precio = 5;
        int cantidad = 10;

        if (5 > jugador.getDinero()) {
            mostrarMensaje("❌ Dinero insuficiente! Necesitas $" + precio, false);
            return;
        }

        Inventario inv = jugador.getInventario();
        if (inv == null) {
            mostrarMensaje("❌ Error: Inventario no disponible", false);
            return;
        }

        Recurso municion = new Recurso(TIPORECURSO.MUNICION, cantidad);
        boolean agregado = inv.agregarRecurso(municion);

        if (agregado) {
            jugador.setDinero(jugador.getDinero() - precio);
            actualizarDinero();
            mostrarMensaje("✅ Compraste 🔫 Munición x" + cantidad + " por $" + precio, true);
            System.out.println("✅ Compra exitosa: Munición x" + cantidad);
        } else {
            mostrarMensaje("❌ Inventario lleno!", false);
        }
    }

    @FXML
    private void comprarMedicina(javafx.event.ActionEvent event) {
        int precio = 4;
        int cantidad = 3;

        if (jugador.getDinero() < precio) {
            mostrarMensaje("❌ Dinero insuficiente! Necesitas $" + precio, false);
            return;
        }

        Inventario inv = jugador.getInventario();
        if (inv == null) {
            mostrarMensaje("❌ Error: Inventario no disponible", false);
            return;
        }

        Recurso medicina = new Recurso(TIPORECURSO.MEDICINA, cantidad);
        boolean agregado = inv.agregarRecurso(medicina);

        if (agregado) {
            jugador.setDinero(jugador.getDinero() - precio);
            actualizarDinero();
            mostrarMensaje("✅ Compraste 💊 Medicina x" + cantidad + " por $" + precio, true);
            System.out.println("✅ Compra exitosa: Medicina x" + cantidad);
        } else {
            mostrarMensaje("❌ Inventario lleno!", false);
        }
    }

    @FXML
    private void volverAlJuego(javafx.event.ActionEvent event) {
        OregonApplication.cambiarVista("game-view.fxml");
    }
}
