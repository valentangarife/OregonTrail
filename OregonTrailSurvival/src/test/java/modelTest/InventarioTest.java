package modelTest;

import enums.TIPORECURSO;
import model.Inventario;
import model.Recurso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventarioTest {

    private Inventario inventario;

    // Stage1 (Llanuras – abundante):
    // Inventario vacío con capacidad alta (50).
    public void setUpStage1() {
        inventario = new Inventario(50);
    }

    // Stage2 (Montañas – combate):
    // Inventario vacío con capacidad media (20).
    public void setUpStage2() {
        inventario = new Inventario(20);
    }

    // Stage3 (Río – escasez):
    // Inventario con capacidad reducida (1) para simular límite rápido.
    public void setUpStage3() {
        inventario = new Inventario(1);
    }

    @Test
    public void agregarComidaSumaCantidad() {
        // arrange
        setUpStage1();
        int antes = inventario.obtenerCantidad(TIPORECURSO.COMIDA);
        // act
        boolean ok = inventario.agregarRecurso(new Recurso(TIPORECURSO.COMIDA, 1));
        // assert
        assertTrue(ok);
        assertEquals(antes + 1, inventario.obtenerCantidad(TIPORECURSO.COMIDA));
    }

    @Test
    public void agregarMismoTipoAcumulaCantidadSinNuevoSlot() {
        // arrange
        setUpStage1();
        inventario.agregarRecurso(new Recurso(TIPORECURSO.COMIDA, 1));
        int slotsAntes = inventario.getRecursos().size();
        int cantidadAntes = inventario.obtenerCantidad(TIPORECURSO.COMIDA);
        // act
        inventario.agregarRecurso(new Recurso(TIPORECURSO.COMIDA, 2));
        // assert
        assertEquals(slotsAntes, inventario.getRecursos().size(), "No debe crear un nuevo slot");
        assertEquals(cantidadAntes + 2, inventario.obtenerCantidad(TIPORECURSO.COMIDA));
    }

    @Test
    public void eliminarRecursoReduceCantidadYEliminaCuandoLlegaACero() {
        // arrange
        setUpStage2();
        inventario.agregarRecurso(new Recurso(TIPORECURSO.MEDICINA, 3));
        // act: quitar 2
        boolean ok1 = inventario.eliminarRecurso(TIPORECURSO.MEDICINA, 2);
        // assert
        assertTrue(ok1);
        assertEquals(1, inventario.obtenerCantidad(TIPORECURSO.MEDICINA));

        // act: quitar 1 (debe quedar en 0 y remover slot)
        boolean ok2 = inventario.eliminarRecurso(TIPORECURSO.MEDICINA, 1);
        assertTrue(ok2);
        assertEquals(0, inventario.obtenerCantidad(TIPORECURSO.MEDICINA));
        assertEquals(0, inventario.getRecursos().size(), "Cuando la cantidad llega a 0 se elimina el recurso de la lista");
    }

    @Test
    public void eliminarRecursoTipoInexistenteDevuelveFalse() {
        // arrange
        setUpStage2();
        // act
        boolean ok = inventario.eliminarRecurso(TIPORECURSO.MEDICINA, 1);
        // assert
        assertFalse(ok, "No debería eliminar si no existe ese tipo en el inventario");
    }

    @Test
    public void noAgregaCuandoCapacidadLlena() {
        // arrange
        setUpStage3(); // capacidad = 1 "slot" de tipo
        assertTrue(inventario.agregarRecurso(new Recurso(TIPORECURSO.MUNICION, 5)));
        int slotsAntes = inventario.getRecursos().size();
        // act: intento agregar otro tipo diferente
        boolean ok = inventario.agregarRecurso(new Recurso(TIPORECURSO.COMIDA, 1));
        // assert
        assertFalse(ok, "Con inventario lleno no debe aceptar nuevos tipos de recursos");
        assertEquals(slotsAntes, inventario.getRecursos().size());
    }

    @Test
    public void obtenerCantidadDevuelveCeroSiNoHayTipo() {
        // arrange
        setUpStage1();
        // act & assert
        assertEquals(0, inventario.obtenerCantidad(TIPORECURSO.MEDICINA));
        assertEquals(0, inventario.obtenerCantidad(TIPORECURSO.MUNICION));
        assertEquals(0, inventario.obtenerCantidad(TIPORECURSO.COMIDA));
    }
}
