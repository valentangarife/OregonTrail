package controller;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.Jugador;
import model.Escenario;
import model.Recurso;
import model.Enemigo;
import enums.TIPORECURSO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ResourceThread extends Thread {

    private final Jugador jugador;
    private final Pane gamePane;
    private final double escala;
    private final Random random = new Random();
    private SpawnListener spawnListener;
    
    // Límites de spawn
    private static final int MAX_RECURSOS = 3;
    private static final int MAX_ENEMIGOS = 4;
    
    // Mapa para validar posiciones
    private int[][] mapData;
    private int rows;
    private int cols;
    
    // Contadores
    private int recursosActivos = 0;
    private int enemigosActivos = 0;

    public ResourceThread(Jugador jugador, Pane gamePane, double escala) {
        this.jugador = jugador;
        this.gamePane = gamePane;
        this.escala = escala;
        setDaemon(true);
    }

    public void setSpawnListener(SpawnListener listener) {
        this.spawnListener = listener;
    }
    
    // Método para recibir información del mapa
    public void setMapData(int[][] mapData, int rows, int cols) {
        this.mapData = mapData;
        this.rows = rows;
        this.cols = cols;
    }
    
    // Método para actualizar contadores
    public void setRecursosActivos(int count) {
        this.recursosActivos = count;
    }
    
    public void setEnemigosActivos(int count) {
        this.enemigosActivos = count;
    }

    @Override
    public void run() {
        System.out.println(">>> ResourceThread iniciado");

        try {
            Thread.sleep(1000); // Dar tiempo inicial
        } catch (Exception ignored) {}

        // Loop para generar recurrentemente
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Solo generar si no se ha alcanzado el límite
                if (recursosActivos < MAX_RECURSOS) {
                    generarRecursoAleatorio();
                } else {
                    System.out.println("⏸️ Recursos al máximo: " + recursosActivos + "/" + MAX_RECURSOS);
                }
                
                if (enemigosActivos < MAX_ENEMIGOS) {
                    generarEnemigoAleatorio();
                } else {
                    System.out.println("⏸️ Enemigos al máximo: " + enemigosActivos + "/" + MAX_ENEMIGOS);
                }
                
                Thread.sleep(5000); // Cada 5 segundos
            } catch (InterruptedException e) {
                System.out.println(">>> ResourceThread interrumpido");
                break;
            }
        }

        System.out.println(">>> ResourceThread finalizado");
    }

    // ========================================================================
    // VALIDACIÓN DE POSICIONES
    // ========================================================================
    private boolean esPosicionValida(int x, int y) {
        if (mapData == null || x < 0 || y < 0 || y >= rows || x >= cols) {
            return false;
        }
        
        int tileCode = mapData[y][x];
        
        // Solo arena (2, 'S' o 'I')
        return tileCode == 2 || tileCode == (int)'S' || tileCode == (int)'I';
    }
    
    private int[] encontrarPosicionAleatoria() {
        if (mapData == null) {
            System.err.println(" ERROR: mapData es null");
            return null;
        }
        
        List<int[]> posicionesValidas = new ArrayList<>();
        
        // Buscar todas las posiciones de arena
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (esPosicionValida(x, y)) {
                    posicionesValidas.add(new int[]{x, y});
                }
            }
        }
        
        if (posicionesValidas.isEmpty()) {
            System.err.println(" ERROR: No hay posiciones válidas en el mapa");
            return null;
        }
        
        // Seleccionar una posición aleatoria
        return posicionesValidas.get(random.nextInt(posicionesValidas.size()));
    }
    
    /**
     * Encuentra una posición válida cerca del jugador (radio de 5-10 tiles)
     */
    private int[] encontrarPosicionCercaDelJugador() {
        if (mapData == null || jugador == null) {
            System.err.println(" ERROR: mapData o jugador es null");
            return null;
        }
        
        int jugadorX = jugador.getPosicionX();
        int jugadorY = jugador.getPosicionY();
        
        List<int[]> posicionesValidas = new ArrayList<>();
        
        // Buscar posiciones en un radio de 5-10 tiles del jugador
        int radioMin = 5;
        int radioMax = 10;
        
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                // Calcular distancia al jugador
                int distanciaX = Math.abs(x - jugadorX);
                int distanciaY = Math.abs(y - jugadorY);
                double distancia = Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);
                
                // Si está en el rango deseado y es una posición válida
                if (distancia >= radioMin && distancia <= radioMax && esPosicionValida(x, y)) {
                    posicionesValidas.add(new int[]{x, y});
                }
            }
        }
        
        // Si no hay posiciones cerca, buscar en todo el mapa
        if (posicionesValidas.isEmpty()) {
            System.out.println("⚠ No hay posiciones cerca del jugador, buscando en todo el mapa");
            return encontrarPosicionAleatoria();
        }
        
        // Seleccionar una posición aleatoria de las válidas
        int[] posicion = posicionesValidas.get(random.nextInt(posicionesValidas.size()));
        System.out.println(" Posición encontrada cerca del jugador: (" + posicion[0] + ", " + posicion[1] +
                         ") | Jugador en: (" + jugadorX + ", " + jugadorY + ")");
        return posicion;
    }

    // ========================================================================
    // GENERACIÓN ALEATORIA
    // ========================================================================
    private void generarRecursoAleatorio() {
        Platform.runLater(() -> {
            Escenario esc = jugador.getEscenarioActual();
            if (esc == null) {
                System.err.println(" ERROR: Escenario es null al generar recurso aleatorio");
                return;
            }
            
            // Verificar límite
            if (recursosActivos >= MAX_RECURSOS) {
                System.out.println(" Límite de recursos alcanzado (" + MAX_RECURSOS + ")");
                return;
            }

            // Encontrar posición válida (solo en arena)
            int[] pos = encontrarPosicionAleatoria();
            if (pos == null) {
                System.err.println(" No se pudo encontrar posición válida para recurso");
                return;
            }
            
            int x = pos[0];
            int y = pos[1];

            // Alternar entre tipos de recursos
            TIPORECURSO tipo;
            String path;
            int rand = random.nextInt(3);
            
            if (rand == 0) {
                tipo = TIPORECURSO.COMIDA;
                path = "/images/recursos/comida.png";
            } else if (rand == 1) {
                tipo = TIPORECURSO.MUNICION;
                path = "/images/recursos/11.png";
            } else {
                tipo = TIPORECURSO.MEDICINA;
                path = "/images/recursos/12.png";
            }

            Recurso recurso = new Recurso(tipo, 5, x, y, path);
            esc.getRecursos().add(recurso);
            recursosActivos++;

            System.out.println(" Recurso aleatorio (" + tipo + ") generado en (" + x + "," + y + ") [" + recursosActivos + "/" + MAX_RECURSOS + "]");

            try {
                Image img = new Image(getClass().getResource(recurso.getSpritePath()).toExternalForm());
                ImageView view = new ImageView(img);
                view.setFitWidth(30);
                view.setFitHeight(30);
                view.setLayoutX(x * escala);
                view.setLayoutY(y * escala);
                
                // Hacer el recurso clickeable
                view.setStyle("-fx-cursor: hand;");
                view.setPickOnBounds(true);
                
                // Añadir manejador de clic para recoger el recurso
                view.setOnMouseClicked(event -> {
                    System.out.println("️ Clic en recurso: " + tipo + " en (" + x + ", " + y + ")");
                    
                    // Intentar agregar al inventario
                    boolean agregado = jugador.agregarRecurso(recurso);
                    
                    if (agregado) {
                        // Remover el sprite del gamePane
                        Platform.runLater(() -> {
                            gamePane.getChildren().remove(view);
                            System.out.println(" Recurso recogido: " + tipo + " x" + recurso.getCantidad());
                        });
                        
                        // Actualizar contador
                        recursosActivos--;
                        System.out.println(" Recursos activos: " + recursosActivos + "/" + MAX_RECURSOS);
                        
                        // Notificar al listener para actualizar HUD
                        if (spawnListener != null) {
                            spawnListener.onRecursoRecogido(recurso);
                        }
                    } else {
                        System.out.println(" Inventario lleno! No se pudo recoger: " + tipo);
                    }
                });

                gamePane.getChildren().add(view);
                recurso.setUserData(view);

                if (spawnListener != null) {
                    spawnListener.onRecursoGenerado(recurso);
                }
            } catch (Exception e) {
                System.err.println(" ERROR cargando recurso aleatorio: " + e.getMessage());
                recursosActivos--; // Revertir contador si falla
                e.printStackTrace();
            }
        });
    }

    private void generarEnemigoAleatorio() {
        Platform.runLater(() -> {
            Escenario esc = jugador.getEscenarioActual();
            if (esc == null) {
                System.err.println(" ERROR: Escenario es null al generar enemigo aleatorio");
                return;
            }
            
            // Verificar límite
            if (enemigosActivos >= MAX_ENEMIGOS) {
                System.out.println("Límite de enemigos alcanzado " + MAX_ENEMIGOS );
                return;
            }

            // Encontrar posición válida CERCA DEL JUGADOR (radio 5-10 tiles)
            int[] pos = encontrarPosicionCercaDelJugador();
            if (pos == null) {
                System.err.println(" No se pudo encontrar posición válida para enemigo");
                return;
            }
            
            int x = pos[0];
            int y = pos[1];

            String id = "Enemigo_" + System.currentTimeMillis();
            Enemigo enemigo = new Enemigo(id, "Lobo", 50, 1, x, y); // Daño reducido a 1 (1 vida por ataque)
            esc.getEnemigos().add(enemigo);
            enemigosActivos++;

            System.out.println(" Enemigo generado en ESCENARIO " + esc.getNumero() + " (" + esc.getNombre() + ") en posición (" + x + "," + y + ") [" + enemigosActivos + "/" + MAX_ENEMIGOS + "]");

            try {
                Image img = new Image(getClass().getResource("/images/enemigos/loboEnemie.png").toExternalForm());
                ImageView view = new ImageView(img);
                view.setFitWidth(40);
                view.setFitHeight(40);
                view.setLayoutX(x * escala);
                view.setLayoutY(y * escala);

                gamePane.getChildren().add(view);
                enemigo.setUserData(view);

                if (spawnListener != null) {
                    spawnListener.onEnemigoGenerado(enemigo);
                }
            } catch (Exception e) {
                System.err.println(" ERROR cargando enemigo aleatorio: " + e.getMessage());
                enemigosActivos--; // Revertir contador si falla
                e.printStackTrace();
            }
        });
    }
}