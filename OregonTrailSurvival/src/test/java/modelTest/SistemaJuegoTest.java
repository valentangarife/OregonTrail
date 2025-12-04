package modelTest;
import enums.PROFESION;
import enums.TIPORECURSO;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SistemaJuegoTest {

    private SistemaJuego sistema;
    private Jugador jugador;
    private List<Escenario> escenarios;

    // =============== STAGE 1 – Llanuras: Sistema con 1 escenario y juego inactivo
    public void setUpStage1() {
        // aquí usamos el constructor viejo para no dañar otros tests
        jugador = new Jugador("Valen", 3);

        Escenario llanuras = new Escenario(
                "Llanuras",
                "Escenario inicial del viaje",
                1,
                new ArrayList<>(),   // recursos
                new ArrayList<>(),   // enemigos
                new ArrayList<>()    // puntos acceso
        );

        escenarios = new ArrayList<>();
        escenarios.add(llanuras);

        sistema = new SistemaJuego(jugador, escenarios, false);
    }

    // =============== STAGE 2 – Montañas: probar generar enemigo aleatorio
    public void setUpStage2() {
        jugador = new Jugador("Dayana", 3);

        Escenario montanas = new Escenario(
                "Montañas Rocosas",
                "Zona peligrosa y fría",
                2,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarios = new ArrayList<>();
        escenarios.add(montanas);

        sistema = new SistemaJuego(jugador, escenarios, false);
    }

    // =============== STAGE 3 – Río Columbia: probar finalizarJuego()
    public void setUpStage3() {
        jugador = new Jugador("Alejandra", 2);

        Escenario rio = new Escenario(
                "Río Columbia",
                "Corrientes heladas",
                3,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarios = new ArrayList<>();
        escenarios.add(rio);

        sistema = new SistemaJuego(jugador, escenarios, true);
    }


    @Test
    public void iniciarActivaYAsignaLlanuras() {
        setUpStage1();

        sistema.iniciarPartida();

        assertTrue(sistema.getJuegoActivo());
        assertNotNull(sistema.getJugador().getEscenarioActual());
        assertEquals("Llanuras", sistema.getJugador().getEscenarioActual().getNombre());
    }

    @Test
    public void iniciarSinEscenariosNoActiva() {
        jugador = new Jugador("Valen", 3);

        sistema = new SistemaJuego(jugador, new ArrayList<>(), false);

        sistema.iniciarPartida();

        assertFalse(sistema.getJuegoActivo());
        assertNull(sistema.getJugador().getEscenarioActual());
    }

    @Test
    public void generarEnemigoEnMontanas() {
        setUpStage2();

        Escenario montanas = sistema.getEscenarios().get(0);
        int antes = montanas.getEnemigos().size();

        sistema.generarEnemigoAleatorio(montanas);

        assertEquals(antes + 1, montanas.getEnemigos().size());
    }

    @Test
    public void finalizarCambiaAInactivo() {
        setUpStage3();

        sistema.finalizarJuego();

        assertFalse(sistema.getJuegoActivo());
    }

    @Test
    public void iniciarNoReemplazaJugador() {
        setUpStage1();

        Jugador original = sistema.getJugador();

        sistema.iniciarPartida();

        assertSame(original, sistema.getJugador());
    }


    @Test
    public void puntajeFinalDependeDeProfesion() {
        // Jugador banquero (más dinero inicial, menor multiplicador)
        Jugador banquero = new Jugador("Valen", PROFESION.BANQUERO);

        // Inventario con algunos recursos
        Inventario invBanquero = new Inventario(10);
        invBanquero.agregarRecurso(new Recurso(TIPORECURSO.COMIDA, 5));
        invBanquero.agregarRecurso(new Recurso(TIPORECURSO.MEDICINA, 2));
        invBanquero.agregarRecurso(new Recurso(TIPORECURSO.MUNICION, 10));


        // Escenario cualquiera para que el sistema tenga algo
        Escenario escenario = new Escenario(
                "Pradera",
                "Escenario de prueba",
                1,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        List<Escenario> lista = new ArrayList<>();
        lista.add(escenario);

        SistemaJuego sistemaBanquero = new SistemaJuego(banquero, lista, false);

        // Mismo setup para granjero, pero profesión diferente
        Jugador granjero = new Jugador("Valen", PROFESION.GRANJERO);
        Inventario invGranjero = new Inventario(10);
        invGranjero.agregarRecurso(new Recurso(TIPORECURSO.COMIDA, 5));
        invGranjero.agregarRecurso(new Recurso(TIPORECURSO.MEDICINA, 2));
        invGranjero.agregarRecurso(new Recurso(TIPORECURSO.MUNICION, 10));


        SistemaJuego sistemaGranjero = new SistemaJuego(granjero, lista, false);

        int puntajeBanquero = sistemaBanquero.calcularPuntajeFinal();
        int puntajeGranjero = sistemaGranjero.calcularPuntajeFinal();

        // Como el multiplicador del granjero es mayor, su puntaje debe ser mayor
        assertTrue(puntajeGranjero > puntajeBanquero);
    }
}
