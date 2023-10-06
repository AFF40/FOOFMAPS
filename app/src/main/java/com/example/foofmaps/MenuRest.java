package com.example.foofmaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MenuRest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rest);

        // Obtener los extras del Intent
        Intent intent = getIntent();
        int restaurante_id = intent.getIntExtra("restaurant_id", 0);
        String nom_rest = intent.getStringExtra("restaurant_name");
        int celular = intent.getIntExtra("restaurant_phone", 0);

        // Obtener referencias a los TextView en el diseño
        TextView textViewRestauranteId = findViewById(R.id.textViewRestauranteId);
        androidx.appcompat.widget.Toolbar bannertop = findViewById(R.id.bannertop);
        TextView textViewCelular = findViewById(R.id.textViewCelular);

        // Establecer los valores en los TextView
        textViewRestauranteId.setText("ID del Restaurant: " + restaurante_id);
        bannertop.setTitle(nom_rest);
        textViewCelular.setText("Número de Teléfono: " + celular);
    }
}
