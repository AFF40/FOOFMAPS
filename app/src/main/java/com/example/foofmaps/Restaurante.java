package com.example.foofmaps;

public class Restaurante {
    private int restauranteId;
    private int celular;
    private String nomRest;

    public Restaurante(int restauranteId, int celular, String nomRest) {
        this.restauranteId = restauranteId;
        this.celular = celular;
        this.nomRest = nomRest;
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
}
