package com.example.foofmaps.platosybebidas;

import android.util.Log;

public class editplato {
    private int id_comida;
    private String nombre_plato;
    private String descripcion_plato;
    private double precio_plato;
    private byte[] imagen_plato; // Cambia el tipo de imagen a byte[]
    private int disponible_plato;

    public editplato( int id_comida,String nombre_plato, String descripcion_plato, double precio_plato, byte[] imagen_plato, int disponible_plato) {
        this.id_comida = id_comida;
        this.nombre_plato = nombre_plato;
        this.descripcion_plato = descripcion_plato;
        this.precio_plato = precio_plato;
        this.imagen_plato = imagen_plato;
        this.disponible_plato = disponible_plato;
        Log.d("PLATO_DEBUGaa", "nombre: " + nombre_plato);
    }

    public int getid_comida() {
        return id_comida;
    }
    public String getnombre_plato() {
        return nombre_plato;
    }

    public String getdescripcion_plato() {
        return descripcion_plato;
    }

    public double getprecio_plato() {
        return precio_plato;
    }

    public byte[] getimagen_plato() { // Cambia el tipo de retorno a byte[]
        return imagen_plato;
    }
    public int getdisponible_plato() {
        return disponible_plato;
    }

    // Puedes agregar setters si es necesario

    // Otros métodos y propiedades de la clase Plato
}

