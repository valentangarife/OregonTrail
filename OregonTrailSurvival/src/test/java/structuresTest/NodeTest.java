package structuresTest;
import structures.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    public void compareToMenorMayor() {
        Node<Integer, String> n1 = new Node<>("A", 1);
        Node<Integer, String> n2 = new Node<>("B", 5);

        int result1 = n1.compareTo(n2);
        int result2 = n2.compareTo(n1);

        assertTrue(result1 < 0);  // 1 < 5
        assertTrue(result2 > 0);  // 5 > 1
    }

    @Test
    public void compareToIgual() {
        Node<Integer, String> n1 = new Node<>("A", 3);
        Node<Integer, String> n2 = new Node<>("B", 3);

        int result = n1.compareTo(n2);

        assertEquals(0, result);
    }
}

