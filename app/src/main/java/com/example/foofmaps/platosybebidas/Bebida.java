package com.example.foofmaps.platosybebidas;

import android.util.Log;

public class Bebida {
    private int id_bebida;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagen; // Cambia el tipo de imagen a byte[]
    private int disponible;
    private int restaurante_id;
    private String nombre_restaurante;

    public Bebida(int id_bebida,String nombre, String descripcion, double precio, String imagen, int disponible, int restaurante_id, String nombre_restaurante) {
        this.id_bebida = id_bebida;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.disponible = disponible;
        this.restaurante_id = restaurante_id;
        this.nombre_restaurante = nombre_restaurante;
        Log.d("PLATO_DEBUGaa", "nombre: " + nombre);
    }

    public int getId() {
        return id_bebida;
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

    public String getImagen() { // Cambia el tipo de retorno a byte[]
        return imagen;
    }
    public int getDisponible() {
        return disponible;
    }
    public int getRestaurante_id() {
        return restaurante_id;
    }
    public String getNombre_restaurante() {
        return nombre_restaurante;
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
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public void setRestaurante_id(int restaurante_id) {
        this.restaurante_id = restaurante_id;
    }
    public void setNombre_restaurante(String nombre_restaurante) {
        this.nombre_restaurante = nombre_restaurante;
    }

}

