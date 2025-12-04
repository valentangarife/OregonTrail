package model;

import enums.TIPORECURSO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Escenario {

    private String nombre;
    private String descripcion;
    private int dificultad;
    private int ancho;
    private int alto;
    private List<Recurso> recursosDisponibles;
    private List<Enemigo> enemigos;
    private List<PuntoAcceso> puntosAcceso;
    private ArrayList<Recurso> recursos = new ArrayList<>();

    // Constructor actualizado
    public Escenario(String nombre, String descripcion, int dificultad, int ancho, int alto,
                     List<Recurso> recursosDisponibles, List<Enemigo> enemigos, List<PuntoAcceso> puntosAcceso) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.dificultad = dificultad;
        this.ancho = ancho;  // Nuevo
        this.alto = alto;    // Nuevo

        if (recursosDisponibles == null) {
            this.recursosDisponibles = new ArrayList<>();
        } else {
            this.recursosDisponibles = recursosDisponibles;
        }

        if (enemigos == null) {
            this.enemigos = new ArrayList<>();
        } else {
            this.enemigos = enemigos;
        }

        if (puntosAcceso == null) {
            this.puntosAcceso = new ArrayList<>();
        } else {
            this.puntosAcceso = puntosAcceso;
        }
    }

    public Escenario(String ríoColumbia, String descripcion, int dificultad, ArrayList<Object> objects, ArrayList<Object> objects1, ArrayList<Object> objects2) {
    }

    // GETTERS y SETTERS existentes (sin cambios)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getDificultad() { return dificultad; }
    public void setDificultad(int dificultad) { this.dificultad = dificultad; }
    public List<Recurso> getRecursosDisponibles() { return recursosDisponibles; }
    public void setRecursosDisponibles(List<Recurso> recursosDisponibles) { if (recursosDisponibles != null) this.recursosDisponibles = recursosDisponibles; }
    public List<Enemigo> getEnemigos() { return enemigos; }
    public void setEnemigos(List<Enemigo> enemigos) { if (enemigos != null) this.enemigos = enemigos; }
    public List<PuntoAcceso> getPuntosAcceso() { return puntosAcceso; }
    public void setPuntosAcceso(List<PuntoAcceso> puntosAcceso) { if (puntosAcceso != null) this.puntosAcceso = puntosAcceso; }

    // Nuevos GETTERS y SETTERS para ancho y alto
    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }
    public ArrayList<Recurso> getRecursos() {
        return recursos;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    // Métodos existentes (con una pequeña actualización en generarEnemigos)
    public void generarRecurso() {
        if (recursosDisponibles == null) {
            recursosDisponibles = new ArrayList<>();
        }

        Random random = new Random();
        TIPORECURSO[] tipos = TIPORECURSO.values();
        int index = random.nextInt(tipos.length);
        TIPORECURSO tipo = tipos[index];
        int cantidadBase = 1 + dificultad * 2;
        int variacion = random.nextInt(3);
        int cantidad = cantidadBase + variacion;
        Recurso recurso = new Recurso(tipo, cantidad);
        recursosDisponibles.add(recurso);
    }

    public void generarEnemigos() {
        if (enemigos == null) {
            enemigos = new ArrayList<>();
        }

        Random random = new Random();
        int cantidadEnemigos = 1 + dificultad;

        for (int i = 0; i < cantidadEnemigos; i++) {
            String id = nombre + "_ENEMIGO_" + (enemigos.size() + 1);
            int salud = 20 + dificultad * 10;
            int dano = 3 + dificultad * 2;

            // Usar ancho y alto en lugar de 10 hardcodeado
            int posX = random.nextInt(ancho);  // Ahora usa this.ancho
            int posY = random.nextInt(alto);   // Ahora usa this.alto

            Enemigo enemigo = new Enemigo(id, nombre, salud, dano, posX, posY);
            enemigos.add(enemigo);
        }
    }

    public PuntoAcceso obtenerPuntoAcceso(String destino) {
        if (destino == null || puntosAcceso == null) {
            return null;
        }

        for (PuntoAcceso puntoA : puntosAcceso) {
            if (puntoA != null && puntoA.getDestino() != null &&
                    puntoA.getDestino().getNombre() != null &&
                    puntoA.getDestino().getNombre().equalsIgnoreCase(destino)) {
                return puntoA;
            }
        }

        return null;
    }

    public String getNumero() {
        String o = null;
        return o;
    }
}