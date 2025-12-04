package ui;

import enums.PROFESION;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.ArbolLogros;
import model.Inventario;
import model.Jugador;
import model.Logro;
import model.SistemaJuego;

public class PrepararViajeController {

    @FXML private TextField nombreField;
    @FXML private TextField comidaField;
    @FXML private TextField municionField;
    @FXML private TextField medicinaField;

    @FXML private Label profesionLabel;
    @FXML private Label vidaLabel;
    @FXML private Label dineroInicialLabel;

    @FXML private Label precioComidaLabel;
    @FXML private Label precioMunicionLabel;
    @FXML private Label precioMedicinaLabel;

    @FXML private Label totalComidaLabel;
    @FXML private Label totalMunicionLabel;
    @FXML private Label totalMedicinaLabel;

    @FXML private Label dineroRestanteLabel;

    @FXML private Button comenzarButton;

    // Estado interno
    private Jugador jugador;
    private SistemaJuego sistema;

    private int dineroInicial;

    // Precios unitarios
    private final int PRECIO_COMIDA   = 20;
    private final int PRECIO_MUNICION = 2;
    private final int PRECIO_MEDICINA = 25;

    @FXML
    private void initialize() {

        // Obtener jugador y sistema desde la aplicación principal
        jugador  = OregonApplication.getJugadorActual();
        sistema  = OregonApplication.getSistemaActual();

        System.out.println(">>> PrepararViajeController.initialize, jugador = " + jugador);

        // Si algo salió mal, volvemos al menú
        if (jugador == null) {
            System.err.println("Jugador es null, regresando al menú");
            OregonApplication.cambiarVista("menu-view.fxml");
            return;
        }

        // ⚠ Asegurarnos de que el botón se haya inyectado
        assert comenzarButton != null : "fx:id=\"comenzarButton\" no fue inyectado desde el FXML";

        // ⚠ Forzar que el botón use este método, por si el onAction del FXML está raro
        comenzarButton.setOnAction(e -> comenzarViaje());

        // Nombre del jugador
        if (jugador.getNombre() != null && !jugador.getNombre().isEmpty()) {
            nombreField.setText(jugador.getNombre());
        }

        // Profesión
        PROFESION profesion = jugador.getProfesion();
        profesionLabel.setText(formatearProfesion(profesion));

        // Vida inicial
        vidaLabel.setText(String.valueOf(jugador.getSalud()));

        // Dinero inicial según profesión
        dineroInicial = jugador.getDinero();
        dineroInicialLabel.setText("$ " + dineroInicial);

        // Precios unitarios
        precioComidaLabel.setText("$ " + PRECIO_COMIDA);
        precioMunicionLabel.setText("$ " + PRECIO_MUNICION);
        precioMedicinaLabel.setText("$ " + PRECIO_MEDICINA);

        // Valores por defecto
        comidaField.setText("0");
        municionField.setText("0");
        medicinaField.setText("0");

        // recalcular todo al escribir en los campos de cantidad
        agregarListenerCantidad(comidaField);
        agregarListenerCantidad(municionField);
        agregarListenerCantidad(medicinaField);

        // Cálculo inicial
        recalcularCostosYDinero();
    }

    // Asegura solo números y recalcula
    private void agregarListenerCantidad(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                field.setText(newVal.replaceAll("[^\\d]", ""));
            }
            recalcularCostosYDinero();
        });
    }

    private String formatearProfesion(PROFESION profesion) {
        if (profesion == null) return "Sin profesión";
        switch (profesion) {
            case BANQUERO:   return "Banquero de Boston";
            case CARPINTERO: return "Carpintero de Ohio";
            case GRANJERO:   return "Granjero de Illinois";
            default:         return profesion.name();
        }
    }

    private int parseCantidad(TextField field) {
        String text = field.getText();
        if (text == null || text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Recalcula totales y dinero restante cada vez que el usuario escribe
    private void recalcularCostosYDinero() {
        int cantComida   = parseCantidad(comidaField);
        int cantMunicion = parseCantidad(municionField);
        int cantMedicina = parseCantidad(medicinaField);

        int totalComida   = cantComida   * PRECIO_COMIDA;
        int totalMunicion = cantMunicion * PRECIO_MUNICION;
        int totalMedicina = cantMedicina * PRECIO_MEDICINA;

        int totalGastado = totalComida + totalMunicion + totalMedicina;
        int restante     = dineroInicial - totalGastado;

        totalComidaLabel.setText("$ " + totalComida);
        totalMunicionLabel.setText("$ " + totalMunicion);
        totalMedicinaLabel.setText("$ " + totalMedicina);

        dineroRestanteLabel.setText("$ " + restante);

        if (restante < 0) {
            dineroRestanteLabel.setStyle(
                    "-fx-text-fill: #ff8080; -fx-font-size: 18px; -fx-font-weight: bold;"
                            + "-fx-effect: dropshadow( gaussian , black , 4, 0.7 , 1 , 1 );"
            );
            if (comenzarButton != null) {
                comenzarButton.setDisable(true);
            }
        } else {
            dineroRestanteLabel.setStyle(
                    "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
                            + "-fx-effect: dropshadow( gaussian , black , 4, 0.7 , 1 , 1 );"
            );
            if (comenzarButton != null) {
                comenzarButton.setDisable(false);
            }
        }
    }

    @FXML
    private void comenzarViaje() {
        System.out.println(">>> CLICK en comenzarViaje()");

        // 1. Validar nombre
        String nombre = nombreField.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlerta("Nombre requerido",
                    "Por favor escribe el nombre del jugador antes de comenzar el viaje.");
            return;
        }

        // Actualizar el nombre del jugador
        jugador.setNombre(nombre);

        // 2. Calcular cantidades y verificar dinero
        int cantComida   = parseCantidad(comidaField);
        int cantMunicion = parseCantidad(municionField);
        int cantMedicina = parseCantidad(medicinaField);

        int totalComida   = cantComida   * PRECIO_COMIDA;
        int totalMunicion = cantMunicion * PRECIO_MUNICION;
        int totalMedicina = cantMedicina * PRECIO_MEDICINA;

        int totalGastado = totalComida + totalMunicion + totalMedicina;
        int restante     = dineroInicial - totalGastado;

        if (restante < 0) {
            mostrarAlerta("Dinero insuficiente",
                    "No puedes gastar más dinero del que tienes. Reduce alguna cantidad.");
            return;
        }


        // 5. Actualizar dinero del jugador
        jugador.setDinero(restante);

        // 6. Actualizar inventario con los recursos comprados
        Inventario inventario = jugador.getInventario();
        if (inventario != null) {
            inventario.agregarComida(cantComida);
            inventario.agregarMunicion(cantMunicion);
            inventario.agregarMedicina(cantMedicina);
        }

        // 7. Asegurarnos de que OregonApplication tenga jugador y sistema
        OregonApplication.setJugadorActual(jugador);
        if (sistema == null) {
            sistema = new SistemaJuego();
        }
        OregonApplication.setSistemaActual(sistema);

        // 8. Ir a la pantalla de instrucciones ANTES del juego
        System.out.println(">>> Cambiando a instrucciones-view.fxml");
        OregonApplication.cambiarVista("instrucciones-view.fxml");
    }


    @FXML
    private void volverProfesiones() {
        OregonApplication.cambiarVista("profesion-view.fxml");
    }

    // Utilidad para mostrar mensajes emergentes
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
