package modelTest;

import model.Enemigo;
import model.Jugador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemigoTest {

    private Jugador jugador;
    private Enemigo enemigo;

    // Stage1 (Llanuras): jugador inicia en (5,5) con salud=3.
    // Enemigo en (2,5), salud=2 y daño=1.
    public void setUpStage1() {
        jugador = new Jugador("Valen", 3);
        jugador.setPosicionX(5);
        jugador.setPosicionY(5);

        enemigo = new Enemigo("E1"); // string id
        enemigo.setSalud(2);
        enemigo.setPosicionX(2);
        enemigo.setPosicionY(5);
    }

    // Stage2 (Montañas Rocosas): combate cercano
    // Jugador en (4,4) con salud=3. Enemigo en (3,4), salud=3 y daño=1.
    public void setUpStage2() {
        jugador = new Jugador("Dayana", 3);
        jugador.setPosicionX(4);
        jugador.setPosicionY(4);

        enemigo = new Enemigo("E2");
        enemigo.setSalud(3);
        enemigo.setPosicionX(3);
        enemigo.setPosicionY(4);
    }

    // Stage3 (Río Columbia – escasez): jugador debilitado.
    // Jugador en (3,3) con salud=2. Enemigo en (2,3), salud=2 y daño=1.
    public void setUpStage3() {
        jugador = new Jugador("Alejandra", 2);
        jugador.setPosicionX(3);
        jugador.setPosicionY(3);

        enemigo = new Enemigo("E3");
        enemigo.setSalud(2);
        enemigo.setDano(1);
        enemigo.setPosicionX(2);
        enemigo.setPosicionY(3);
    }

    // TESTS

    @Test // positivo
    public void atacarQuitaUnaVida() {
        setUpStage2();
        int vidaAntes = jugador.getSalud();
        int danyo = enemigo.getDano();

        enemigo.atacar(jugador);

        assertEquals(vidaAntes - danyo, jugador.getSalud());
    }

    @Test // positivo
    public void atacarConJugadorEnDosVidasQuedaEnUna() {
        setUpStage3(); // jugador salud = 2

        enemigo.atacar(jugador);

        assertEquals(1, jugador.getSalud());
    }

    @Test // positivo
    public void recibirDanyoreduceSalud() {
        setUpStage1(); // enemigo salud = 2

        enemigo.recibirDano(1);  // usa el mismo nombre de método que tengas en Enemigo

        assertEquals(1, enemigo.getSalud());
        assertTrue(enemigo.estaVivo());
    }

    @Test // positivo
    public void morirAlLlegarACero() {
        setUpStage1(); // enemigo salud = 2

        enemigo.recibirDano(2);

        assertEquals(0, enemigo.getSalud());
        assertFalse(enemigo.estaVivo());
    }

    @Test // negativo
    public void atacarConDañoCeroNoAfecta() {
        setUpStage2();
        enemigo.setDano(0);
        int vidaAntes = jugador.getSalud();

        enemigo.atacar(jugador);

        assertEquals(vidaAntes, jugador.getSalud(),
                "Con daño=0 la vida del jugador no debe cambiar");
    }
}
