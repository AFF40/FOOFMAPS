package com.example.foofmaps.dueño;

import java.io.Serializable;

public class get_restaurante implements Serializable {
    private int restauranteId;
    private String nomRest;
    private int celular;
    private String ubicacion;
    private String estado;
    private String imagen;
    private int userId;

    public get_restaurante(int restauranteId, String nomRest, int celular, String ubicacion, String estado, String imagen, int userId) {
        this.restauranteId = restauranteId;
        this.nomRest = nomRest;
        this.celular = celular;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.imagen = imagen;
        this.userId = userId;
    }

    public int getRestauranteId() {
        return restauranteId;
    }

    public String getNomRest() {
        return nomRest;
    }

    public int getCelular() {
        return celular;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getImagen() {
        return imagen;
    }

    public int getUserId() {
        return userId;
    }
}
