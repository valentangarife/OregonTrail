package model;

import enums.TIPORECURSO;

import java.util.ArrayList;
import java.util.List;

public class Inventario {

    private int capacidadMaxima;
    private List<Recurso> recursos;

    // Constructor para inventarios vacíos
    public Inventario(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.recursos = new ArrayList<>();
    }

    // Constructor completo
    public Inventario(int capacidadMaxima, List<Recurso> recursos) {
        this.capacidadMaxima = capacidadMaxima;
        if (recursos == null) {
            this.recursos = new ArrayList<>();
        } else {
            this.recursos = recursos;
        }
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        if (recursos != null) {
            this.recursos = recursos;
        }
    }

    //metodos
    public boolean agregarRecurso(Recurso recurso) { //agrupa por tipo y respeta la capacidad

        if (recurso == null) {
            return false;
        }

        // Si hay espacio aún para agregar (solo cuenta como 1 ítem por tipo nuevo)
        boolean existe = false;

        for (Recurso r : recursos) {
            if (r.getTipo() == recurso.getTipo()) {
                r.aumentarCantidad(recurso.getCantidad());
                existe = true;
                break;
            }
        }

        // Si NO existía el tipo de recurso, debemos verificar capacidad
        if (!existe) {
            if (recursos.size() >= capacidadMaxima) {
                return false; // inventario lleno
            }
            recursos.add(recurso);
        }

        return true;
    }

    public boolean eliminarRecurso(TIPORECURSO tipo, int cantidad) {

        if (tipo == null || cantidad <= 0) {
            return false;
        }

        for (Recurso r : recursos) {
            if (r.getTipo() == tipo) {
                r.usarRecurso(cantidad);

                if (r.getCantidad() == 0) {
                    recursos.remove(r);
                }
                return true;
            }
        }

        return false; // no se encontró ese tipo
    }


    public int obtenerCantidad(TIPORECURSO tipo) {

        if (tipo == null) {
            return 0;
        }

        for (Recurso r : recursos) {
            if (r.getTipo() == tipo) {
                return r.getCantidad();
            }
        }

        return 0; // significa que no existe ese tipo
    }


    public void agregarComida(int cantidad) {
        if (cantidad <= 0) return;
        agregarRecurso(new Recurso(TIPORECURSO.COMIDA, cantidad));
    }

    public void agregarMunicion(int cantidad) {
        if (cantidad <= 0) return;
        agregarRecurso(new Recurso(TIPORECURSO.MUNICION, cantidad));
    }

    public void agregarMedicina(int cantidad) {
        if (cantidad <= 0) return;
        agregarRecurso(new Recurso(TIPORECURSO.MEDICINA, cantidad));
    }


    public void add(Recurso r) {
    }
}
