package structuresTest;

import structures.Generic_BST;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Generic_BSTTest {

    private Generic_BST<Integer, String> buildSimpleTree() {
        Generic_BST<Integer, String> bst = new Generic_BST<>();

        bst.insert(5, "A");
        bst.insert(2, "B");
        bst.insert(8, "C");
        bst.insert(1, "D");
        bst.insert(3, "E");

        return bst;
    }

    @Test
    public void inOrderReturnsSortedKeys() {
        Generic_BST<Integer, String> bst = buildSimpleTree();

        String inOrder = bst.inOrder();

        // Las claves insertadas: 5,2,8,1,3 -> inOrder debe ser: "1 2 3 5 8"
        assertEquals("1 2 3 5 8", inOrder);
    }

    @Test
    public void searchReturnsCorrectValue() {
        Generic_BST<Integer, String> bst = buildSimpleTree();

        String valFor3 = bst.search(3);
        String valFor8 = bst.search(8);
        String valFor10 = bst.search(10); // no existe

        assertEquals("E", valFor3);
        assertEquals("C", valFor8);
        assertNull(valFor10);
    }

    @Test
    public void deleteLeafNode() {
        Generic_BST<Integer, String> bst = buildSimpleTree();

        String deleted = bst.delete(1); // 1 es hoja

        assertEquals("D", deleted);
        assertEquals("2 3 5 8", bst.inOrder());
        assertNull(bst.search(1));
    }

    @Test
    public void deleteNodeWithOneChild() {
        Generic_BST<Integer, String> bst = new Generic_BST<>();
        // aqui construimos un árbol donde 2 tiene un solo hijo 1. ej:
        //    5
        //   /
        //  2
        // /
        //1
        bst.insert(5, "A");
        bst.insert(2, "B");
        bst.insert(1, "C");

        String deleted = bst.delete(2);

        assertEquals("B", deleted);
        // ahora deberían quedar claves 1 y 5
        assertEquals("1 5", bst.inOrder());
        assertNull(bst.search(2));
    }

    @Test
    public void deleteNodeWithTwoChildren() {
        Generic_BST<Integer, String> bst = buildSimpleTree();
        // 5 tiene dos hijos (2 y 8)

        String deleted = bst.delete(5);

        assertEquals("A", deleted);
        // El árbol sigue siendo un BST, solo revisamos que ya no está la clave 5
        assertFalse(bst.inOrder().contains("5"));
        assertNull(bst.search(5));
    }
}
