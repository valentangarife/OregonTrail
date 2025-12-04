package model;

import structures.Generic_BST;
import structures.Node;
import java.util.ArrayList;
import java.util.List;

public class ArbolLogros {

    private Generic_BST<Integer, Logro> bst;

    public ArbolLogros() {
        bst = new Generic_BST<>();
    }

    public Generic_BST<Integer, Logro> getBst() {
        return bst;
    }

    //principalmente: agregar, buscar, marcar como obtenido, eliminar

    public void agregarLogro(Logro logro) {
        if (logro == null) return;
        bst.insert(logro.getId(), logro);
    }

    public Logro buscarLogroPorId(int id) {
        return bst.search(id);
    }

    public boolean marcarLogroComoObtenido(int id) {
        Logro l = bst.search(id);
        if (l == null) return false;

        if (!l.isObtenido()) {
            l.marcarComoObtenido();
        }
        return true;
    }

    public Logro eliminarLogro(int id) {
        return bst.delete(id);
    }


    public List<Logro> obtenerLogrosEnOrden() {
        List<Logro> lista = new ArrayList<>();
        recorrerInOrder(bst, lista);
        return lista;
    }


    public String obtenerIdsEnOrden() {
        return bst.inOrder();   // retorna "1 2 3"
    }

    // metodo auxiliar — porque nuestro Generic_BST solo nos devuelve string
    private void recorrerInOrder(Generic_BST<Integer, Logro> tree, List<Logro> lista) {
        recorrer(tree, lista, tree.getRootNode());
    }

    private void recorrer(Generic_BST<Integer, Logro> tree, List<Logro> lista, Node<Integer, Logro> node) {
        if (node == null) return;

        recorrer(tree, lista, node.getPreviousYLeft());   // izquierda
        lista.add(node.getValue());                       // nodo actual
        recorrer(tree, lista, node.getNextYright());      // derecha
    }

}
