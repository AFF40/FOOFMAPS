package com.example.foofmaps.clientes.restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foofmaps.R;

public class Editaresteplato extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        int platoId = intent.getIntExtra("id_comida", 0);
        Log.d("plato_id_editaresteplato", String.valueOf(platoId));
        int restauranteId = intent.getIntExtra("restaurante_id", 0);
        String nombre = intent.getStringExtra("nombre");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editaresteplato);
    }
}