package model;

public class Logro implements Comparable<Logro> {

    private int id; // clave para el BST
    private String nombre;
    private String descripcion;
    private boolean obtenido; //true = ya lo consiguió
    private int puntos; //puntaje del logro

    public Logro(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.obtenido = false;
        this.puntos = 0;
    }

    public Logro(int id, String nombre, String descripcion, int puntos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.obtenido = false;
        this.puntos = puntos;
    }

    // Getters - Setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isObtenido() { return obtenido; }
    public int getPuntos() { return puntos; }

    public void setObtenido(boolean obtenido) {
        this.obtenido = obtenido;
    }

    public void marcarComoObtenido() {
        this.obtenido = true;
    }

    @Override
    public int compareTo(Logro o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        String estado = obtenido ? "✔ OBTENIDO" : "✖ PENDIENTE";
        return "[" + id + "] " + nombre + " - " + estado;
    }
}
