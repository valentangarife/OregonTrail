package structuresTest;
import org.junit.jupiter.api.Test;
import structures.BinarySearch;
import structures.LinkedList;
import structures.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinarySearchTest {

    private LinkedList<Integer, Integer> buildSortedList() {
        LinkedList<Integer, Integer> list = new LinkedList<>();

        // Lista ya ordenada por key: 1, 3, 5, 7, 9
        list.addLast(new Node<>(1, 1));
        list.addLast(new Node<>(3, 3));
        list.addLast(new Node<>(5, 5));
        list.addLast(new Node<>(7, 7));
        list.addLast(new Node<>(9, 9));

        return list;
    }

    @Test
    public void findsExistingElement() {
        LinkedList<Integer, Integer> list = buildSortedList();
        BinarySearch<Integer> bs = new BinarySearch<>();
        String pos = bs.binarySearch(list, 5);

        assertEquals("3", pos); // 5 está en índice 2 -> posición lógica 3
    }

    @Test
    public void returnsMinusOneWhenNotFound() {
        LinkedList<Integer, Integer> list = buildSortedList();
        BinarySearch<Integer> bs = new BinarySearch<>();

        String pos = bs.binarySearch(list, 4);

        assertEquals("-1", pos);
    }

    @Test
    public void searchFirstAndLast() {
        LinkedList<Integer, Integer> list = buildSortedList();
        BinarySearch<Integer> bs = new BinarySearch<>();

        String posFirst = bs.binarySearch(list, 1);
        String posLast  = bs.binarySearch(list, 9);

        assertEquals("1", posFirst);
        assertEquals("5", posLast);
    }
}
