package com.example.foofmaps;

public class Plato {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private byte[] imagen; // Cambia el tipo de imagen a byte[]

    public Plato(String nombre, String descripcion, double precio, byte[] imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public byte[] getImagen() { // Cambia el tipo de retorno a byte[]
        return imagen;
    }

    // Puedes agregar setters si es necesario

    // Otros métodos y propiedades de la clase Plato
}

