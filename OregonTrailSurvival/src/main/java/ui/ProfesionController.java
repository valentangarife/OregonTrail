package ui;

import enums.PROFESION;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.ArbolLogros;
import model.Escenario;
import model.Jugador;
import model.SistemaJuego;

import java.util.ArrayList;
import java.util.List;

public class ProfesionController {

    // Para ajustar el fondo al tamaño de la ventana
    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private void initialize() {
        // Hacer que la imagen de fondo siempre llene la ventana
        if (backgroundImage != null && rootPane != null) {
            backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    // ================== BOTONES ==================

    @FXML
    public void seleccionarBanquero() {
        crearPartidaConProfesion(PROFESION.BANQUERO);
    }

    @FXML
    public void seleccionarCarpintero() {
        crearPartidaConProfesion(PROFESION.CARPINTERO);
    }

    @FXML
    public void seleccionarGranjero() {
        crearPartidaConProfesion(PROFESION.GRANJERO);
    }

    @FXML
    public void volverMenu() {
        OregonApplication.cambiarVista("menu-view.fxml");
    }

    // ================== LÓGICA ==================

    private void crearPartidaConProfesion(PROFESION profesion) {

        System.out.println(">>> Profesión elegida: " + profesion);

        // Nombre inicial vacío; se escribe en Preparar Viaje
        Jugador jugador = new Jugador("", profesion);

        // Crear escenarios del viaje
        List<Escenario> escenarios = crearEscenariosBase();

        // Árbol de logros (por ahora vacío)
        ArbolLogros arbolLogros = new ArbolLogros();

        // Crear sistema de juego
        SistemaJuego sistema = new SistemaJuego(jugador, escenarios, false, arbolLogros);
        sistema.iniciarPartida();

        // Guardar referencias globales
        OregonApplication.setJugadorActual(jugador);
        OregonApplication.setSistemaActual(sistema);

        // Ir a "Preparar viaje"
        OregonApplication.cambiarVista("prepararViaje-view.fxml");
    }

    private List<Escenario> crearEscenariosBase() {
        List<Escenario> escenarios = new ArrayList<>();

        Escenario llanuras = new Escenario(
                "Llanuras",
                "El inicio del viaje hacia Oregón. Terreno relativamente tranquilo.",
                1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());

        Escenario montanas = new Escenario(
                "Montañas Rocosas",
                "Terreno peligroso, frío y con enemigos más fuertes.",
                2,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());

        Escenario rio = new Escenario(
                "Río Columbia",
                "El tramo final del viaje, con grandes riesgos climáticos.",
                3,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());

        escenarios.add(llanuras);
        escenarios.add(montanas);
        escenarios.add(rio);

        return escenarios;
    }

}
