package com.example.foofmaps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Importar la clase Log
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import androidx.appcompat.app.AppCompatActivity;

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

        // Agregar mensajes de depuración
        Log.d("ID_DEBUG", "restaurante_id: " + restaurante_id);

        // Obtener referencias a los elementos en el diseño
        TextView textViewRestauranteId = findViewById(R.id.textViewRestauranteId);
        TextView bannertop = findViewById(R.id.bannertop);
        TextView textViewCelular = findViewById(R.id.textViewCelular);
        ImageButton whatsappButton = findViewById(R.id.btnWhatsApp);
        ImageView imageViewRestaurante = findViewById(R.id.imageViewRestaurante);

        // Establecer los valores en los TextView
        textViewRestauranteId.setText("ID del Restaurante: " + restaurante_id);
        bannertop.setText(nom_rest);
        textViewCelular.setText("Número de Teléfono: " + celular);

        // Configurar el botón para abrir WhatsApp con el número de teléfono
        whatsappButton.setOnClickListener(v -> openWhatsApp(celular));

        // Construir la URL para obtener la imagen
        String imageUrl = "http://192.168.1.3/modelo/icono_rest.php?id=" + restaurante_id;

        // Agregar mensaje de depuración para verificar la URL
        Log.d("URL_DEBUG", "imageUrl: " + imageUrl);

        // Cargar la imagen desde tu servidor utilizando Picasso
        Picasso.get().load(imageUrl).into(imageViewRestaurante);
    }

    // Método para abrir WhatsApp con el número de teléfono
    private void openWhatsApp(int phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
