package ui;

import controller.ResourceThread;
import controller.SpawnListener;
import enums.NOMBRE_ARMA;
import enums.TIPORECURSO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameViewController {

    // ==== NODOS FXML ====
    @FXML private Canvas mapCanvas;
    @FXML private Pane gamePane;
    @FXML private StackPane gameStackPane;
    @FXML private ImageView playerImage;
    @FXML private ImageView healthBarImage;

    @FXML private Label municionLabel;
    @FXML private Label inventarioLabel;
    @FXML private Label dineroLabel;
    @FXML private Label mensajeAccionLabel;
    
    // Suministros en tiempo real
    @FXML private Label comidaLabel;
    @FXML private Label municionInventarioLabel;
    @FXML private Label medicinaLabel;
    @FXML private Label vidaLabel;
    
    // Panel de armas
    @FXML private Label armaActualLabel;
    @FXML private Label municionArmaLabel;
    @FXML private Label danoArmaLabel;
    
    // Retícula
    @FXML private Pane crosshairPane;
    @FXML private ImageView crosshairImage;
    
    // Arma en mano del jugador
    @FXML private ImageView armaEnManoImage;
    
    // ==== SISTEMA DE ANIMACIÓN DEL JUGADOR ====
    private Map<String, Image> playerSprites = new HashMap<>();
    private String currentDirection = "down"; // down, up, left, rigth
    private int spriteFrame = 1; // 1 o 2
    private long lastSpriteChange = 0;
    private static final long SPRITE_CHANGE_DELAY = 200; // milisegundos entre frames
    
    // Portal automático (código 5 en el mapa)

    @FXML
    private void verInventario(ActionEvent e) {
        OregonApplication.cambiarVista("inventario-view.fxml");
    }
    
    @FXML
    private void abrirTienda(ActionEvent e) {
        OregonApplication.cambiarVista("tienda-view.fxml");
    }
    
    @FXML
    private void abrirManual(ActionEvent e) {
        OregonApplication.abrirManual();
    }
    
    /**
     * Abre la ventana de diálogo con un NPC
     */
    private void abrirDialogoNPC(model.NPC npc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/dialogo-view.fxml"));
            javafx.scene.Parent root = loader.load();
            
            DialogoViewController controller = loader.getController();
            controller.setNPC(npc);
            controller.setJugador(jugador);
            
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(" Conversación con " + npc.getNombre());
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.initModality(javafx.stage.Modality.NONE); // No bloquear el juego
            controller.setDialogStage(dialogStage);
            
            dialogStage.show();
            
            System.out.println(" Abriendo diálogo con " + npc.getNombre());
        } catch (Exception e) {
            System.err.println("Error abriendo diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    
    // ========================================================================
    // CARGA DE ESCENARIOS Y MAPAS
    // ========================================================================
    private void cargarEscenario(int numero) {
        escenarioActual = numero;
        
        String ruta;
        String nombreEscenario;
        
        switch (numero) {
            case 1:
                ruta = "/maps/map_llanuras.txt";
                nombreEscenario = "Llanuras y Praderas";
                break;
            case 2:
                ruta = "/maps/map_montañas.txt";
                nombreEscenario = "Montañas Rocosas";
                break;
            case 3:
                ruta = "/maps/map_rio.txt";
                nombreEscenario = "Río y Áreas Cercanas a Oregón";
                break;
            default:
                ruta = "/maps/map_llanuras.txt";
                nombreEscenario = "Llanuras y Praderas";
        }
        
        System.out.println("🗺️ Cargando " + nombreEscenario + " desde " + ruta);
        
        loadMap(ruta);
        ajustarTamañoCanvas();
        drawMap();
        
        // Obtener el escenario del mapa
        Escenario esc = escenariosMap.get(numero);
        if (esc != null) {
            esc.setAncho(cols);
            esc.setAlto(rows);
            jugador.setEscenarioActual(esc);
        }
        
        System.out.println("📐 " + nombreEscenario + " cargado: " + cols + "x" + rows + " tiles");
        
        colocarJugadorEnInicioDeCamino();
        limpiarEntidades();
        crearNPCsDelEscenario(numero);
        
        // Verificar logros al cambiar de escenario
        verificarYMarcarLogros();
    }
    
    private void loadMap(String resourcePath) {
        try (InputStream is = GameViewController.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("❌ No encontré el mapa: " + resourcePath);
                System.err.println("❌ Intentando rutas alternativas...");
                return;
            }
            
            System.out.println("✅ InputStream obtenido para: " + resourcePath);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            List<String> lines = new ArrayList<>();
            
            String ln;
            while ((ln = br.readLine()) != null) {
                if (!ln.trim().isEmpty()) {
                    lines.add(ln);
                }
            }
            
            if (lines.isEmpty()) {
                System.err.println("❌ El mapa está vacío: " + resourcePath);
                return;
            }
            
            rows = lines.size();
            cols = lines.get(0).length();
            mapData = new int[rows][cols];
            
            System.out.println("📊 Procesando mapa: " + rows + " filas x " + cols + " columnas");
            
            for (int r = 0; r < rows; r++) {
                String rowText = lines.get(r);
                for (int c = 0; c < cols && c < rowText.length(); c++) {
                    char ch = rowText.charAt(c);
                    if (Character.isDigit(ch)) {
                        mapData[r][c] = ch - '0';
                        // Debug para portales
                        if (ch == '5') {
                            System.out.println("🚪 PORTAL ENCONTRADO EN MAPA: Fila=" + r + ", Col=" + c + ", Valor=" + mapData[r][c]);
                        }
                    } else {
                        mapData[r][c] = (int) ch;
                    }
                }
            }
            
            System.out.println("✅ Mapa cargado exitosamente: " + resourcePath);
            System.out.println("✅ Dimensiones finales: " + rows + " x " + cols);
            
        } catch (IOException e) {
            System.err.println("❌ Error al cargar mapa: " + resourcePath);
            e.printStackTrace();
        }
    }
    
    private void ajustarTamañoCanvas() {
        if (rows <= 0 || cols <= 0) return;
        mapCanvas.setWidth(cols * TILE_SIZE);
        mapCanvas.setHeight(rows * TILE_SIZE);
    }
    
    private void drawMap() {
        if (mapData == null) return;
        
        GraphicsContext g = mapCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int code = mapData[r][c];
                Image tile = tileSet.get(code);
                if (tile != null) {
                    double x = c * TILE_SIZE;
                    double y = r * TILE_SIZE;
                    g.drawImage(tile, x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
    
    private void colocarJugadorEnInicioDeCamino() {
        System.out.println("📍 Buscando primer cuadrito de arena en el mapa...");
        System.out.println("📏 Dimensiones del mapa: " + rows + " filas x " + cols + " columnas");
        
        // Buscar el primer cuadrito de arena (código 2) en el mapa
        buscarPrimeraArena();
        
        // Actualizar posición visual del jugador
        if (playerImage != null) {
            playerImage.setTranslateX(playerCol * TILE_SIZE);
            playerImage.setTranslateY(playerRow * TILE_SIZE);
        }
        System.out.println("✅ Jugador colocado en primer cuadrito de arena: (" + playerRow + ", " + playerCol + ")");
    }
    
    private void buscarPrimeraArena() {
        System.out.println("🔍 Buscando primera posición de arena...");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mapData[r][c] == 2) {
                    playerRow = r;
                    playerCol = c;
                    System.out.println("   ✅ Primera arena encontrada en: (" + r + ", " + c + ")");
                    return;
                }
            }
        }
    }
    
    private void buscarArenaCercana() {
        // Buscar arena en un radio de 3 tiles
        for (int radio = 1; radio <= 3; radio++) {
            for (int dr = -radio; dr <= radio; dr++) {
                for (int dc = -radio; dc <= radio; dc++) {
                    int r = playerRow + dr;
                    int c = playerCol + dc;
                    if (r >= 0 && r < rows && c >= 0 && c < cols) {
                        if (mapData[r][c] == 2) {
                            playerRow = r;
                            playerCol = c;
                            System.out.println("   ✅ Arena encontrada en: (" + r + ", " + c + ")");
                            return;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Actualiza la posición visual del sprite del jugador según playerRow y playerCol
     */
    private void updatePlayerImagePosition() {
        if (playerImage != null) {
            playerImage.setTranslateX(playerCol * TILE_SIZE);
            playerImage.setTranslateY(playerRow * TILE_SIZE);
        }
    }
    
    private void limpiarEntidades() {
        Platform.runLater(() -> {
            // Remover sprites de enemigos
            for (Enemigo e : enemigosActivos) {
                Object userData = e.getUserData();
                if (userData instanceof ImageView) {
                    gamePane.getChildren().remove(userData);
                }
            }
            
            // Remover sprites de recursos
            for (Recurso r : recursosActivos) {
                Object userData = r.getSprite();
                if (userData instanceof ImageView) {
                    gamePane.getChildren().remove(userData);
                }
            }
            
            // Remover sprites de NPCs
            for (model.NPC npc : npcsActivos) {
                if (npc.getSprite() != null) {
                    gamePane.getChildren().remove(npc.getSprite());
                }
            }
            
            // Limpiar listas
            enemigosActivos.clear();
            recursosActivos.clear();
            npcsActivos.clear();
            
            // ⚠️ CRÍTICO: Actualizar contadores en el ResourceThread
            if (resourceThread != null) {
                resourceThread.setEnemigosActivos(0);
                resourceThread.setRecursosActivos(0);
                System.out.println("🔄 Contadores del ResourceThread reseteados: Enemigos=0, Recursos=0");
            }
        });
    }
    
    /**
     * Crea NPCs específicos para cada escenario
     */
    private void crearNPCsDelEscenario(int numeroEscenario) {
        Platform.runLater(() -> {
            switch (numeroEscenario) {
                case 1: // Llanuras
                    crearNPC("npc_comerciante", "Juan el Comerciante", "comerciante", 35, 8);
                    crearNPC("npc_viajero", "María la Viajera", "viajero", 20, 10);
                    break;
                case 2: // Montañas
                    crearNPC("npc_guia", "Pedro el Guía", "guia", 30, 7);
                    break;
                case 3: // Río
                    crearNPC("npc_comerciante2", "Carlos el Pescador", "comerciante", 25, 11);
                    crearNPC("npc_viajero2", "Ana la Exploradora", "viajero", 15, 8);
                    break;
            }
            System.out.println("✅ NPCs creados para escenario " + numeroEscenario);
        });
    }
    
    /**
     * Crea un NPC y lo agrega al mapa
     */
    private void crearNPC(String id, String nombre, String tipo, int x, int y) {
        model.NPC npc = new model.NPC(id, nombre, tipo, x, y);
        
        // Intentar cargar imagen de profesión
        String rutaImagen = switch (tipo) {
            case "comerciante" -> "/images/profesiones/banquero.png";
            case "viajero" -> "/images/profesiones/granjero.png";
            case "guia" -> "/images/profesiones/carpintero.png";
            default -> "/images/profesiones/granjero.png";
        };
        
        ImageView sprite = new ImageView();
        boolean imagenCargada = false;
        
        try {
            InputStream is = getClass().getResourceAsStream(rutaImagen);
            if (is != null) {
                sprite.setImage(new Image(is));
                imagenCargada = true;
                System.out.println("✅ Imagen cargada para NPC: " + rutaImagen);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error cargando imagen de NPC: " + e.getMessage());
        }
        
        // Si no se cargó la imagen, crear un círculo de respaldo
        if (!imagenCargada) {
            System.out.println("⚠️ Usando círculo de respaldo para NPC: " + nombre);
            
            javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(20);
            circulo.setFill(switch (tipo) {
                case "comerciante" -> javafx.scene.paint.Color.GOLD;
                case "viajero" -> javafx.scene.paint.Color.LIGHTBLUE;
                case "guia" -> javafx.scene.paint.Color.LIGHTGREEN;
                default -> javafx.scene.paint.Color.GRAY;
            });
            circulo.setStroke(javafx.scene.paint.Color.BLACK);
            circulo.setStrokeWidth(3);
            
            // Agregar efecto de brillo
            javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
            circulo.setEffect(glow);
            
            // Posicionar el círculo
            circulo.setTranslateX(x * TILE_SIZE + 16);
            circulo.setTranslateY(y * TILE_SIZE + 16);
            
            // Hacer clickeable y CONSUMIR el evento
            circulo.setOnMouseClicked(event -> {
                event.consume();
                abrirDialogoNPC(npc);
            });
            circulo.setStyle("-fx-cursor: hand;");
            
            // Agregar al pane
            gamePane.getChildren().add(circulo);
            npcsActivos.add(npc);
            
            System.out.println("💬 NPC creado (círculo): " + nombre + " (" + tipo + ") en (" + x + ", " + y + ")");
            return;
        }
        
        // Si la imagen se cargó, configurar el sprite
        sprite.setFitWidth(40);
        sprite.setFitHeight(40);
        sprite.setPreserveRatio(true);
        sprite.setTranslateX(x * TILE_SIZE);
        sprite.setTranslateY(y * TILE_SIZE);
        
        // Agregar efecto visual de brillo
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(javafx.scene.paint.Color.YELLOW);
        shadow.setRadius(15);
        shadow.setSpread(0.5);
        sprite.setEffect(shadow);
        
        // Hacer clickeable y CONSUMIR el evento para evitar disparar
        sprite.setOnMouseClicked(event -> {
            event.consume(); // ⚠️ IMPORTANTE: Evita que el clic llegue al gameStackPane
            abrirDialogoNPC(npc);
        });
        sprite.setStyle("-fx-cursor: hand;");
        
        // Agregar al pane
        gamePane.getChildren().add(sprite);
        npc.setSprite(sprite);
        npcsActivos.add(npc);
        
        System.out.println("💬 NPC creado (imagen): " + nombre + " (" + tipo + ") en (" + x + ", " + y + ")");
    }
    
    private void loadTileImages() {
        tileSet.clear();
        // Códigos numéricos
        tileSet.put(0, loadTile("/images/tiles/tree.png")); // ✅ Verde → ARBOLITOS
        tileSet.put(1, loadTile("/images/tiles/tree.png"));  // Árboles
        tileSet.put(2, loadTile("/images/tiles/sand.png"));  // Camino amarillo (ARENA)
        tileSet.put(3, loadTile("/images/tiles/water.png")); // Agua
        tileSet.put(4, loadTile("/images/tiles/sand.png"));  // Arena (decorativo, caminable)
        tileSet.put(5, loadTile("/images/tiles/portal.jpg")); // Portal de teletransportación
        tileSet.put(6, loadTile("/images/tiles/wall.png"));  // Rocas/montañas
        
        // Códigos de letras (para mapas antiguos)
        tileSet.put((int)'G', loadTile("/images/tiles/tree.png")); // ✅ 'G' → ARBOLITOS
        tileSet.put((int)'T', loadTile("/images/tiles/tree.png"));
        tileSet.put((int)'S', loadTile("/images/tiles/sand.png"));
        tileSet.put((int)'W', loadTile("/images/tiles/water.png"));
        tileSet.put((int)'P', loadTile("/images/tiles/portal.jpg"));
        tileSet.put((int)'B', loadTile("/images/tiles/tree.png"));
        tileSet.put((int)'E', loadTile("/images/tiles/tree.png")); // ✅ 'E' → ARBOLITOS
        tileSet.put((int)'I', loadTile("/images/tiles/sand.png"));
    }
    
    private Image loadTile(String path) {
        InputStream is = GameViewController.class.getResourceAsStream(path);
        if (is == null) {
            System.err.println("❌ No se encontró tile: " + path);
            return null;
        }
        return new Image(is);
    }

    // ==== MODELO ====
    private Jugador jugador;
    private SistemaJuego sistema;
    private int escenarioActual = 1;
    private List<Enemigo> enemigosActivos = new ArrayList<>();
    private List<Recurso> recursosActivos = new ArrayList<>();
    private List<Proyectil> proyectilesEnemigos = new ArrayList<>();
    private List<Proyectil> proyectilesJugador = new ArrayList<>();
    private List<model.NPC> npcsActivos = new ArrayList<>();

    // ==== GESTIÓN DE ESCENARIOS ====
    private Map<Integer, Escenario> escenariosMap = new HashMap<>();
    private Escenario escenarioLlanuras;
    private Escenario escenarioMontanas;
    private Escenario escenarioRio;

    // ==== MAPA (NUMÉRICO) ====
    private static final int TILE_SIZE = 32;
    private int[][] mapData;
    private int rows;
    private int cols;
    private final Map<Integer, Image> tileSet = new HashMap<>();

    // posición del jugador en coordenadas de tile
    private int playerRow = 0;
    private int playerCol = 0;
    
    // ==== ANIMACIÓN DEL JUGADOR ====
    private String direccionActual = "down"; // up, down, left, right
    private int frameAnimacion = 1; // 1 o 2 para alternar sprites
    private long ultimoCambioFrame = 0;
    private static final long FRAME_DELAY = 150; // milisegundos entre frames (más rápido)
    
    // ==== MOVIMIENTO FLUIDO ====
    private long ultimoMovimiento = 0;
    private static final long MOVIMIENTO_DELAY = 100; // milisegundos entre movimientos (más fluido)
    private final java.util.Set<javafx.scene.input.KeyCode> teclasPresionadas = new java.util.HashSet<>();

    // ==== HILO DE SPAWNING ====
    private ResourceThread resourceThread;
    private Thread enemigoAIThread;
    private Thread proyectilesThread;
    private volatile boolean juegoActivo = true;
    private double escala = TILE_SIZE;
    
    // ==== SISTEMA DE ARMAS ====
    private Arma armaActual;
    private Arma rifle;
    private Arma revolver;
    private int indiceArmaActual = 0; // 0 = rifle, 1 = revolver

    // ==== INICIALIZACIÓN ====
    @FXML
    private void initialize() {
        jugador = OregonApplication.getJugadorActual();
        sistema = OregonApplication.getSistemaActual();

        if (jugador == null) {
            System.err.println("❌ ERROR: Jugador es null");
            OregonApplication.cambiarVista("menu-view.fxml");
            return;
        }

        loadTileImages();
        inicializarEscenarios(); // Crear y conectar escenarios
        inicializarArmas(); // Inicializar sistema de armas
        inicializarReticula(); // Inicializar crosshair
        cargarEscenario(1);
        initPlayerSprite();
        updateHUD();
        setupKeyboard();
        setupMouse(); // Configurar control del mouse

        // ⚠️ CRÍTICO: INICIAR HILOS
        iniciarHiloDeSpawning();
        iniciarHiloDeEnemigoAI();
        iniciarHiloDeProyectiles();
        
        // Verificar logros al iniciar (por si ya se cumplieron algunos)
        verificarYMarcarLogros();
    }
    


    // ========================================================================
    // INICIALIZACIÓN Y CONEXIÓN DE ESCENARIOS
    // ========================================================================
    private void inicializarEscenarios() {
        // Crear los 3 escenarios (sin dimensiones aún, se asignarán al cargar)
        escenarioLlanuras = new Escenario(
                "Llanuras y Praderas",
                "Extensas praderas verdes con árboles dispersos. El inicio de tu viaje hacia el oeste.",
                1, 0, 0,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarioMontanas = new Escenario(
                "Montañas Rocosas",
                "Terreno montañoso y peligroso. Atraviesa con cuidado estas formaciones rocosas.",
                2, 0, 0,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarioRio = new Escenario(
                "Río y Áreas Cercanas a Oregón",
                "El río Columbia te guía hacia tu destino final. ¡Oregón está cerca!",
                3, 0, 0,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Conectar escenarios bidireccionales usando PuntoAcceso
        conectarEscenarios(escenarioLlanuras, escenarioMontanas);
        conectarEscenarios(escenarioMontanas, escenarioRio);

        // Guardar en el mapa
        escenariosMap.put(1, escenarioLlanuras);
        escenariosMap.put(2, escenarioMontanas);
        escenariosMap.put(3, escenarioRio);

        System.out.println("✅ Escenarios inicializados y conectados");
    }

    /**
     * Conecta dos escenarios de forma bidireccional usando PuntoAcceso
     */
    private void conectarEscenarios(Escenario escenarioA, Escenario escenarioB) {
        if (escenarioA != null && escenarioB != null) {
            // Crear punto de acceso de A hacia B
            PuntoAcceso puntoAB = new PuntoAcceso(escenarioB,
                    "Portal hacia " + escenarioB.getNombre(), true);
            escenarioA.getPuntosAcceso().add(puntoAB);

            // Crear punto de acceso de B hacia A
            PuntoAcceso puntoBA = new PuntoAcceso(escenarioA,
                    "Portal hacia " + escenarioA.getNombre(), true);
            escenarioB.getPuntosAcceso().add(puntoBA);

            System.out.println("🔗 Conectados: " + escenarioA.getNombre() + " ↔ " + escenarioB.getNombre());
        }
    }
    
    // ========================================================================
    // INICIALIZACIÓN DE ARMAS
    // ========================================================================
    private void inicializarArmas() {
        // Crear Rifle Avancarga: más daño, menos munición, recarga lenta
        rifle = new Arma(
            NOMBRE_ARMA.RIFLE_AVANCARGA,
            30,  // daño
            6,   // capacidad cargador
            6,   // munición inicial
            3.0, // tiempo recarga (segundos)
            1.5  // velocidad disparo
        );
        
        // Crear Revólver: menos daño, más munición, recarga rápida
        revolver = new Arma(
            NOMBRE_ARMA.REVOLVER,
            15,  // daño
            12,  // capacidad cargador
            12,  // munición inicial
            1.5, // tiempo recarga (segundos)
            0.5  // velocidad disparo
        );
        
        // Establecer rifle como arma inicial
        armaActual = rifle;
        indiceArmaActual = 0;
        
        actualizarPanelArmas();
        inicializarArmaEnMano();
        System.out.println("✅ Armas inicializadas: RIFLE y REVOLVER");
    }
    
    /**
     * Inicializa la imagen del arma en mano del jugador
     */
    private void inicializarArmaEnMano() {
        if (armaEnManoImage == null || armaActual == null) {
            System.err.println("❌ armaEnManoImage o armaActual es null");
            return;
        }
        
        // Cargar imagen del arma inicial (rifle)
        String rutaArma = armaActual.getArma() == NOMBRE_ARMA.RIFLE_AVANCARGA 
            ? "/images/proyectiles/rifle.png" 
            : "/images/proyectiles/revolver.png";
        
        try {
            InputStream is = getClass().getResourceAsStream(rutaArma);
            if (is != null) {
                armaEnManoImage.setImage(new Image(is));
                armaEnManoImage.setFitWidth(60);
                armaEnManoImage.setFitHeight(60);
                armaEnManoImage.setPreserveRatio(true);
                armaEnManoImage.setVisible(true);
                System.out.println("✅ Arma en mano inicializada: " + rutaArma);
            } else {
                System.err.println("❌ No se encontró la imagen del arma: " + rutaArma);
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando imagen del arma en mano: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarPanelArmas() {
        if (armaActual == null) return;
        
        String nombreArma = armaActual.getArma() == NOMBRE_ARMA.RIFLE_AVANCARGA 
            ? "RIFLE AVANCARGA" : "REVOLVER";
        
        armaActualLabel.setText(nombreArma);
        municionArmaLabel.setText("[" + armaActual.getMunicionRestante() + "/" + 
                                  armaActual.getCapacidadCargador() + "]");
        danoArmaLabel.setText("Daño: " + armaActual.getDaño());
        
        // Cambiar color según munición
        if (armaActual.getMunicionRestante() == 0) {
            municionArmaLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else if (armaActual.getMunicionRestante() <= armaActual.getCapacidadCargador() / 3) {
            municionArmaLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 16px;");
        } else {
            municionArmaLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 16px;");
        }
    }
    
    // ========================================================================
    // INICIALIZACIÓN DE RETÍCULA
    // ========================================================================
    private void inicializarReticula() {
        if (crosshairImage == null) {
            System.err.println("❌ ERROR: crosshairImage es null");
            return;
        }
        
        // Crear una retícula simple con código (círculo con cruz)
        // Por ahora usaremos un estilo CSS
        crosshairImage.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,0,0,0.8), 10, 0, 0, 0);");
        
        // Intentar cargar imagen de retícula si existe
        try {
            InputStream is = getClass().getResourceAsStream("/images/ui/crosshair.png");
            if (is != null) {
                crosshairImage.setImage(new Image(is));
            } else {
                // Si no hay imagen, crear una retícula con texto
                System.out.println("⚠️ No se encontró imagen de retícula, usando estilo por defecto");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error cargando retícula: " + e.getMessage());
        }
        
        crosshairImage.setVisible(true);
        System.out.println("✅ Retícula inicializada");
    }
    
    // ========================================================================
    // CONTROL DEL MOUSE
    // ========================================================================
    private void setupMouse() {
        if (gameStackPane == null) {
            System.err.println("❌ ERROR: gameStackPane es null");
            return;
        }
        
        // Mover la retícula con el mouse Y actualizar posición del arma
        gameStackPane.setOnMouseMoved(event -> {
            if (crosshairImage != null) {
                double x = event.getX() - crosshairImage.getFitWidth() / 2;
                double y = event.getY() - crosshairImage.getFitHeight() / 2;
                crosshairImage.setLayoutX(x);
                crosshairImage.setLayoutY(y);
            }
            
            // Actualizar posición y rotación del arma en mano
            actualizarPosicionArmaEnMano(event.getX(), event.getY());
        });
        
        // Disparar con clic izquierdo
        gameStackPane.setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                disparar(event.getX(), event.getY());
            }
        });
        
        System.out.println("✅ Control del mouse configurado");
    }
    
    /**
     * Muestra el arma en la mano del jugador apuntando hacia el mouse
     */
    private void mostrarArmaEnMano(double mouseX, double mouseY) {
        if (armaEnManoImage == null || armaActual == null) return;
        
        // Cargar imagen del arma según el arma actual
        String rutaArma = armaActual.getArma() == NOMBRE_ARMA.RIFLE_AVANCARGA 
            ? "/images/proyectiles/rifle.png" 
            : "/images/proyectiles/revolver.png";
        
        try {
            InputStream is = getClass().getResourceAsStream(rutaArma);
            if (is != null) {
                armaEnManoImage.setImage(new Image(is));
                armaEnManoImage.setVisible(true);
                
                // Posicionar y rotar el arma
                actualizarPosicionArmaEnMano(mouseX, mouseY);
                
                // Ocultar el arma después de 0.3 segundos (efecto de disparo)
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        Platform.runLater(() -> {
                            // No ocultar, mantener visible
                            // armaEnManoImage.setVisible(false);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                
                System.out.println("🔫 Arma mostrada en mano del jugador");
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando imagen del arma: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la posición y rotación del arma en mano según la posición del mouse
     */
    private void actualizarPosicionArmaEnMano(double mouseX, double mouseY) {
        if (armaEnManoImage == null || !armaEnManoImage.isVisible()) return;
        
        // Posición del jugador (centro)
        double jugadorX = playerCol * escala + 32;
        double jugadorY = playerRow * escala + 32;
        
        // Calcular ángulo hacia el mouse
        double dx = mouseX - jugadorX;
        double dy = mouseY - jugadorY;
        double angulo = Math.toDegrees(Math.atan2(dy, dx));
        
        // Posicionar el arma ligeramente adelante del jugador en dirección al mouse
        double distanciaDelJugador = 20; // Píxeles adelante del jugador
        double armaX = jugadorX + Math.cos(Math.toRadians(angulo)) * distanciaDelJugador;
        double armaY = jugadorY + Math.sin(Math.toRadians(angulo)) * distanciaDelJugador;
        
        // Ajustar para centrar la imagen del arma
        armaEnManoImage.setLayoutX(armaX - armaEnManoImage.getFitWidth() / 2);
        armaEnManoImage.setLayoutY(armaY - armaEnManoImage.getFitHeight() / 2);
        
        // Rotar el arma hacia el mouse
        armaEnManoImage.setRotate(angulo);
    }
    
    private void disparar(double mouseX, double mouseY) {
        if (armaActual == null || !armaActual.estaDisponible()) {
            mostrarMensajeAccion("❌ Sin munición! Presiona R para recargar");
            System.out.println("❌ No se puede disparar: sin munición");
            return;
        }
        
        // Gastar munición
        armaActual.setMunicionRestante(armaActual.getMunicionRestante() - 1);
        actualizarPanelArmas();
        
        System.out.println("💥 DISPARO hacia (" + mouseX + ", " + mouseY + ")");
        
        // Mostrar arma en la mano del jugador
        mostrarArmaEnMano(mouseX, mouseY);
        
        // Crear proyectil visual del jugador
        crearProyectilJugador(mouseX, mouseY);
    }
    
    private void crearProyectilJugador(double objetivoX, double objetivoY) {
        // Posición inicial del proyectil (centro del jugador)
        double jugadorX = playerCol * escala + 32;
        double jugadorY = playerRow * escala + 32;
        
        // Calcular dirección
        double dx = objetivoX - jugadorX;
        double dy = objetivoY - jugadorY;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        
        if (distancia == 0) return;
        
        // Normalizar y aplicar velocidad
        double velocidad = 8.0; // Más rápido que proyectiles enemigos
        double velocidadX = (dx / distancia) * velocidad;
        double velocidadY = (dy / distancia) * velocidad;
        
        // Crear proyectil
        Proyectil proyectil = new Proyectil(jugadorX, jugadorY, velocidadX, velocidadY, armaActual.getDaño());
        proyectil.setPersigue(false); // No persigue, va en línea recta
        
        // Crear sprite visual del proyectil
        Platform.runLater(() -> {
            // Seleccionar imagen según el arma
            String rutaBala = armaActual.getArma() == NOMBRE_ARMA.RIFLE_AVANCARGA 
                ? "/images/proyectiles/balaRifle.png" 
                : "/images/proyectiles/balaRevolver.png";
            
            try {
                InputStream is = getClass().getResourceAsStream(rutaBala);
                if (is != null) {
                    Image imgBala = new Image(is);
                    ImageView balaSprite = new ImageView(imgBala);
                    balaSprite.setFitWidth(20);
                    balaSprite.setFitHeight(20);
                    balaSprite.setPreserveRatio(true);
                    
                    // Calcular rotación para que apunte hacia el objetivo
                    double angulo = Math.toDegrees(Math.atan2(dy, dx));
                    balaSprite.setRotate(angulo);
                    
                    balaSprite.setLayoutX(jugadorX - 10);
                    balaSprite.setLayoutY(jugadorY - 10);
                    
                    // Efecto de brillo
                    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.6);
                    balaSprite.setEffect(glow);
                    
                    gamePane.getChildren().add(balaSprite);
                    proyectil.setSprite(balaSprite);
                    
                    proyectilesJugador.add(proyectil);
                } else {
                    // Si no hay imagen, usar círculo amarillo
                    javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(6);
                    circulo.setFill(javafx.scene.paint.Color.YELLOW);
                    circulo.setStroke(javafx.scene.paint.Color.ORANGE);
                    circulo.setStrokeWidth(2);
                    circulo.setLayoutX(jugadorX);
                    circulo.setLayoutY(jugadorY);
                    
                    javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
                    circulo.setEffect(glow);
                    
                    gamePane.getChildren().add(circulo);
                    
                    ImageView sprite = new ImageView();
                    sprite.setUserData(circulo);
                    proyectil.setSprite(sprite);
                    
                    proyectilesJugador.add(proyectil);
                }
            } catch (Exception e) {
                System.err.println("Error cargando imagen de bala: " + e.getMessage());
            }
        });
        
        System.out.println("🔫 Proyectil del jugador creado");
    }

    // ========================================================================
    // NUEVO: INICIALIZACIÓN DEL HILO DE RECURSOS Y ENEMIGOS
    // ========================================================================
    private void iniciarHiloDeSpawning() {
        if (gamePane == null) {
            System.err.println("❌ ERROR: gamePane es null. Asegúrate de que exista en tu FXML como <Pane fx:id=\"gamePane\">");
            return;
        }

        System.out.println("✅ Iniciando ResourceThread...");

        // Crear el hilo
        resourceThread = new ResourceThread(jugador, gamePane, escala);

        // Pasar información del mapa al hilo
        resourceThread.setMapData(mapData, rows, cols);

        // Actualizar contadores iniciales
        resourceThread.setRecursosActivos(recursosActivos.size());
        resourceThread.setEnemigosActivos(enemigosActivos.size());

        // Configurar listener para actualizar las listas locales
        resourceThread.setSpawnListener(new SpawnListener() {
            @Override
            public void onEnemigoGenerado(Enemigo e) {
                Platform.runLater(() -> {
                    if (!enemigosActivos.contains(e)) {
                        enemigosActivos.add(e);
                        resourceThread.setEnemigosActivos(enemigosActivos.size());
                        System.out.println("✅ Enemigo agregado a la lista: " + e.getId() + " [" + enemigosActivos.size() + "/4]");
                    }
                });
            }

            @Override
            public void onRecursoGenerado(Recurso r) {
                Platform.runLater(() -> {
                    if (!recursosActivos.contains(r)) {
                        recursosActivos.add(r);
                        resourceThread.setRecursosActivos(recursosActivos.size());
                        System.out.println("✅ Recurso agregado a la lista: " + r.getTipo() + " [" + recursosActivos.size() + "/3]");
                    }
                });
            }
            
            @Override
            public void onRecursoRecogido(Recurso r) {
                Platform.runLater(() -> {
                    // Remover de la lista de recursos activos
                    recursosActivos.remove(r);
                    resourceThread.setRecursosActivos(recursosActivos.size());
                    
                    // Actualizar HUD para mostrar el nuevo inventario
                    updateHUD();
                    
                    // Mostrar mensaje de éxito
                    mostrarMensajeAccion("✅ Recogido: " + r.getTipo() + " x" + r.getCantidad());
                    
                    System.out.println("📦 Recurso recogido y HUD actualizado | Recursos activos: " + recursosActivos.size() + "/3");
                });
            }
        });

        // Iniciar el hilo
        resourceThread.start();
    }
    
    // ========================================================================
    // HILO DE IA DE ENEMIGOS (PERSECUCIÓN Y ATAQUE)
    // ========================================================================
    private void iniciarHiloDeEnemigoAI() {
        enemigoAIThread = new Thread(() -> {
            System.out.println("✅ Hilo de IA de enemigos iniciado");
            
            while (juegoActivo && !Thread.currentThread().isInterrupted()) {
                try {
                    Platform.runLater(this::actualizarEnemigos);
                    Thread.sleep(500); // Actualizar cada 0.5 segundos
                } catch (InterruptedException e) {
                    System.out.println(">>> Hilo de IA de enemigos interrumpido");
                    break;
                }
            }
            
            System.out.println(">>> Hilo de IA de enemigos finalizado");
        });
        
        enemigoAIThread.setDaemon(true);
        enemigoAIThread.start();
    }
    
    private void actualizarEnemigos() {
        if (enemigosActivos.isEmpty()) return;
        
        List<Enemigo> enemigosAEliminar = new ArrayList<>();
        
        for (Enemigo enemigo : enemigosActivos) {
            if (enemigo.getSalud() <= 0) {
                enemigosAEliminar.add(enemigo);
                continue;
            }
            
            // Obtener posición del enemigo y jugador
            int enemigoX = enemigo.getPosicionX();
            int enemigoY = enemigo.getPosicionY();
            
            // Calcular distancia al jugador
            int distanciaX = playerCol - enemigoX;
            int distanciaY = playerRow - enemigoY;
            double distancia = Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);
            
            // Si está cerca del jugador (radio de 8 tiles), perseguir
            if (distancia < 8 && distancia > 1) {
                // Mover hacia el jugador
                int nuevoX = enemigoX;
                int nuevoY = enemigoY;
                
                if (Math.abs(distanciaX) > Math.abs(distanciaY)) {
                    nuevoX += (distanciaX > 0) ? 1 : -1;
                } else {
                    nuevoY += (distanciaY > 0) ? 1 : -1;
                }
                
                // Verificar que la nueva posición sea válida
                if (isInsideMap(nuevoY, nuevoX) && esCaminable(mapData[nuevoY][nuevoX])) {
                    enemigo.setPosicionX(nuevoX);
                    enemigo.setPosicionY(nuevoY);
                    
                    // Actualizar sprite
                    Object userData = enemigo.getUserData();
                    if (userData instanceof ImageView) {
                        ImageView sprite = (ImageView) userData;
                        sprite.setLayoutX(nuevoX * escala);
                        sprite.setLayoutY(nuevoY * escala);
                    }
                }
            }
            
            // Si está en rango de ataque (distancia 2-6 tiles), disparar proyectil
            if (distancia >= 2 && distancia <= 6) {
                // Disparar cada 2 segundos aproximadamente (cada 4 actualizaciones)
                if (Math.random() < 0.25) {
                    dispararProyectil(enemigo);
                }
            }
        }
        
        // Eliminar enemigos muertos
        for (Enemigo e : enemigosAEliminar) {
            Object userData = e.getUserData();
            if (userData instanceof ImageView) {
                gamePane.getChildren().remove(userData);
            }
            enemigosActivos.remove(e);
        }
        
        if (!enemigosAEliminar.isEmpty() && resourceThread != null) {
            resourceThread.setEnemigosActivos(enemigosActivos.size());
        }
    }
    
    private void dispararProyectil(Enemigo enemigo) {
        // Calcular posición inicial del proyectil (centro del enemigo)
        double enemigoX = enemigo.getPosicionX() * escala + 20;
        double enemigoY = enemigo.getPosicionY() * escala + 20;
        
        // Crear proyectil que persigue
        Proyectil proyectil = new Proyectil(enemigoX, enemigoY, 0, 0, enemigo.getDano());
        proyectil.setPersigue(true);
        
        // Crear sprite del proyectil (círculo rojo brillante y grande)
        Platform.runLater(() -> {
            // Crear un círculo visual MÁS GRANDE y BRILLANTE
            javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(8); // Radio más grande
            circulo.setFill(javafx.scene.paint.Color.rgb(255, 50, 50, 0.9)); // Rojo brillante
            circulo.setStroke(javafx.scene.paint.Color.DARKRED);
            circulo.setStrokeWidth(3);
            
            // Agregar efecto de brillo
            javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
            circulo.setEffect(glow);
            
            // Usar setCenterX/Y para círculos en lugar de setLayoutX/Y
            circulo.setCenterX(enemigoX);
            circulo.setCenterY(enemigoY);
            
            gamePane.getChildren().add(circulo);
            
            // Guardar referencia
            ImageView sprite = new ImageView();
            sprite.setUserData(circulo);
            proyectil.setSprite(sprite);
            
            proyectilesEnemigos.add(proyectil);
        });
        
        System.out.println("🔴 Enemigo " + enemigo.getId() + " disparó proyectil perseguidor");
    }
    
    // ========================================================================
    // HILO DE PROYECTILES
    // ========================================================================
    private void iniciarHiloDeProyectiles() {
        proyectilesThread = new Thread(() -> {
            System.out.println("✅ Hilo de proyectiles iniciado");
            
            while (juegoActivo && !Thread.currentThread().isInterrupted()) {
                try {
                    Platform.runLater(this::actualizarProyectiles);
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    System.out.println(">>> Hilo de proyectiles interrumpido");
                    break;
                }
            }
            
            System.out.println(">>> Hilo de proyectiles finalizado");
        });
        
        proyectilesThread.setDaemon(true);
        proyectilesThread.start();
    }

    private void actualizarProyectilesEnemigos() {
        List<Proyectil> proyectilesAEliminar = new ArrayList<>();
        
        // Calcular posición del jugador (centro)
        double jugadorCentroX = playerCol * escala + 32;
        double jugadorCentroY = playerRow * escala + 32;
        
        for (Proyectil proyectil : proyectilesEnemigos) {
            if (!proyectil.isActivo()) {
                proyectilesAEliminar.add(proyectil);
                continue;
            }
            
            // Si el proyectil persigue, actualizar dirección hacia el jugador
            if (proyectil.isPersigue()) {
                proyectil.actualizarPersiguiendo(jugadorCentroX, jugadorCentroY);
            } else {
                proyectil.actualizar();
            }
            
            // Actualizar sprite visual
            Object userData = proyectil.getSprite().getUserData();
            if (userData instanceof javafx.scene.shape.Circle) {
                javafx.scene.shape.Circle circulo = (javafx.scene.shape.Circle) userData;
                circulo.setCenterX(proyectil.getX());
                circulo.setCenterY(proyectil.getY());
            }
            
            // Verificar colisión con jugador (hitbox más preciso)
            double jugadorX = playerCol * escala + 16; // Ajustar al centro
            double jugadorY = playerRow * escala + 16;
            double jugadorRadius = 24; // Radio de colisión del jugador
            
            double distanciaAlJugador = Math.sqrt(
                Math.pow(proyectil.getX() - jugadorCentroX, 2) + 
                Math.pow(proyectil.getY() - jugadorCentroY, 2)
            );
            
            if (distanciaAlJugador <= jugadorRadius) {
                // ¡IMPACTO!
                crearEfectoImpacto(proyectil.getX(), proyectil.getY());
                
                int dano = proyectil.getDano();
                int saludActual = jugador.getSalud();
                int nuevaSalud = Math.max(0, saludActual - dano);
                
                jugador.setSalud(nuevaSalud);
                
                System.out.println("💥 ¡PROYECTIL IMPACTÓ! Daño: " + dano + 
                                 " | Salud restante: " + nuevaSalud);
                
                mostrarMensajeAccion("💥 ¡IMPACTO! -" + dano + " HP");
                updateHUD();
                
                proyectil.setActivo(false);
                proyectilesAEliminar.add(proyectil);
                
                // Si el jugador murió
                if (nuevaSalud <= 0) {
                    juegoActivo = false;
                    if (resourceThread != null) resourceThread.interrupt();
                    if (enemigoAIThread != null) enemigoAIThread.interrupt();
                    if (proyectilesThread != null) proyectilesThread.interrupt();
                    
                    Platform.runLater(() -> {
                        mostrarMensajeAccion("💀 ¡HAS MUERTO!");
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.runLater(() -> OregonApplication.cambiarVista("gameover-view.fxml"));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                }
            }
            
            // Verificar si salió del mapa
            if (proyectil.getX() < -50 || proyectil.getX() > cols * escala + 50 ||
                proyectil.getY() < -50 || proyectil.getY() > rows * escala + 50) {
                proyectil.setActivo(false);
                proyectilesAEliminar.add(proyectil);
            }
        }
        
        // Eliminar proyectiles inactivos
        for (Proyectil p : proyectilesAEliminar) {
            Object userData = p.getSprite().getUserData();
            if (userData instanceof javafx.scene.shape.Circle) {
                gamePane.getChildren().remove(userData);
            }
            proyectilesEnemigos.remove(p);
        }
    }
    
    private void actualizarProyectilesJugador() {
        List<Proyectil> proyectilesAEliminar = new ArrayList<>();
        
        for (Proyectil proyectil : proyectilesJugador) {
            if (!proyectil.isActivo()) {
                proyectilesAEliminar.add(proyectil);
                continue;
            }
            
            // Actualizar posición (línea recta)
            proyectil.actualizar();
            
            // Actualizar sprite visual
            ImageView sprite = proyectil.getSprite();
            if (sprite != null) {
                Object userData = sprite.getUserData();
                if (userData instanceof javafx.scene.shape.Circle) {
                    // Es un círculo
                    javafx.scene.shape.Circle circulo = (javafx.scene.shape.Circle) userData;
                    circulo.setLayoutX(proyectil.getX());
                    circulo.setLayoutY(proyectil.getY());
                } else {
                    // Es una imagen
                    sprite.setLayoutX(proyectil.getX() - 10);
                    sprite.setLayoutY(proyectil.getY() - 10);
                }
            }
            
            // Verificar colisión con enemigos
            boolean impacto = false;
            for (Enemigo enemigo : new ArrayList<>(enemigosActivos)) {
                Object userData = enemigo.getUserData();
                if (userData instanceof ImageView) {
                    ImageView enemigoSprite = (ImageView) userData;
                    double enemigoX = enemigoSprite.getLayoutX();
                    double enemigoY = enemigoSprite.getLayoutY();
                    double enemigoWidth = enemigoSprite.getFitWidth();
                    double enemigoHeight = enemigoSprite.getFitHeight();
                    
                    // Verificar colisión
                    if (proyectil.getX() >= enemigoX && proyectil.getX() <= enemigoX + enemigoWidth &&
                        proyectil.getY() >= enemigoY && proyectil.getY() <= enemigoY + enemigoHeight) {
                        
                        // ¡IMPACTO!
                        crearEfectoImpacto(proyectil.getX(), proyectil.getY());
                        
                        int dano = proyectil.getDano();
                        int saludActual = enemigo.getSalud();
                        int nuevaSalud = Math.max(0, saludActual - dano);
                        
                        enemigo.setSalud(nuevaSalud);
                        
                        System.out.println("🎯 ¡IMPACTO EN ENEMIGO! Daño: " + dano + 
                                         " | Salud restante: " + nuevaSalud);
                        
                        if (nuevaSalud <= 0) {
                            // Enemigo eliminado - DAR RECOMPENSA
                            int recompensa = 2;
                            jugador.setDinero(jugador.getDinero() + recompensa);
                            
                            Platform.runLater(() -> gamePane.getChildren().remove(enemigoSprite));
                            enemigosActivos.remove(enemigo);
                            mostrarMensajeAccion("💀 Enemigo eliminado! +$" + recompensa);
                            System.out.println("💀 Enemigo eliminado | Recompensa: $" + recompensa);
                            
                            updateHUD();
                            
                            if (resourceThread != null) {
                                resourceThread.setEnemigosActivos(enemigosActivos.size());
                            }
                        } else {
                            mostrarMensajeAccion("🎯 Impacto! -" + dano + " HP");
                        }
                        
                        proyectil.setActivo(false);
                        proyectilesAEliminar.add(proyectil);
                        impacto = true;
                        break;
                    }
                }
            }
            
            // Verificar si salió del mapa
            if (!impacto && (proyectil.getX() < -50 || proyectil.getX() > cols * escala + 50 ||
                proyectil.getY() < -50 || proyectil.getY() > rows * escala + 50)) {
                proyectil.setActivo(false);
                proyectilesAEliminar.add(proyectil);
            }
        }
        
        // Eliminar proyectiles inactivos
        for (Proyectil p : proyectilesAEliminar) {
            ImageView sprite = p.getSprite();
            if (sprite != null) {
                Object userData = sprite.getUserData();
                if (userData instanceof javafx.scene.shape.Circle) {
                    gamePane.getChildren().remove(userData);
                } else {
                    gamePane.getChildren().remove(sprite);
                }
            }
            proyectilesJugador.remove(p);
        }
    }
    
    /**
     * Crea un efecto visual de explosión cuando un proyectil impacta
     */
    private void crearEfectoImpacto(double x, double y) {
        Platform.runLater(() -> {
            // Crear círculo de explosión
            javafx.scene.shape.Circle explosion = new javafx.scene.shape.Circle(15);
            explosion.setFill(javafx.scene.paint.Color.rgb(255, 100, 0, 0.7)); // Naranja
            explosion.setStroke(javafx.scene.paint.Color.YELLOW);
            explosion.setStrokeWidth(3);
            explosion.setLayoutX(x);
            explosion.setLayoutY(y);
            
            // Efecto de brillo
            javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(1.0);
            explosion.setEffect(glow);
            
            gamePane.getChildren().add(explosion);
            
            // Animar y eliminar después de 0.3 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    Platform.runLater(() -> gamePane.getChildren().remove(explosion));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    // ========================================================================
    // HUD Y ACTUALIZACIÓN DE SUMINISTROS
    // ========================================================================
    private void updateHUD() {
        if (jugador == null) return;

        Inventario inv = jugador.getInventario();
        
        // Actualizar suministros en tiempo real
        int comida = (inv != null) ? inv.obtenerCantidad(TIPORECURSO.COMIDA) : 0;
        int municion = (inv != null) ? inv.obtenerCantidad(TIPORECURSO.MUNICION) : 0;
        int medicina = (inv != null) ? inv.obtenerCantidad(TIPORECURSO.MEDICINA) : 0;
        
        if (comidaLabel != null) comidaLabel.setText("🍖 Comida: " + comida + " (E)");
        if (municionInventarioLabel != null) municionInventarioLabel.setText("🔫 Munición: " + municion);
        if (medicinaLabel != null) medicinaLabel.setText("💊 Medicina: " + medicina + " (M)");
        
        // Actualizar vida (0-100)
        int vidaActual = calcularVidaPorcentaje();
        if (vidaLabel != null) {
            vidaLabel.setText("❤️ VIDA: " + vidaActual + "/100");
            
            // Cambiar color según la vida
            if (vidaActual <= 25) {
                vidaLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else if (vidaActual <= 50) {
                vidaLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                vidaLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 18px; -fx-font-weight: bold;");
            }
        }
        
        // Actualizar inventario y dinero
        int objetos = (inv != null) ? inv.getRecursos().size() : 0;
        int cap = (inv != null) ? inv.getCapacidadMaxima() : 0;
        
        if (inventarioLabel != null) inventarioLabel.setText("Inventario: " + objetos + "/" + cap);
        if (dineroLabel != null) dineroLabel.setText("Dinero: $" + jugador.getDinero());

        // Verificar logros
        verificarYMarcarLogros();

        // Actualizar barra de salud
        int salud = jugador.getSalud();
        String fileName = switch (salud) {
            case 0 -> "Barra0Vidas.png";
            case 1 -> "Barra1vidas.png";
            case 2 -> "Barra2vidas.png";
            default -> "Barra3vidas.png";
        };

        String ruta = "/images/player/generalJugador/" + fileName;
        InputStream is = GameViewController.class.getResourceAsStream(ruta);

        if (is != null && healthBarImage != null) {
            healthBarImage.setImage(new Image(is));
        }
    }
    
    /**
     * Calcula el porcentaje de vida del jugador (0-100)
     * La salud ya está en escala 0-100
     */
    private int calcularVidaPorcentaje() {
        int salud = jugador.getSalud();
        return Math.max(0, Math.min(100, salud)); // La salud ya está en escala 0-100
    }



    private void initPlayerSprite() {
        if (jugador == null) {
            System.err.println("initPlayerSprite(): jugador es null");
            return;
        }

        // Tamaño del sprite
        playerImage.setFitWidth(64);
        playerImage.setFitHeight(64);
        playerImage.setPreserveRatio(true);

        // Posición inicial
        playerImage.setTranslateX(playerCol * TILE_SIZE);
        playerImage.setTranslateY(playerRow * TILE_SIZE);

        // Cargar sprite inicial (mirando hacia abajo)
        actualizarSpriteJugador("down");
        
        System.out.println("Sprite del jugador inicializado");
    }
    
    /**
     * Actualiza el sprite del jugador según la dirección de movimiento
     */
    private void actualizarSpriteJugador(String direccion) {
        if (jugador == null) return;
        
        // Actualizar dirección actual
        direccionActual = direccion;
        
        // Alternar frame de animación si ha pasado suficiente tiempo
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoCambioFrame > FRAME_DELAY) {
            frameAnimacion = (frameAnimacion == 1) ? 2 : 1;
            ultimoCambioFrame = tiempoActual;
        }
        
        // Construir ruta del sprite
        enums.PROFESION profesion = jugador.getProfesion();
        String carpetaProfesion;
        String nombreProfesion;
        
        if (profesion == enums.PROFESION.BANQUERO) {
            carpetaProfesion = "banquero";
            nombreProfesion = "banquero";
        } else if (profesion == enums.PROFESION.CARPINTERO) {
            carpetaProfesion = "carpintero";
            nombreProfesion = "carpintero";
        } else if (profesion == enums.PROFESION.GRANJERO) {
            carpetaProfesion = "granjero";
            nombreProfesion = "granjero";
        } else {
            return;
        }
        
        // Corregir "rigth" a "right" si es necesario
        String direccionArchivo = direccion.equals("right") ? "rigth" : direccion;
        
        String rutaSprite = String.format("/images/player/%s/%s_%s%d.png", 
            carpetaProfesion, nombreProfesion, direccionArchivo, frameAnimacion);
        
        try {
            InputStream is = getClass().getResourceAsStream(rutaSprite);
            if (is != null) {
                Image img = new Image(is);
                playerImage.setImage(img);
            } else {
                System.err.println("⚠️ No se encontró sprite: " + rutaSprite);
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando sprite: " + e.getMessage());
        }
    }


    // ========================================================================
    // MOVIMIENTO
    // ========================================================================
    private void setupKeyboard() {
        mapCanvas.setFocusTraversable(true);

        mapCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Manejar teclas presionadas
                newScene.setOnKeyPressed(e -> {
                    // Agregar tecla al conjunto de teclas presionadas
                    teclasPresionadas.add(e.getCode());
                    
                    // Acciones instantáneas (no de movimiento)
                    switch (e.getCode()) {
                        case Q -> cambiarArma(null);
                        case R -> recargarArma(null);
                        case E -> comerComida();
                        case M -> usarMedicina();
                    }
                });
                
                // Manejar teclas soltadas
                newScene.setOnKeyReleased(e -> {
                    teclasPresionadas.remove(e.getCode());
                });
                
                Platform.runLater(() -> mapCanvas.requestFocus());
            }
        });

        // Iniciar hilo de movimiento continuo
        iniciarHiloMovimiento();
        
        Platform.runLater(() -> mapCanvas.requestFocus());
    }
    
    /**
     * Hilo que procesa el movimiento continuo mientras se mantienen las teclas presionadas
     */
    private void iniciarHiloMovimiento() {
        Thread movimientoThread = new Thread(() -> {
            System.out.println("✅ Hilo de movimiento continuo iniciado");
            
            while (juegoActivo && !Thread.currentThread().isInterrupted()) {
                try {
                    // Verificar si hay teclas de movimiento presionadas
                    long tiempoActual = System.currentTimeMillis();
                    
                    if (tiempoActual - ultimoMovimiento >= MOVIMIENTO_DELAY) {
                        boolean seMovio = false;
                        
                        // Prioridad: última tecla presionada
                        if (teclasPresionadas.contains(javafx.scene.input.KeyCode.UP) || 
                            teclasPresionadas.contains(javafx.scene.input.KeyCode.W)) {
                            Platform.runLater(() -> movePlayer(-1, 0));
                            seMovio = true;
                        } else if (teclasPresionadas.contains(javafx.scene.input.KeyCode.DOWN) || 
                                   teclasPresionadas.contains(javafx.scene.input.KeyCode.S)) {
                            Platform.runLater(() -> movePlayer(1, 0));
                            seMovio = true;
                        } else if (teclasPresionadas.contains(javafx.scene.input.KeyCode.LEFT) || 
                                   teclasPresionadas.contains(javafx.scene.input.KeyCode.A)) {
                            Platform.runLater(() -> movePlayer(0, -1));
                            seMovio = true;
                        } else if (teclasPresionadas.contains(javafx.scene.input.KeyCode.RIGHT) || 
                                   teclasPresionadas.contains(javafx.scene.input.KeyCode.D)) {
                            Platform.runLater(() -> movePlayer(0, 1));
                            seMovio = true;
                        }
                        
                        if (seMovio) {
                            ultimoMovimiento = tiempoActual;
                        }
                    }
                    
                    Thread.sleep(50); // Verificar cada 50ms
                    
                } catch (InterruptedException e) {
                    System.out.println(">>> Hilo de movimiento interrumpido");
                    break;
                }
            }
            
            System.out.println(">>> Hilo de movimiento finalizado");
        });
        
        movimientoThread.setDaemon(true);
        movimientoThread.start();
    }

    private boolean isInsideMap(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private boolean esCaminable(int tileCode) {
        // ✅ Tiles CAMINABLES: pasto (0), arena (2), código 4, PORTAL (5)
        // ❌ Tiles NO CAMINABLES: árboles (1), agua (3), rocas (6)
        
        // Primero verificar obstáculos (NO caminables)
        if (tileCode == 1 || tileCode == 3 || tileCode == 6) {
            return false; // Árboles, agua, rocas
        }
        
        // ✅ El código 5 (PORTAL) SÍ es caminable para que el jugador pueda entrar
        if (tileCode == 5) {
            return true;
        }
        
        char ch = (char) tileCode;
        
        // Obstáculos por letra
        if (ch == 'T' || ch == 'W' || ch == 'B') {
            return false; // Tree, Water, Bloqueado
        }
        
        // ✅ Portal por letra también es caminable
        if (ch == 'P') {
            return true;
        }
        
        // Tiles caminables por código numérico
        if (tileCode == 0 || tileCode == 2 || tileCode == 4) {
            return true; // Pasto, Arena, Código 4 (decorativo)
        }
        
        // Tiles caminables por letra
        if (ch == 'G' || ch == 'S' || ch == 'I' || ch == 'E') {
            return true; // Grass, Sand, Inicio, Enemigo
        }
        
        // Por defecto, NO caminable
        System.out.println("⚠️ Tile desconocido NO caminable: código=" + tileCode + ", char='" + ch + "'");
        return false;
    }

    private boolean esPortal(int tileCode) {
        // ⚠️ SOLO el código 5 es portal de teletransportación
        boolean esPortal = (tileCode == 5);
        
        if (esPortal) {
            System.out.println("✅ PORTAL CONFIRMADO: código=" + tileCode);
        }
        
        return esPortal;
    }

    private void verificarPortalCercano() {
        int radio = 1; // Radio de 1 tile alrededor del jugador
        
        // Revisar en un cuadrado alrededor del jugador
        for (int r = playerRow - radio; r <= playerRow + radio; r++) {
            for (int c = playerCol - radio; c <= playerCol + radio; c++) {
                // Saltar la posición actual del jugador
                if (r == playerRow && c == playerCol) continue;
                
                // Verificar si la posición está dentro del mapa
                if (isInsideMap(r, c)) {
                    int tileCode = mapData[r][c];
                    if (esPortal(tileCode)) {
                        System.out.println("🚪 PORTAL CERCANO DETECTADO! Posición: (" + r + "," + c + ") | Código: " + tileCode);
                        manejarTransicionEscenario();
                        return;
                    }
                }
            }
        }
    }

    private void movePlayer(int dRow, int dCol) {
        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;

        if (!isInsideMap(newRow, newCol)) {
            return;
        }

        int tileCode = mapData[newRow][newCol];

        // DEBUG: Imprimir información del tile
        System.out.println("DEBUG - Posición: (" + newRow + "," + newCol + ") | Tile: " + tileCode + " | Char: '" + (char)tileCode + "' | Escenario: " + escenarioActual + " | esPortal=" + esPortal(tileCode) + " | esCaminable=" + esCaminable(tileCode));

        // ⚠️ VERIFICAR PORTAL PRIMERO (antes de verificar si es caminable)
        if (esPortal(tileCode)) {
            System.out.println("🚪 PORTAL DETECTADO! Activando teletransporte...");
            // Mover al jugador al portal primero
            playerRow = newRow;
            playerCol = newCol;
            updatePlayerImagePosition();
            // Activar teletransporte
            manejarTransicionEscenario();
            return;
        }

        if (!esCaminable(tileCode)) {
            System.out.println("DEBUG - Tile NO caminable (código: " + tileCode + ")");
            return;
        }

        // Determinar dirección del movimiento y actualizar sprite
        String direccion;
        if (dRow < 0) {
            direccion = "up";
        } else if (dRow > 0) {
            direccion = "down";
        } else if (dCol < 0) {
            direccion = "left";
        } else if (dCol > 0) {
            direccion = "right";
        } else {
            direccion = direccionActual; // Mantener dirección actual si no hay movimiento
        }
        
        // Actualizar sprite del jugador según la dirección
        actualizarSpriteJugador(direccion);

        // Mover al jugador
        playerRow = newRow;
        playerCol = newCol;
        updatePlayerImagePosition();

        // Verificar colisiones con recursos
        verificarRecoleccion();
        
        // ⚠️ NUEVO: Verificar si el jugador está sobre un portal DESPUÉS de moverse
        verificarPortalEnPosicionActual();
    }

    private void manejarTransicionEscenario() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("🚪 PORTAL ACTIVADO!");
        System.out.println("📍 Escenario actual: " + escenarioActual);
        System.out.println("═══════════════════════════════════════");
        
        // Si estamos en el último escenario (Río = 3), mostrar victoria
        if (escenarioActual >= 3) {
            System.out.println("🎉 ¡Has llegado a Oregón! VICTORIA");
            juegoActivo = false;
            if (resourceThread != null) resourceThread.interrupt();
            if (enemigoAIThread != null) enemigoAIThread.interrupt();
            if (proyectilesThread != null) proyectilesThread.interrupt();
            
            Platform.runLater(() -> {
                mostrarMensajeAccion("🎉 ¡LLEGASTE A OREGÓN!");
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        System.out.println("🏆 Cambiando a pantalla de victoria...");
                        OregonApplication.cambiarVista("victoria-view.fxml");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } else {
            // Avanzar al siguiente escenario
            int siguienteEscenario = escenarioActual + 1;
            System.out.println("🗺️ TELETRANSPORTANDO: Escenario " + escenarioActual + " → Escenario " + siguienteEscenario);
            
            Platform.runLater(() -> {
                mostrarMensajeAccion("🚪 Teletransportando...");
                try {
                    Thread.sleep(500);
                    Platform.runLater(() -> {
                        System.out.println("🔄 Cargando escenario " + siguienteEscenario + "...");
                        cargarEscenario(siguienteEscenario);
                        System.out.println("✅ Escenario " + siguienteEscenario + " cargado!");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Verifica si el jugador está actualmente sobre un portal y activa la teletransportación
     */
    private void verificarPortalEnPosicionActual() {
        // Verificar si la posición actual del jugador es un portal
        if (isInsideMap(playerRow, playerCol)) {
            int tileCode = mapData[playerRow][playerCol];
            
            if (esPortal(tileCode)) {
                System.out.println("🚪 JUGADOR SOBRE PORTAL! Posición: (" + playerRow + "," + playerCol + ") | Tile: " + tileCode);
                manejarTransicionEscenario();
            }
        }
    }

    // ========================================================================
    // RECOLECCIÓN
    // ========================================================================
    private void verificarRecoleccion() {
        List<Recurso> porEliminar = new ArrayList<>();

        for (Recurso r : recursosActivos) {
            // Conversión: posición del jugador en tiles → coordenadas lógicas
            int jugadorX = playerCol;
            int jugadorY = playerRow;

            if (jugadorX == r.getX() && jugadorY == r.getY()) {
                jugador.agregarRecurso(r);
                porEliminar.add(r);

                // Remover sprite del gamePane
                Platform.runLater(() -> {
                    Object userData = r.getSprite();
                    if (userData instanceof ImageView) {
                        gamePane.getChildren().remove(userData);
                    }
                });

                System.out.println("✅ Recurso recolectado: " + r.getTipo());
            }
        }

        recursosActivos.removeAll(porEliminar);

        // Actualizar contador en el hilo
        if (!porEliminar.isEmpty() && resourceThread != null) {
            resourceThread.setRecursosActivos(recursosActivos.size());
            System.out.println("📦 Recursos activos: " + recursosActivos.size() + "/3");
            updateHUD();
        }
    }

    // ========================================================================
    // BOTONES INFERIORES
    // ========================================================================
    @FXML
    private void recogerObjeto(ActionEvent e) {
        if (recursosActivos.isEmpty()) {
            System.out.println("📭 No hay recursos disponibles para recoger");
            mostrarMensajeAccion("📭 No hay recursos disponibles");
            return;
        }
        
        List<Recurso> recursosRecogidos = new ArrayList<>();
        int radio = 2; // Radio de búsqueda (2 tiles alrededor del jugador)
        
        System.out.println("🔍 Buscando recursos cerca del jugador en posición (" + playerRow + ", " + playerCol + ")");
        
        // Buscar recursos en un radio cercano al jugador
        for (Recurso r : recursosActivos) {
            int distanciaX = Math.abs(r.getX() - playerCol);
            int distanciaY = Math.abs(r.getY() - playerRow);
            
            // Si el recurso está dentro del radio
            if (distanciaX <= radio && distanciaY <= radio) {
                // Intentar agregar al inventario
                boolean agregado = jugador.agregarRecurso(r);
                
                if (agregado) {
                    recursosRecogidos.add(r);
                    
                    // Remover sprite del gamePane
                    Platform.runLater(() -> {
                        Object userData = r.getSprite();
                        if (userData instanceof ImageView) {
                            gamePane.getChildren().remove(userData);
                        }
                    });
                    
                    System.out.println("✅ Recurso recogido: " + r.getTipo() + " x" + r.getCantidad() + 
                                     " en posición (" + r.getY() + ", " + r.getX() + ")");
                } else {
                    System.out.println("⚠️ Inventario lleno! No se pudo recoger: " + r.getTipo());
                    mostrarMensajeAccion("⚠️ Inventario lleno!");
                }
            }
        }
        
        // Remover recursos recogidos de la lista
        recursosActivos.removeAll(recursosRecogidos);
        
        // Actualizar contador en el hilo
        if (!recursosRecogidos.isEmpty() && resourceThread != null) {
            resourceThread.setRecursosActivos(recursosActivos.size());
            System.out.println("📦 Recursos recogidos: " + recursosRecogidos.size() + " | Recursos activos: " + recursosActivos.size() + "/3");
            
            // Mostrar mensaje de éxito
            String mensaje = "✅ Recogido: " + recursosRecogidos.size() + " recurso" + 
                           (recursosRecogidos.size() > 1 ? "s" : "");
            mostrarMensajeAccion(mensaje);
            
            updateHUD();
        } else if (recursosRecogidos.isEmpty()) {
            System.out.println("❌ No hay recursos cerca del jugador (radio: " + radio + " tiles)");
            mostrarMensajeAccion("❌ No hay recursos cerca");
        }
    }
    
    /**
     * Muestra un mensaje temporal en pantalla
     */
    private void mostrarMensajeAccion(String mensaje) {
        if (mensajeAccionLabel == null) return;
        
        Platform.runLater(() -> {
            mensajeAccionLabel.setText(mensaje);
            mensajeAccionLabel.setVisible(true);
            
            // Ocultar después de 2 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> mensajeAccionLabel.setVisible(false));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
    }
    
    // ========================================================================
    // SISTEMA DE LOGROS
    // ========================================================================
    private void verificarYMarcarLogros() {
        ArbolLogros arbolLogros = OregonApplication.getArbolLogros();
        if (arbolLogros == null || jugador == null) return;
        
        // Logro 1: "Primer paso" - Se marca en PrepararViajeController al iniciar viaje
        
        // Logro 2: "Cazador novato" - Derrotaste tu primer autómata
        // (Se marca cuando se mata el primer enemigo - ver actualizarProyectilesJugador)
        
        // Logro 3: "Superviviente" - Sobreviviste a las montañas rocosas
        if (escenarioActual >= 2) {
            marcarLogro(arbolLogros, 3, "🏆 Logro desbloqueado: Superviviente - Sobreviviste a las montañas rocosas!");
        }
        
        // Logro 4: "Leyenda de Oregón" - Completaste el viaje hasta el Valle de Willamette
        if (escenarioActual >= 3) {
            marcarLogro(arbolLogros, 4, "🏆 Logro desbloqueado: Leyenda de Oregón - Completaste el viaje!");
        }
        
        // ⚠️ VERIFICAR SI TODOS LOS LOGROS ESTÁN COMPLETOS
        verificarVictoriaPorLogros(arbolLogros);
    }
    
    /**
     * Verifica si todos los logros están completos y muestra la pantalla de victoria
     */
    private void verificarVictoriaPorLogros(ArbolLogros arbolLogros) {
        if (arbolLogros == null) return;
        
        // Obtener todos los logros
        java.util.List<Logro> todosLosLogros = arbolLogros.obtenerLogrosEnOrden();
        
        if (todosLosLogros.isEmpty()) return;
        
        // Verificar si TODOS están obtenidos
        boolean todosCompletos = true;
        int logrosObtenidos = 0;
        
        for (Logro logro : todosLosLogros) {
            if (logro.isObtenido()) {
                logrosObtenidos++;
            } else {
                todosCompletos = false;
            }
        }
        
        System.out.println("📊 Progreso de logros: " + logrosObtenidos + "/" + todosLosLogros.size());
        
        // Si todos los logros están completos, mostrar victoria
        if (todosCompletos && todosLosLogros.size() > 0) {
            System.out.println("🎉 ¡TODOS LOS LOGROS COMPLETADOS! Mostrando pantalla de victoria...");
            
            // Detener el juego
            juegoActivo = false;
            if (resourceThread != null) resourceThread.interrupt();
            if (enemigoAIThread != null) enemigoAIThread.interrupt();
            if (proyectilesThread != null) proyectilesThread.interrupt();
            
            // Mostrar mensaje y cambiar a victoria
            Platform.runLater(() -> {
                mostrarMensajeAccion("🏆 ¡TODOS LOS LOGROS COMPLETADOS!");
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> OregonApplication.cambiarVista("victoria-view.fxml"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    private void marcarLogro(ArbolLogros arbolLogros, int idLogro, String mensaje) {
        Logro logro = arbolLogros.buscarLogroPorId(idLogro);
        if (logro != null && !logro.isObtenido()) {
            arbolLogros.marcarLogroComoObtenido(idLogro);
            mostrarMensajeAccion(mensaje);
            System.out.println("✅ " + mensaje);
            
            // Verificar victoria después de marcar un logro
            verificarVictoriaPorLogros(arbolLogros);
        }
    }

    @FXML
    private void cambiarArma(ActionEvent e) {
        if (rifle == null || revolver == null) {
            System.err.println("❌ ERROR: Armas no inicializadas");
            return;
        }
        
        // Alternar entre armas
        indiceArmaActual = (indiceArmaActual + 1) % 2;
        armaActual = (indiceArmaActual == 0) ? rifle : revolver;
        
        String nombreArma = armaActual.getArma() == NOMBRE_ARMA.RIFLE_AVANCARGA 
            ? "RIFLE AVANCARGA" : "REVOLVER";
        
        mostrarMensajeAccion("🔫 Cambiado a: " + nombreArma);
        System.out.println("🔫 Arma cambiada a: " + nombreArma);
        
        actualizarPanelArmas();
        actualizarImagenArmaEnMano();
    }
    
    /**
     * Actualiza la imagen del arma en mano cuando se cambia de arma
     */
    private void actualizarImagenArmaEnMano() {
        if (armaEnManoImage == null || armaActual == null) return;
        
        String rutaArma = armaActual.getArma() == NOMBRE_ARMA.RIFLE_AVANCARGA 
            ? "/images/proyectiles/rifle.png" 
            : "/images/proyectiles/revolver.png";
        
        try {
            InputStream is = getClass().getResourceAsStream(rutaArma);
            if (is != null) {
                armaEnManoImage.setImage(new Image(is));
                System.out.println("✅ Imagen del arma en mano actualizada");
            }
        } catch (Exception e) {
            System.err.println("❌ Error actualizando imagen del arma: " + e.getMessage());
        }
    }
    
    @FXML
    private void recargarArma(ActionEvent e) {
        if (armaActual == null) {
            System.err.println("❌ ERROR: Arma actual es null");
            return;
        }
        
        if (armaActual.getMunicionRestante() == armaActual.getCapacidadCargador()) {
            mostrarMensajeAccion("⚠️ Cargador lleno!");
            System.out.println("⚠️ El cargador ya está lleno");
            return;
        }
        
        armaActual.recargar();
        mostrarMensajeAccion("🔄 Recargando...");
        System.out.println("🔄 Arma recargada: " + armaActual.getMunicionRestante() + "/" + 
                         armaActual.getCapacidadCargador());
        
        actualizarPanelArmas();
    }
    
    @FXML
    private void verLogros(ActionEvent e) {
        OregonApplication.cambiarVista("logros-view.fxml");
    }

    @FXML
    private void mostrarVictoria(ActionEvent e) {
        if (resourceThread != null) {
            resourceThread.interrupt();
        }
        OregonApplication.cambiarVista("victoria-view.fxml");
    }

    @FXML
    private void mostrarDerrota(ActionEvent e) {
        if (resourceThread != null) {
            resourceThread.interrupt();
        }
        OregonApplication.cambiarVista("gameover-view.fxml");
    }

    @FXML
    private void volverAlMenu(ActionEvent e) {
        if (resourceThread != null) {
            resourceThread.interrupt();
        }
        OregonApplication.cambiarVista("menu-view.fxml");
    }
    
    // ========================================================================
    // CONSUMO DE RECURSOS (E = COMIDA, M = MEDICINA)
    // ========================================================================
    
    /**
     * Consume comida del inventario para restaurar vida (Tecla E)
     */
    private void comerComida() {
        if (jugador == null) return;
        
        Inventario inv = jugador.getInventario();
        if (inv == null) return;
        
        int comidaDisponible = inv.obtenerCantidad(TIPORECURSO.COMIDA);
        
        // Verificar si hay comida
        if (comidaDisponible <= 0) {
            mostrarMensajeAccion("❌ No tienes comida!");
            System.out.println("❌ No hay comida en el inventario");
            return;
        }
        
        // Verificar si la vida está llena
        if (jugador.getSalud() >= 3) {
            mostrarMensajeAccion("❤️ Vida completa!");
            System.out.println("❤️ La vida ya está al máximo");
            return;
        }
        
        // Consumir 1 comida
        boolean consumido = inv.eliminarRecurso(TIPORECURSO.COMIDA, 1);
        
        if (consumido) {
            // Restaurar 1 punto de vida
            int vidaActual = jugador.getSalud();
            int nuevaVida = Math.min(3, vidaActual + 1);
            jugador.setSalud(nuevaVida);
            
            mostrarMensajeAccion("🍖 Comida consumida! +1 ❤️");
            System.out.println("🍖 Comida consumida | Vida: " + vidaActual + " → " + nuevaVida);
            
            updateHUD();
        } else {
            mostrarMensajeAccion("❌ Error al consumir comida");
            System.err.println("❌ Error al eliminar comida del inventario");
        }
    }
    
    /**
     * Usa medicina del inventario para restaurar vida completamente (Tecla M)
     */
    private void usarMedicina() {
        if (jugador == null) return;
        
        Inventario inv = jugador.getInventario();
        if (inv == null) return;
        
        int medicinaDisponible = inv.obtenerCantidad(TIPORECURSO.MEDICINA);
        
        // Verificar si hay medicina
        if (medicinaDisponible <= 0) {
            mostrarMensajeAccion("❌ No tienes medicina!");
            System.out.println("❌ No hay medicina en el inventario");
            return;
        }
        
        // Verificar si la vida está llena
        if (jugador.getSalud() >= 3) {
            mostrarMensajeAccion("❤️ Vida completa!");
            System.out.println("❤️ La vida ya está al máximo");
            return;
        }
        
        // Consumir 1 medicina
        boolean consumido = inv.eliminarRecurso(TIPORECURSO.MEDICINA, 1);
        
        if (consumido) {
            // Restaurar vida completa
            int vidaActual = jugador.getSalud();
            jugador.setSalud(3);
            
            mostrarMensajeAccion("💊 Medicina usada! Vida restaurada!");
            System.out.println("💊 Medicina usada | Vida: " + vidaActual + " → 3 (completa)");
            
            updateHUD();
        } else {
            mostrarMensajeAccion("❌ Error al usar medicina");
            System.err.println("❌ Error al eliminar medicina del inventario");
        }
    }

    private void actualizarProyectiles() {
        actualizarProyectilesEnemigos();
        actualizarProyectilesJugador();
    }

    private void dispararProyectilEnemigo(Enemigo enemigo) {
        double enemigoX = enemigo.getPosicionX() * escala + 20;
        double enemigoY = enemigo.getPosicionY() * escala + 20;
        
        Proyectil proyectil = new Proyectil(enemigoX, enemigoY, 0, 0, enemigo.getDano());
        proyectil.setPersigue(true);
        
        Platform.runLater(() -> {
            javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(8);
            circulo.setFill(javafx.scene.paint.Color.rgb(255, 50, 50, 0.9));
            circulo.setStroke(javafx.scene.paint.Color.DARKRED);
            circulo.setStrokeWidth(3);
            
            javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
            circulo.setEffect(glow);
            
            // Usar setCenterX/Y para círculos en lugar de setLayoutX/Y
            circulo.setCenterX(enemigoX);
            circulo.setCenterY(enemigoY);
            
            gamePane.getChildren().add(circulo);
            
            ImageView sprite = new ImageView();
            sprite.setUserData(circulo);
            proyectil.setSprite(sprite);
            
            proyectilesEnemigos.add(proyectil);
        });
        
        System.out.println("🔴 Enemigo " + enemigo.getId() + " disparó proyectil");
    }

}