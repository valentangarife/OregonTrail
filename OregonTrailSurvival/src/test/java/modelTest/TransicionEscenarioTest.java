package modelTest;

import enums.PROFESION;
import model.Escenario;
import model.Jugador;
import model.PuntoAcceso;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TransicionEscenarioTest {

    private Jugador jugador;
    private Escenario escenario1;
    private Escenario escenario2;
    private Escenario escenario3;

    // Stage 1: Jugador en Llanuras, puede avanzar a Montañas
    public void setUpStage1() {
        jugador = new Jugador("Valen", PROFESION.CARPINTERO);
        
        escenario1 = new Escenario(
                "Llanuras",
                "Llanuras y Praderas",
                1,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenario2 = new Escenario(
                "Montañas Rocosas",
                "Terreno montañoso",
                2,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Conectar escenarios
        PuntoAcceso portal = new PuntoAcceso(escenario2, "Portal a Montañas", true);
        escenario1.getPuntosAcceso().add(portal);

        jugador.setEscenarioActual(escenario1);
        jugador.setPosicionX(5);
        jugador.setPosicionY(5);
    }

    // Stage 2: Jugador en Montañas, puede avanzar a Río
    public void setUpStage2() {
        jugador = new Jugador("Dayana", PROFESION.BANQUERO);
        
        escenario2 = new Escenario(
                "Montañas Rocosas",
                "Terreno montañoso",
                2,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenario3 = new Escenario(
                "Río Columbia",
                "Río y áreas cercanas a Oregón",
                3,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Conectar escenarios
        PuntoAcceso portal = new PuntoAcceso(escenario3, "Portal a Río", true);
        escenario2.getPuntosAcceso().add(portal);

        jugador.setEscenarioActual(escenario2);
        jugador.setPosicionX(10);
        jugador.setPosicionY(10);
    }

    // Stage 3: Jugador en Río (último escenario)
    public void setUpStage3() {
        jugador = new Jugador("Alejandra", PROFESION.GRANJERO);
        
        escenario3 = new Escenario(
                "Río Columbia",
                "Río y áreas cercanas a Oregón",
                3,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        jugador.setEscenarioActual(escenario3);
        jugador.setPosicionX(15);
        jugador.setPosicionY(12);
    }

    // TESTS

    @Test
    public void jugadorCambiaDeEscenario() {
        setUpStage1();
        assertEquals("Llanuras", jugador.getEscenarioActual().getNombre());
        
        // Simular cambio de escenario
        jugador.cambiaEscenario(escenario2);
        
        assertEquals("Montañas Rocosas", jugador.getEscenarioActual().getNombre());
    }

    @Test
    public void escenarioTienePortalDisponible() {
        setUpStage1();
        assertFalse(escenario1.getPuntosAcceso().isEmpty());
        assertTrue(escenario1.getPuntosAcceso().get(0).esAccesible());
    }

    @Test
    public void portalApuntaAlEscenarioCorrecto() {
        setUpStage1();
        PuntoAcceso portal = escenario1.getPuntosAcceso().get(0);
        assertEquals("Montañas Rocosas", portal.getDestino().getNombre());
    }

    @Test
    public void jugadorMantienePosicionAlCambiarEscenario() {
        setUpStage1();
        int posXAntes = jugador.getPosicionX();
        int posYAntes = jugador.getPosicionY();
        
        jugador.cambiaEscenario(escenario2);
        
        // La posición se mantiene (será reajustada por el controlador)
        assertEquals(posXAntes, jugador.getPosicionX());
        assertEquals(posYAntes, jugador.getPosicionY());
    }

    @Test
    public void jugadorMantieneEstadoAlCambiarEscenario() {
        setUpStage1();
        int saludAntes = jugador.getSalud();
        int dineroAntes = jugador.getDinero();
        
        jugador.cambiaEscenario(escenario2);
        
        assertEquals(saludAntes, jugador.getSalud());
        assertEquals(dineroAntes, jugador.getDinero());
    }

    @Test
    public void transicionCompletaDeLlanurasARio() {
        setUpStage1();
        
        // Agregar portal de Montañas a Río
        PuntoAcceso portal2 = new PuntoAcceso(escenario3, "Portal a Río", true);
        escenario2.getPuntosAcceso().add(portal2);
        
        // Primera transición: Llanuras → Montañas
        assertEquals("Llanuras", jugador.getEscenarioActual().getNombre());
        jugador.cambiaEscenario(escenario2);
        assertEquals("Montañas Rocosas", jugador.getEscenarioActual().getNombre());
        
        // Segunda transición: Montañas → Río
        jugador.cambiaEscenario(escenario3);
        assertEquals("Río Columbia", jugador.getEscenarioActual().getNombre());
    }

    @Test
    public void escenarioFinalNoTienePortalSalida() {
        setUpStage3();
        // El escenario final (Río) no debería tener portales de salida
        assertTrue(escenario3.getPuntosAcceso().isEmpty());
    }

    @Test
    public void dificultadAumentaConEscenarios() {
        setUpStage1();
        
        assertEquals(1, escenario1.getDificultad());
        assertEquals(2, escenario2.getDificultad());
        
        setUpStage2();
        assertEquals(3, escenario3.getDificultad());
    }

    @Test
    public void jugadorPuedeVolverAEscenarioAnterior() {
        setUpStage1();
        
        // Agregar portal bidireccional
        PuntoAcceso portalVuelta = new PuntoAcceso(escenario1, "Portal de vuelta", true);
        escenario2.getPuntosAcceso().add(portalVuelta);
        
        // Ir a Montañas
        jugador.cambiaEscenario(escenario2);
        assertEquals("Montañas Rocosas", jugador.getEscenarioActual().getNombre());
        
        // Volver a Llanuras
        jugador.cambiaEscenario(escenario1);
        assertEquals("Llanuras", jugador.getEscenarioActual().getNombre());
    }

    @Test
    public void escenarioTieneDimensionesCorrectas() {
        setUpStage1();
        assertEquals(40, escenario1.getAncho());
        assertEquals(15, escenario1.getAlto());
    }
}
