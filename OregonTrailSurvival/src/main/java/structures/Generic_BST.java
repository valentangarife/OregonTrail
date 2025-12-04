package structures;

/**
 * Árbol Binario de Búsqueda genérico.
 * T1 = clave (Comparable), T2 = valor asociado.
 * Convención estándar: menor ->izquierda; mayor -> derecha.
 */
public class Generic_BST<T1 extends Comparable<T1>, T2> {
    private Node<T1, T2> root; // Raíz del árbol


    public Node<T1, T2> getRootNode() {
        return root;
    }

    // Inserta una (key, value) en el BST, Si la clave ya existe, no inserta
    public void insert(T1 key, T2 value) {
        if (root == null) { // árbol vacío
            root = new Node<T1, T2>(value, key);
        } else {
            Node<T1, T2> node = new Node<T1, T2>(value, key);
            insert(root, node);
        }
    }


    private void insert(Node<T1, T2> pointer, Node<T1, T2> newNode) {
        if (newNode.getKey().compareTo(pointer.getKey()) < 0) { // ir hacia el subárbol izquierdo
            if (pointer.getPreviousYLeft() == null) {
                pointer.setPreviousYLeft(newNode);
            } else {
                insert(pointer.getPreviousYLeft(), newNode); // nueva recursión hacia el nivel inferior del left
            }
        } else if (newNode.getKey().compareTo(pointer.getKey()) > 0) { // ir hacia el subárbol derecho
            if (pointer.getNextYright() == null) {
                pointer.setNextYright(newNode);
            } else {
                insert(pointer.getNextYright(), newNode); // nueva recursión hacia el nivel inferior del right
            }
        } else {
            return; // si la clave ya existe, no se insertan duplicados
        }
    }

    public String inOrder() {
        String result = inorder(root); //Recorre el árbol en-orden y retorna las claves separadas por espacios

        return result.trim(); //Usa la función recursiva inorder(Node)
    }

    // Recorre el árbol en orden ascendente (izquierda → nodo → derecha) y construye una cadena con todas las claves separadas
    private String inorder(Node<T1, T2> pointer) {
        // Iniciamos una cadena vacía para ir acumulando los valores del recorrido
        String s = "";

        // Si el puntero actual NO es nulo, significa que hay un nodo por procesar
        if (pointer != null) {

            s = s + inorder(pointer.getPreviousYLeft()); //Llamamos recursivamente al subárbol izquierdo, Esto asegura que primero se visiten los nodos con claves menores.
            s = s + (pointer.getKey() + " "); //Agregamos la clave (key) del nodo actual a la cadena, más un espacio para separar los valores.
            s = s + inorder(pointer.getNextYright()); //Llamamos recursivamente al subárbol derecho, Esto agrega los nodos con claves mayores.
        }
        return s; //Retornamos la cadena con las claves acumuladas hasta el momento.
    }


    //Este metodo Busca por clave y retorna el valor asociado (o null si no existe).
    public T2 search(T1 val) {
        return search(root, val);
    }

    // Búsqueda recursiva por clave.
    private T2 search(Node<T1, T2> pointer, T1 val) {
        if (pointer == null) { // el nodo no se encontró
            return null;
        } else if (pointer.getKey().compareTo(val) == 0) { // encontramos el nodo
            return pointer.getValue();
        } else if (val.compareTo(pointer.getKey()) < 0) { // debemos buscar a la izquierda
            return search(pointer.getPreviousYLeft(), val);
        } else { // debemos buscar a la derecha
            return search(pointer.getNextYright(), val);
        }
    }

    // este metodo elimina un nodo por clave y retorna el valor eliminado (o null si no estaba).
    public T2 delete(T1 value) {
        return delete(null, root, value);
    }

    /**
     * Eliminación recursiva con referencia al padre para re-enlazar.
     * Casos:
     *   1) Sin hijos
     *   2) Solo hijo izquierdo
     *   3) Solo hijo derecho
     *   4) Dos hijos (se reemplaza por el sucesor: mínimo del subárbol derecho)
     */
    private T2 delete(Node<T1, T2> parent, Node<T1, T2> pointer, T1 value) {
        if (pointer == null) { // caso base si no lo encontramos
            return null;
        }

        int cmp = value.compareTo(pointer.getKey());

        if (cmp < 0) { // entonces buscamos a la izquierda
            return delete(pointer, pointer.getPreviousYLeft(), value);
        } else if (cmp > 0) { // si no buscamos a la derecha
            return delete(pointer, pointer.getNextYright(), value);
        } else {
            // y si encontramos el nodo a eliminar
            T2 out = pointer.getValue();

            // 1) Caso: sin hijos
            if (pointer.getPreviousYLeft() == null && pointer.getNextYright() == null) {
                if (parent == null) { // era la raiz
                    root = null;
                } else {
                    if (parent.getPreviousYLeft() == pointer) {
                        parent.setPreviousYLeft(null);
                    } else {
                        parent.setNextYright(null);
                    }
                }
                return out;
            }

            // 2) Caso: solo hijo izquierdo
            if (pointer.getNextYright() == null && pointer.getPreviousYLeft() != null) {
                if (parent == null) { // era la raiz
                    root = pointer.getPreviousYLeft();
                } else {
                    if (parent.getPreviousYLeft() == pointer) {
                        parent.setPreviousYLeft(pointer.getPreviousYLeft());
                    } else {
                        parent.setNextYright(pointer.getPreviousYLeft());
                    }
                }
                return out;
            }

            // 3) Caso: solo hijo derecho
            if (pointer.getPreviousYLeft() == null && pointer.getNextYright() != null) {
                if (parent == null) { // era la raiz
                    root = pointer.getNextYright();
                } else {
                    if (parent.getPreviousYLeft() == pointer) {
                        parent.setPreviousYLeft(pointer.getNextYright());
                    } else {
                        parent.setNextYright(pointer.getNextYright());
                    }
                }
                return out;
            }

            // 4) Caso: dos hijos > usamos sucesor (mínimo del subárbol derecho)
            Node<T1, T2> sucesor = getSucesor(pointer); // el siguiente del puntero (mínimo del derecho)
            pointer.setKey(sucesor.getKey()); // Transferimos contenido (clave y valor) del sucesor al nodo actual
            pointer.setValue(sucesor.getValue());
            delete(pointer, pointer.getNextYright(), sucesor.getKey()); // Eliminamos el sucesor desde el subárbol derecho (el padre ahora es 'pointer')

            return out;
        }
    }

    ///Este metodo  retorna el nodo con la clave mínima en el subárbol cuyo tope es 'pointer' (Camina todo hacia la izquierda)
    private Node<T1, T2> getMin(Node<T1, T2> pointer) {
        Node<T1, T2> cur = pointer;
        while (cur.getPreviousYLeft() != null) {
            cur = cur.getPreviousYLeft();
        }
        return cur; //cur: current
    }


    //El sucesor in-order de 'pointer' es el mínimo del subárbol derecho.
    private Node<T1, T2> getSucesor(Node<T1, T2> pointer) {
        return getMin(pointer.getNextYright());
    }
}
