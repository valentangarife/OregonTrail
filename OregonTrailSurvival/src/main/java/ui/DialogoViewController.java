package ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.NPC;
import model.Jugador;

public class DialogoViewController {

    @FXML private Label nombreNPCLabel;
    @FXML private Label tipoNPCLabel;
    @FXML private Label dialogoLabel;
    @FXML private HBox loadingBox;

    private NPC npcActual;
    private Jugador jugador;
    private Stage dialogStage;
    private StringBuilder historialDialogos = new StringBuilder();

    public void setNPC(NPC npc) {
        this.npcActual = npc;
        if (npc != null) {
            nombreNPCLabel.setText(npc.getNombre());
            tipoNPCLabel.setText("[" + capitalize(npc.getTipo()) + "]");
            
            // Limpiar historial y mostrar saludo inicial
            historialDialogos.setLength(0);
            agregarDialogo("💬 " + npc.getNombre() + ": " + npc.saludar());
        }
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void hablarConNPC() {
        if (npcActual == null) return;
        
        agregarDialogo("\n👤 Tú: Hola, ¿cómo estás?");
        mostrarCargando(true);
        
        // Ejecutar en hilo separado para no bloquear la UI
        new Thread(() -> {
            String contexto = construirContexto();
            String dialogo = npcActual.hablar(contexto);
            
            Platform.runLater(() -> {
                mostrarCargando(false);
                agregarDialogo("\n💬 " + npcActual.getNombre() + ": " + dialogo);
            });
        }).start();
    }

    @FXML
    private void pedirConsejo() {
        if (npcActual == null || jugador == null) return;
        
        agregarDialogo("\n Tú: ¿Tienes algún consejo para mí?");
        mostrarCargando(true);
        
        new Thread(() -> {
            String escenario = jugador.getEscenarioActual() != null 
                ? jugador.getEscenarioActual().getNombre() 
                : "Desconocido";
            
            int comida = jugador.getInventario() != null 
                ? jugador.getInventario().obtenerCantidad(enums.TIPORECURSO.COMIDA) 
                : 0;
            
            int municion = jugador.getInventario() != null 
                ? jugador.getInventario().obtenerCantidad(enums.TIPORECURSO.MUNICION) 
                : 0;
            
            String consejo = npcActual.darConsejo(
                jugador.getSalud(), 
                comida, 
                municion, 
                escenario
            );
            
            Platform.runLater(() -> {
                mostrarCargando(false);
                agregarDialogo("\n💡 " + npcActual.getNombre() + ": " + consejo);
            });
        }).start();
    }

    @FXML
    private void cerrarDialogo() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    /**
     * Agrega un nuevo mensaje al historial sin borrar los anteriores
     */
    private void agregarDialogo(String texto) {
        historialDialogos.append(texto);
        dialogoLabel.setText(historialDialogos.toString());
    }

    private void mostrarCargando(boolean mostrar) {
        loadingBox.setVisible(mostrar);
    }

    private String construirContexto() {
        if (jugador == null) {
            return "El jugador se acerca";
        }
        
        StringBuilder contexto = new StringBuilder();
        contexto.append("El jugador tiene ").append(jugador.getSalud()).append(" de salud");
        
        if (jugador.getEscenarioActual() != null) {
            contexto.append(" y está en ").append(jugador.getEscenarioActual().getNombre());
        }
        
        return contexto.toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
