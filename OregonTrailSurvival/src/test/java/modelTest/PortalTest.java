package modelTest;

import model.Escenario;
import model.Portal;
import model.PuntoAcceso;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PortalTest {

    private Escenario escenarioOrigen;
    private Escenario escenarioDestino;
    private PuntoAcceso puntoAcceso;

    // Stage 1: Portal de Llanuras a Montañas
    public void setUpStage1() {
        escenarioOrigen = new Escenario(
                "Llanuras",
                "Llanuras y Praderas",
                1,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarioDestino = new Escenario(
                "Montañas Rocosas",
                "Terreno montañoso",
                2,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        puntoAcceso = new PuntoAcceso(
                escenarioDestino,
                "Portal hacia Montañas Rocosas",
                true
        );

        escenarioOrigen.getPuntosAcceso().add(puntoAcceso);
    }

    // Stage 2: Portal de Montañas a Río
    public void setUpStage2() {
        escenarioOrigen = new Escenario(
                "Montañas Rocosas",
                "Terreno montañoso",
                2,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarioDestino = new Escenario(
                "Río Columbia",
                "Río y áreas cercanas a Oregón",
                3,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        puntoAcceso = new PuntoAcceso(
                escenarioDestino,
                "Portal hacia Río Columbia",
                true
        );

        escenarioOrigen.getPuntosAcceso().add(puntoAcceso);
    }

    // Stage 3: Portal bloqueado
    public void setUpStage3() {
        escenarioOrigen = new Escenario(
                "Llanuras",
                "Llanuras y Praderas",
                1,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        escenarioDestino = new Escenario(
                "Montañas Rocosas",
                "Terreno montañoso",
                2,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        puntoAcceso = new PuntoAcceso(
                escenarioDestino,
                "Portal bloqueado",
                false  // NO accesible
        );

        escenarioOrigen.getPuntosAcceso().add(puntoAcceso);
    }

    // TESTS

    @Test
    public void portalTieneDestinoValido() {
        setUpStage1();
        assertNotNull(puntoAcceso.getDestino());
        assertEquals("Montañas Rocosas", puntoAcceso.getDestino().getNombre());
    }

    @Test
    public void portalEsAccesible() {
        setUpStage1();
        assertTrue(puntoAcceso.esAccesible());
    }

    @Test
    public void portalBloqueadoNoEsAccesible() {
        setUpStage3();
        assertFalse(puntoAcceso.esAccesible());
    }

    @Test
    public void escenarioTienePuntoDeAcceso() {
        setUpStage1();
        assertFalse(escenarioOrigen.getPuntosAcceso().isEmpty());
        assertEquals(1, escenarioOrigen.getPuntosAcceso().size());
    }

    @Test
    public void puntoAccesoTieneDescripcion() {
        setUpStage1();
        assertNotNull(puntoAcceso.getDescripcion());
        assertTrue(puntoAcceso.getDescripcion().contains("Montañas"));
    }

    @Test
    public void portalConectaEscenariosCorrectamente() {
        setUpStage2();
        assertEquals("Montañas Rocosas", escenarioOrigen.getNombre());
        assertEquals("Río Columbia", puntoAcceso.getDestino().getNombre());
    }

    @Test
    public void desbloquearPortalBloqueado() {
        setUpStage3();
        assertFalse(puntoAcceso.esAccesible());
        
        // Desbloquear el portal
        puntoAcceso.setAccesible(true);
        
        assertTrue(puntoAcceso.esAccesible());
    }

    @Test
    public void bloquearPortalAccesible() {
        setUpStage1();
        assertTrue(puntoAcceso.esAccesible());
        
        // Bloquear el portal
        puntoAcceso.setAccesible(false);
        
        assertFalse(puntoAcceso.esAccesible());
    }

    @Test
    public void escenarioConMultiplesPuntosDeAcceso() {
        setUpStage1();
        
        // Agregar un segundo punto de acceso
        Escenario tercerEscenario = new Escenario(
                "Río Columbia",
                "Río",
                3,
                40,
                15,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        
        PuntoAcceso segundoPunto = new PuntoAcceso(
                tercerEscenario,
                "Portal alternativo",
                true
        );
        
        escenarioOrigen.getPuntosAcceso().add(segundoPunto);
        
        assertEquals(2, escenarioOrigen.getPuntosAcceso().size());
    }

    @Test
    public void obtenerPrimerPuntoAccesible() {
        setUpStage1();
        
        // Agregar un punto bloqueado
        PuntoAcceso puntoBloqueado = new PuntoAcceso(
                escenarioDestino,
                "Portal bloqueado",
                false
        );
        escenarioOrigen.getPuntosAcceso().add(0, puntoBloqueado);
        
        // Buscar el primer punto accesible
        PuntoAcceso accesible = null;
        for (PuntoAcceso pa : escenarioOrigen.getPuntosAcceso()) {
            if (pa.esAccesible()) {
                accesible = pa;
                break;
            }
        }
        
        assertNotNull(accesible);
        assertTrue(accesible.esAccesible());
        assertEquals("Portal hacia Montañas Rocosas", accesible.getDescripcion());
    }
}
