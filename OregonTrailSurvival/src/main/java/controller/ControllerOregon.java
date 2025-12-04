package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import model.Escenario;
import model.Jugador;
import model.Enemigo;
import model.Recurso;

public class ControllerOregon {

    @FXML
    private Pane paneJuego;

    private Escenario escenarioActual;
    private Jugador jugador;

    private double SCALE_FACTOR = 40.0;

    private Thread hiloEnemigos;
    private Thread hiloRecursos;


    public void initialize() {
        System.out.println("Controller cargado correctamente.");
    }

    public void cargarEscenario(Escenario escenario, Jugador jugador) {
        this.escenarioActual = escenario;
        this.jugador = jugador;


        mostrarJugador();
        iniciarHiloEnemigos();
        iniciarHiloRecursos();
    }

    // Mostrar Jugador (Añadir a Pane y Posicionar con Escala)
    private void mostrarJugador() {
        if (jugador == null) return;

        Platform.runLater(() -> {
            if (!paneJuego.getChildren().contains(jugador.getSprite())) {
                paneJuego.getChildren().add(jugador.getSprite());
            }

            // Posicionar el sprite del jugador usando la escala
            jugador.getSprite().setLayoutX(jugador.getPosicionX() * SCALE_FACTOR);
            jugador.getSprite().setLayoutY(jugador.getPosicionY() * SCALE_FACTOR);
        });
    }

    //  HILO: ENEMIGOS (Añadir a Pane y Posicionar con Escala)
    private void iniciarHiloEnemigos() {

        hiloEnemigos = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1900);

                    // 1. Lógica del Modelo (en el hilo de fondo)
                    escenarioActual.generarEnemigos();

                    // 2. Lógica de UI (en el Hilo de Aplicación de JavaFX)
                    Platform.runLater(() -> {
                        for (Enemigo e : escenarioActual.getEnemigos()) {
                            if (!paneJuego.getChildren().contains(e.getSprite())) {

                                // Aplicar posición del modelo y al sprite con el factor de escala antes de añadirlo.
                                e.getSprite().setLayoutX(e.getPosicionX() * SCALE_FACTOR);
                                e.getSprite().setLayoutY(e.getPosicionY() * SCALE_FACTOR);

                                paneJuego.getChildren().add(e.getSprite());
                                System.out.println("[HILO ENEMIGOS] Añadido: " + e.getId());
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        hiloEnemigos.setDaemon(true);
        hiloEnemigos.start();
    }

    //  HILO: RECURSOS (Añadir a Pane y Posicionar con Escala)
    private void iniciarHiloRecursos() {

        hiloRecursos = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(2500);

                    // 1. Lógica del Modelo (en el hilo de fondo)
                    escenarioActual.generarRecurso();

                    // 2. Lógica de UI (en el Hilo de Aplicación de JavaFX)
                    Platform.runLater(() -> {
                        for (Recurso r : escenarioActual.getRecursosDisponibles()) {
                            if (r.getSprite() != null && !paneJuego.getChildren().contains(r.getSprite())) {

                                // Si Escenario no asigna coords, las asignamos aquí
                                // usando el tamaño lógico del pane / SCALE_FACTOR.
                                if (r.getX() < 0 || r.getY() < 0) {
                                    // Genera X/Y lógicos
                                    int anchoLogico = (int) (paneJuego.getWidth() / SCALE_FACTOR);
                                    int altoLogico = (int) (paneJuego.getHeight() / SCALE_FACTOR);
                                    int x = (int) (Math.random() * anchoLogico);
                                    int y = (int) (Math.random() * altoLogico);
                                    r.setX(x);
                                    r.setY(y);
                                }

                                // Posicionar el sprite con el factor de escala
                                r.getSprite().setLayoutX(r.getX() * SCALE_FACTOR);
                                r.getSprite().setLayoutY(r.getY() * SCALE_FACTOR);

                                paneJuego.getChildren().add(r.getSprite());
                                System.out.println("[HILO RECURSOS] Añadido: " + r.getTipo());
                            }
                        }
                    });
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        hiloRecursos.setDaemon(true);
        hiloRecursos.start();
    }
}