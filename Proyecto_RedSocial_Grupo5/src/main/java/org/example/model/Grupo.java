package org.example.model;

import javafx.scene.paint.Color;

// Clase que representa un grupo dentro de la red social.
// Un grupo permite organizar usuarios y asignarles una categoría visual mediante un color específico.
public class Grupo {

    //Atributos
    private String nombre;
    private Color color;

    // Constructor de la clase Grupo.
    public Grupo(String nombre, Color color) {
        this.nombre = nombre;
        this.color = color;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Representación en texto del objeto Grupo.
    @java.lang.Override
    public java.lang.String toString() {
        return "Grupo{" +
                "nombre='" + nombre + '\'' +
                ", color=" + color +
                '}';
    }
}
