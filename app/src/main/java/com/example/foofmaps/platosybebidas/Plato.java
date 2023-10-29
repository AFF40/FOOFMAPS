package com.example.foofmaps.platosybebidas;

import android.util.Log;

public class Plato {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private byte[] imagen;
    private int disponible;

    public Plato(int id_comida, String nombre, String descripcion, double precio, byte[] imagen, int disponible) {
        this.id = id_comida;
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

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }


    // Puedes agregar setters si es necesario

    // Otros métodos y propiedades de la clase Plato
}

