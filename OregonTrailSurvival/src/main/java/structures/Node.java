package structures;

public class Node<T1 extends Comparable<T1>, T2> implements Comparable<Node<T1, T2>> {
    //T1 key es la clave del nodo (debe ser comparable)
    //T2 value es el valor asociado al nodo

    private Node<T1, T2> nextYright;       // Referencia al siguiente nodo (o hijo derecho)
    private Node<T1, T2> previousYLeft;    // Referencia al anterior nodo (o hijo izquierdo)
    private T2 value;                      // Valor almacenado
    private T1 key;                        // Clave del nodo (base de comparación)


    public Node(T2 value, T1 key) {
        this.value = value;
        this.key = key;
    }

    public Node<T1, T2> getNextYright() {
        return nextYright;
    }

    public void setNextYright(Node<T1, T2> nextYright) {
        this.nextYright = nextYright;
    }

    public Node<T1, T2> getPreviousYLeft() {
        return previousYLeft;
    }

    public void setPreviousYLeft(Node<T1, T2> previousYLeft) {
        this.previousYLeft = previousYLeft;
    }

    public T2 getValue() {
        return value;
    }

    public void setValue(T2 value) {
        this.value = value;
    }

    public T1 getKey() {
        return key;
    }

    public void setKey(T1 key) {
        this.key = key;
    }

    @Override
    public int compareTo(Node<T1, T2> other) {
        if (other == null) {
            return 1; // cualquier nodo es mayor que null
        }
        return this.key.compareTo(other.key);
    }
}
