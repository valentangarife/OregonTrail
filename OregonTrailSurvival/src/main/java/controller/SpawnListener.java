package controller;

import model.Enemigo;
import model.Recurso;

public interface SpawnListener {
    void onEnemigoGenerado(Enemigo e);
    void onRecursoGenerado(Recurso r);
    void onRecursoRecogido(Recurso r);
}
