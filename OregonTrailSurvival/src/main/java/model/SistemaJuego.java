package model;
import enums.PROFESION;
import enums.TIPORECURSO;

import java.util.List;
import java.util.Random;

public class SistemaJuego {

    private Jugador jugador;
    private List<Escenario> escenarios;
    private boolean juegoActivo;
    private int indiceEscenarioActual;
    private ArbolLogros arbolLogros;

    public SistemaJuego(Jugador jugador,
                        List<Escenario> escenarios,
                        boolean juegoActivo,
                        ArbolLogros arbolLogros) {
        this.jugador = jugador;
        this.escenarios = escenarios;
        this.juegoActivo = juegoActivo;
        this.indiceEscenarioActual = 0;
        this.arbolLogros = arbolLogros;
    }

    // para no dañar los test agregamos este constructor
    public SistemaJuego(Jugador jugador, List<Escenario> escenarios, boolean juegoActivo) {
        this(jugador, escenarios, juegoActivo, null);
    }

    public SistemaJuego() {
        this.juegoActivo = false;
        this.indiceEscenarioActual = 0;
        this.escenarios = new java.util.ArrayList<>();
    }


    // getters y setters
    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public List<Escenario> getEscenarios() {
        return escenarios;
    }

    public void setEscenarios(List<Escenario> escenarios) {
        this.escenarios = escenarios;
    }

    public boolean getJuegoActivo() {
        return juegoActivo;
    }

    public void setJuegoActivo(boolean juegoActivo) {
        this.juegoActivo = juegoActivo;
    }

    public int getIndiceEscenarioActual() {
        return indiceEscenarioActual;
    }

    public int getTotalEscenarios() {
        if (escenarios == null) return 0;
        return escenarios.size();
    }

    public Escenario getEscenarioActual() {
        if (escenarios == null || escenarios.isEmpty()) return null;
        if (indiceEscenarioActual < 0 || indiceEscenarioActual >= escenarios.size()) return null;
        return escenarios.get(indiceEscenarioActual);
    }

    public ArbolLogros getArbolLogros() {
        return arbolLogros;
    }

    public void setArbolLogros(ArbolLogros arbolLogros) {
        this.arbolLogros = arbolLogros;
    }

    //metodos
    public void iniciarPartida() {
        if (jugador == null || escenarios == null || escenarios.isEmpty()) {
            juegoActivo = false;
            return;
        }

        juegoActivo = true;
        indiceEscenarioActual = 0;

        // Reiniciamos la salud base del jugador al empezar de nuevo
        jugador.setSalud(3);

        Escenario primero = escenarios.get(indiceEscenarioActual);
        jugador.cambiaEscenario(primero);
    }

    public void reiniciarPartida() {
        iniciarPartida();
    }

    public void avanzarAlSiguienteEscenario() {
        if (escenarios == null || escenarios.isEmpty()) return;
        if (indiceEscenarioActual + 1 >= escenarios.size()) return;

        indiceEscenarioActual++;
        Escenario nuevo = escenarios.get(indiceEscenarioActual);

        if (jugador != null) jugador.cambiaEscenario(nuevo);
    }

    public void generarEnemigoAleatorio(Escenario escenarioObjetivo) {
        if (escenarioObjetivo == null || escenarioObjetivo.getEnemigos() == null) return;

        Random random = new Random();

        String id = "ENEMIGO_" + (escenarioObjetivo.getEnemigos().size() + 1);
        int dificultad = escenarioObjetivo.getDificultad();
        String nombre = "Enemigo Nivel " + dificultad;

        int salud = 30 + dificultad * 10;
        int dano = 5 + dificultad * 2;

        int posX = random.nextInt(10);
        int posY = random.nextInt(10);

        Enemigo nuevo = new Enemigo(id, nombre, salud, dano, posX, posY);
        escenarioObjetivo.getEnemigos().add(nuevo);
    }

    public Recurso generarRecursoAleatorio(Escenario escenario) {
        // Rango del mapa
        int filas = escenario.getAlto();
        int columnas = escenario.getAncho();

        Random random = new Random();

        // Crear posiciones X y Y aleatorias
        int x = random.nextInt(filas);
        int y = random.nextInt(columnas);

        // Crear recurso aleatorio
        Recurso recurso = new Recurso("Recurso" + System.currentTimeMillis(), x, y);

        // Agregar al escenario
        escenario.getRecursos().add(recurso);

        return recurso;
    }


    public void generarVariosRecursos(Escenario escenario, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            generarRecursoAleatorio(escenario);
        }
    }


    public void finalizarJuego() {
        juegoActivo = false;
    }

    public void actualizarEstadoJuego() {
        if (jugador == null) return;

        if (!jugador.estaVivo()) {
            finalizarJuego();
            return;
        }

        Escenario actual = getEscenarioActual();
        if (actual == null) return;

        boolean sinEnemigos = actual.getEnemigos() == null || actual.getEnemigos().isEmpty();
        boolean esUltimo = escenarios != null &&
                !escenarios.isEmpty() &&
                indiceEscenarioActual == escenarios.size() - 1;

        if (sinEnemigos && esUltimo) {
            finalizarJuego();
        }
    }

    public void desbloquearLogro(int idLogro) {
        if (arbolLogros == null) {
            return;
        }
        arbolLogros.marcarLogroComoObtenido(idLogro);
    }

    /**
     * Calcula un puntaje final usando:
     * - salud final
     * - recursos en inventario
     * - dinero restante
     * y aplica un multiplicador según la profesión:
     * BANQUERO -> x1  (más fácil, menos puntos)
     * CARPINTERO -> x2
     * GRANJERO -> x3  (más difícil, más puntos)
     */
    public int calcularPuntajeFinal() {
        if (jugador == null) {
            return 0;
        }

        int base = 0;

        // Salud => vale bastante
        base += jugador.getSalud() * 100;

        // Recursos del inventario
        Inventario inv = jugador.getInventario();
        if (inv != null) {
            base += inv.obtenerCantidad(TIPORECURSO.COMIDA) * 10;
            base += inv.obtenerCantidad(TIPORECURSO.MEDICINA) * 15;
            base += inv.obtenerCantidad(TIPORECURSO.MUNICION) * 5;
        }

        // Dinero sobrante al final
        base += Integer.parseInt(String.valueOf(jugador.getDinero()));

        int multiplicador = obtenerMultiplicadorProfesion(jugador.getProfesion());
        return base * multiplicador;
    }

    private int obtenerMultiplicadorProfesion(PROFESION profesion) {
        if (profesion == null) {
            return 1;
        }
        switch (profesion) {
            case BANQUERO:
                return 1; // más fácil
            case CARPINTERO:
                return 2;
            case GRANJERO:
                return 3; // más difícil, recompensa mayor
            default:
                return 1;
        }
    }
}
