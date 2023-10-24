package com.example.foofmaps.platosybebidas;

import android.util.Log;

public class editbebida {
    private int id_bebida;
    private String nombre_bebida;
    private String descripcion_bebida;
    private double precio_bebida;
    private byte[] imagen_bebida; // Cambia el tipo de imagen a byte[]
    private int disponible_bebida;

    public editbebida( int id_bebida,String nombre_bebida, String descripcion_bebida, double precio_bebida, byte[] imagen_bebida, int disponible_bebida) {
        this.id_bebida = id_bebida;
        this.nombre_bebida = nombre_bebida;
        this.descripcion_bebida = descripcion_bebida;
        this.precio_bebida = precio_bebida;
        this.imagen_bebida = imagen_bebida;
        this.disponible_bebida = disponible_bebida;
        Log.d("PLATO_DEBUGaa", "nombre: " + nombre_bebida);
    }

    public int getId_bebida() {
        return id_bebida;
    }
    public String getNombre_bebida() {
        return nombre_bebida;
    }

    public String getDescripcion_bebida() {
        return descripcion_bebida;
    }

    public double getPrecio_bebida() {
        return precio_bebida;
    }

    public byte[] getImagen_bebida() { // Cambia el tipo de retorno a byte[]
        return imagen_bebida;
    }
    public int getDisponible_bebida() {
        return disponible_bebida;
    }

    // Puedes agregar setters si es necesario

    // Otros métodos y propiedades de la clase Plato
}

