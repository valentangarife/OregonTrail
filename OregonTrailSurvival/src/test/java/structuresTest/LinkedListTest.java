package structuresTest;

import structures.LinkedList;
import structures.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTest {

    @Test
    public void addLastAndSearch() {
        LinkedList<Integer, String> list = new LinkedList<>();

        list.addLast(new Node<>("A", 2)); // value=A, key=2
        list.addLast(new Node<>("B", 5));
        list.addLast(new Node<>("C", 7));

        assertEquals(3, list.getSize());
        assertEquals("A", list.search(0).getValue());
        assertEquals("C", list.search(2).getValue());
    }


    @Test
    public void insertionSortOrdersByKey() {
        LinkedList<Integer, String> list = new LinkedList<>();

        list.addLast(new Node<>("C", 3));
        list.addLast(new Node<>("A", 1));
        list.addLast(new Node<>("B", 2));

        list.insertionSort();

        assertEquals(3, list.getSize());
        assertEquals(1, list.search(0).getKey());
        assertEquals(2, list.search(1).getKey());
        assertEquals(3, list.search(2).getKey());
    }

}
