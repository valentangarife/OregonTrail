package structures;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class LinkedList<T1 extends Comparable<T1>, T2> implements Iterable<Node<T1, T2>> {

    private Node<T1, T2> last;
    private Node<T1, T2> first;
    private int size; // Agregamos el atributo size para saber cuantos elementos tiene la linkedList, y así evitar hacer recorridos innecesarios

    public LinkedList() {
    }

    public Node<T1, T2> getLast() {
        return last;
    }

    public void setLast(Node<T1, T2> last) {
        this.last = last;
    }

    public Node<T1, T2> getFirst() {
        return first;
    }

    public void setFirst(Node<T1, T2> first) {
        this.first = first;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEmpty() { // Este metod0 nos lo inventamos como un atajo y nos va a verificar si size==0
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }

    // --------------AÑADIR-----------------------//

    // AÑADIR ALGO AL FINAL
    public void addLast(Node<T1, T2> node) {
        if (isEmpty()) {
            // Caso lista vacía
            first = node;
            last = node;
        } else {
            // Conecta el nuevo nodo al final
            last.setNextYright(node);
            node.setPreviousYLeft(last);
            last = node;
        }
        size = size + 1;
    }

    // AÑADIR ALGO EN UN INDICE EN ESPECIFICO
    public void add(int index, Node<T1, T2> node) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        if (index == size) { // caso agregar al final
            addLast(node);
            return;
        }

        if (index == 0) { // caso agregar al inicio
            node.setNextYright(first);
            if (first != null) {
                first.setPreviousYLeft(node);
            }
            first = node;
            if (last == null) { // lista estaba vacía
                last = node;
            }
            size = size + 1;
            return;
        }

        // caso general: insertar en medio
        Node<T1, T2> current = nodeAt(index);           // nodo actualmente en 'index'
        Node<T1, T2> prev = current.getPreviousYLeft(); // nodo  anterior

        // conectamos el nuevo nodo entre prev y current
        node.setPreviousYLeft(prev);
        node.setNextYright(current);
        prev.setNextYright(node);
        current.setPreviousYLeft(node);

        size = size + 1;
    }

    // -------------ELIMINAR------------------------//

    // ELIMINAR ALGO EN UN INDICE EN ESPECIFICO
    public T2 delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        Node<T1, T2> current = nodeAt(index);

        if (current == first) { // caso deleteFirst
            first = current.getNextYright();
            if (first != null) {
                first.setPreviousYLeft(null);
            } else {
                last = null; // la lista quedó vacía
            }
        } else if (current == last) { // caso deleteLast
            last = current.getPreviousYLeft();
            if (last != null) {
                last.setNextYright(null);
            } else {
                first = null; // lista vacía
            }
        } else { // caso general (nodo en medio)
            current.getPreviousYLeft().setNextYright(current.getNextYright());
            current.getNextYright().setPreviousYLeft(current.getPreviousYLeft());
        }

        size = size - 1;
        return current.getValue();
    }

    // -------------BUSCAR------------------------//

    // BUSCAR ALGO EN UN INDICE ESPECIFICO
    public Node<T1, T2> search(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return nodeAt(index);
    }

    // Recorre la lista hasta el índice dado y devuelve el nodo
    private Node<T1, T2> nodeAt(int index) {
        Node<T1, T2> current = first;
        int i = 0;
        while (i < index) {
            current = current.getNextYright();
            i = i + 1;
        }
        return current;
    }

    // -------------IMPRIMIR------------------------//
    public String showAll() throws NullPointerException {
        try {
            if (isEmpty()) { // si la lista está vacía ponemos un mensaje y terminamos
                return "Lista vacía";
            }

            StringBuilder sb = new StringBuilder("List (first->last): "); // usamos sb para armar la cadena del resultado
            Node<T1, T2> current = first; // Creamos una variable auxiliar 'current' para recorrer la lista desde el primer nodo

            while (current != null) { // Mientras no lleguemos al final (cuando current es null), seguimos recorriendo
                sb.append(current.getValue()); // Agregamos el valor almacenado en el nodo actual a la cadena
                current = current.getNextYright(); // Avanzamos al siguiente nodo usando el enlace 'next'
                if (current != null) { // Si aún hay más nodos, añadimos un separador " -> "
                    sb.append(" -> ");
                }
            }
            return sb.toString(); // Cuando el bucle termina, devolvemos la cadena completa
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    // -------------ORDENAMIENTO (INSERTION SORT)------------------------//

    /**
     * Ordena la lista ascendentemente por la clave (T1) usando Insertion Sort.
     * Mantiene correctamente los enlaces dobles y actualiza first/last.
     * Complejidad: O(n^2) en el peor caso — eficiente para listas pequeñas o casi ordenadas.
     */
    public void insertionSort() {
        if (first == null) {
            return;
        }
        if (first.getNextYright() == null) {
            return;
        }

        // 'sortedFirst' será el inicio de la sublista ya ordenada
        Node<T1, T2> sortedFirst = null;

        // Recorremos la lista original, extrayendo cada nodo 'current'
        Node<T1, T2> current = first;
        while (current != null) {
            Node<T1, T2> next = current.getNextYright(); // guardamos el siguiente
            current.setNextYright(null); // desconectamos 'current' de la lista original
            current.setPreviousYLeft(null);

            if (sortedFirst == null) { //se interta current como el primer nodo de la sublista ordenada
                sortedFirst = current;
            } else {
                // Si va al inicio
                if (current.getKey().compareTo(sortedFirst.getKey()) < 0) {
                    current.setNextYright(sortedFirst);
                    sortedFirst.setPreviousYLeft(current);
                    sortedFirst = current;
                } else {

                    Node<T1, T2> temp = sortedFirst; // aqui se busca la posición dentro de la parte ordenada
                    while (temp.getNextYright() != null &&
                            current.getKey().compareTo(temp.getNextYright().getKey()) >= 0) {
                        temp = temp.getNextYright();
                    }
                    Node<T1, T2> after = temp.getNextYright();// se inserta después de 'temp'
                    temp.setNextYright(current);
                    current.setPreviousYLeft(temp);
                    if (after != null) {
                        current.setNextYright(after);
                        after.setPreviousYLeft(current);
                    }
                }
            }
            current = next;
        }

        first = sortedFirst;// Al finalizar, 'sortedFirst' es la nueva cabeza
        recomputeLastFromFirst();// Recomputamos 'last' caminando al final
    }


    private void recomputeLastFromFirst() { //Este metodo recalcula el puntero 'last' empezando desde 'first'.
        if (first == null) {
            last = null;
            return;
        }
        Node<T1, T2> cur = first;
        while (cur.getNextYright() != null) {
            cur = cur.getNextYright();
        }
        last = cur;
    }

    @Override
    public Iterator<Node<T1, T2>> iterator() {
        return new Iterator<Node<T1, T2>>() {
            Node<T1, T2> cur = first;

            @Override
            public boolean hasNext() {
                if (cur != null) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Node<T1, T2> next() {
                Node<T1, T2> n = cur;
                cur = cur.getNextYright();
                return n;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Node<T1, T2>> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Node<T1, T2>> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
}
