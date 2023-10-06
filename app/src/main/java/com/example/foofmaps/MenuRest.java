package com.example.foofmaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

        // Obtener referencias a los TextView y al botón en el diseño
        TextView textViewRestauranteId = findViewById(R.id.textViewRestauranteId);
        Toolbar bannertop = findViewById(R.id.bannertop);
        TextView textViewCelular = findViewById(R.id.textViewCelular);
        ImageButton whatsappButton = findViewById(R.id.btnWhatsApp);

        // Establecer los valores en los TextView
        textViewRestauranteId.setText("ID del Restaurant: " + restaurante_id);
        bannertop.setTitle(nom_rest);
        textViewCelular.setText("Número de Teléfono: " + celular);

        // Configurar el botón para abrir WhatsApp con el número de teléfono
        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(celular);
            }
        });
    }

    // Método para abrir WhatsApp con el número de teléfono
    private void openWhatsApp(int phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
