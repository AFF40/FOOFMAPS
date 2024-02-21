package com.example.foofmaps;

public class Restaurante {
    private int restauranteId;
    private int celular;
    private String nomRest;

    private String imagen;

    public Restaurante(int restauranteId, int celular, String nomRest, String imagen) {
        this.restauranteId = restauranteId;
        this.celular = celular;
        this.nomRest = nomRest;
        this.imagen = imagen;
    }

    public int getRestauranteId() {
        return restauranteId;
    }

    public int getCelular() {
        return celular;
    }

    public String getNomRest() {
        return nomRest;
    }

    public String getImagen() {
        return imagen;
    }
}
