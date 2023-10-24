package com.example.foofmaps.platosybebidas;

import android.util.Log;

public class Bebida {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private byte[] imagen; // Cambia el tipo de imagen a byte[]
    private int disponible;

    public Bebida(String nombre, String descripcion, double precio, byte[] imagen, int disponible) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.disponible = disponible;
        Log.d("PLATO_DEBUGaa", "nombre: " + nombre);
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
    public int getDisponible() {
        return disponible;
    }

}

