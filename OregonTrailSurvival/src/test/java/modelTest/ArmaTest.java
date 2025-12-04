package modelTest;

import enums.NOMBRE_ARMA;
import model.Arma;
import model.Enemigo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArmaTest {

    private Arma arma;
    private Enemigo enemigo;

    /**
     * Stage1: arma con cargador a mitad
     * Ejemplo: RIFLE, daño=30, cap=6, munición=3; enemigo salud=50
     * Propósito: probar disparo normal (baja munición y vida).
     */
    public void setUpStage1() {
        arma = new Arma(NOMBRE_ARMA.RIFLE_AVANCARGA,
                30,   // daño
                6,    // capacidad cargador
                3,    // munición restante
                2.0,  // tiempo recarga
                3.0   // velocidad disparo
        );

        enemigo = new Enemigo(
                "E1",
                "Bandido",
                50, // salud
                5,  // daño del enemigo (no importa aquí)
                0, 0
        );
    }

    /**
     * Stage2: arma con cargador vacío
     * Ejemplo: cap=6, munición=0; enemigo salud=40
     * Propósito: disparar sin balas no debe cambiar nada.
     */
    public void setUpStage2() {
        arma = new Arma(
                NOMBRE_ARMA.RIFLE_AVANCARGA,
                30,
                6,
                0,    // sin munición
                2.0,
                3.0
        );

        enemigo = new Enemigo(
                "E2",
                "Forajido",
                40,
                5,
                0, 0
        );
    }

    /**
     * Stage3: arma con única bala que mata justo al enemigo.
     * Ejemplo: daño=40, cap=1, munición=1, enemigo salud=40.
     */
    public void setUpStage3() {
        arma = new Arma(
                NOMBRE_ARMA.REVOLVER,
                40,   // daño
                1,    // capacidad
                1,    // única bala
                3.5,
                1.0
        );

        enemigo = new Enemigo(
                "E3",
                "Coyote",
                40,
                3,
                0, 0
        );
    }
    //ya pasamos a los test

    @Test // positivo: con munición debe estar disponible
    public void disponibleConMunicion() {
        setUpStage1();
        boolean disponible = arma.estaDisponible();
        assertTrue(disponible, "Con munición > 0 el arma debe estar disponible");
    }

    @Test // negativo: sin munición no está disponible
    public void noDisponibleSinMunicion() {
        setUpStage2();
        boolean disponible = arma.estaDisponible();
        assertFalse(disponible, "Con munición = 0 el arma no debe estar disponible");
    }

    @Test // positivo: disparar consume bala y hiere al enemigo
    public void dispararConsumeBalaYHiere() {
        setUpStage1();
        int municionAntes = arma.getMunicionRestante();
        int saludAntes = enemigo.getSalud();
        arma.disparar(enemigo);
        assertEquals(municionAntes - 1, arma.getMunicionRestante(), "Disparar debe consumir 1 bala");
        assertTrue(enemigo.getSalud() < saludAntes, "La salud del enemigo debe disminuir al disparar");
    }

    @Test // negativo: disparar sin munición no cambia nada
    public void dispararSinMunicionNoCambia() {
        setUpStage2();
        int municionAntes = arma.getMunicionRestante(); // 0
        int saludAntes = enemigo.getSalud();

        arma.disparar(enemigo);
        assertEquals(municionAntes, arma.getMunicionRestante(), "Sin munición, no se debe consumir nada");
        assertEquals(saludAntes, enemigo.getSalud(), "Sin munición, la salud del enemigo no debe cambiar");
    }

    @Test // positivo: recargar desde vacío llena el cargador
    public void recargarDesdeVacioLlena() {
        setUpStage2(); // munición = 0
        int capacidad = arma.getCapacidadCargador();

        arma.recargar();

        assertEquals(capacidad, arma.getMunicionRestante(),
                "Recargar debe dejar el cargador lleno");
    }

    @Test // positivo: única bala elimina enemigo sin dejar salud negativa
    public void unicaBalaEliminaEnemigo() {
        setUpStage3();
        arma.disparar(enemigo);
        assertEquals(0, arma.getMunicionRestante(), "Después del disparo debe quedar 0 balas");
        assertEquals(0, enemigo.getSalud(), "La salud no debe ser negativa, debe quedar en 0");
    }

    @Test // negativo: disparar a objetivo null no lanza error ni cambia munición
    public void dispararAObjetivoNullNoFalla() {
        setUpStage1();
        int municionAntes = arma.getMunicionRestante();
        arma.disparar(null);
        assertEquals(municionAntes, arma.getMunicionRestante(), "Si el objetivo es null, la munición no debe cambiar");
    }
}
