package modelTest;
import model.ArbolLogros;
import model.Logro;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArbolLogrosTest {
    private ArbolLogros arbol;


    private void setUp() {
        arbol = new ArbolLogros();
        arbol.agregarLogro(new Logro(1, "Primer enemigo", "Derrotar tu primer enemigo"));
        arbol.agregarLogro(new Logro(2, "Sobreviviente", "Completar el primer escenario"));
        arbol.agregarLogro(new Logro(3, "Experto", "Completar el juego"));
    }

    @Test
    public void insertarYBuscarLogro() {
        setUp();
        Logro l = arbol.buscarLogroPorId(2);
        assertNotNull(l);
        assertEquals("Sobreviviente", l.getNombre());
    }

    @Test
    public void marcarComoObtenidoFunciona() {
        setUp();
        boolean ok = arbol.marcarLogroComoObtenido(1);
        assertTrue(ok);

        Logro l = arbol.buscarLogroPorId(1);
        assertTrue(l.isObtenido());
    }

    @Test
    public void idsEnOrdenSonCorrectos() {
        setUp();
        String inOrder = arbol.obtenerIdsEnOrden();
        assertEquals("1 2 3", inOrder);
    }
}
