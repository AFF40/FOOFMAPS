package com.example.foofmaps.clientes.restaurantes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.foofmaps.R;

public class MenuRest extends AppCompatActivity {

    private platos_rest platosFragment;
    private bebidas_rest bebidasFragment;
    private int restaurante_id;  // Almacenar el restaurante_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rest);

        // Obtener los extras del Intent
        Intent intent = getIntent();
        restaurante_id = intent.getIntExtra("restaurant_id", 0);
        String nom_rest = intent.getStringExtra("restaurant_name");
        int celular = intent.getIntExtra("restaurant_phone", 0);
        String modeloURL = intent.getStringExtra("image_url"); // Obtener la URL de la imagen
        Log.d("RESTAURANT_ID", String.valueOf(restaurante_id));
        Log.d("RESTAURANT_NAME", nom_rest);
        Log.d("RESTAURANT_CELULAR", String.valueOf(celular));

        // Obtener referencias a los elementos en el diseño
        TextView bannertop = findViewById(R.id.bannertop);
        ImageButton whatsappButton = findViewById(R.id.btnWhatsApp);
        ImageView imageViewRestaurante = findViewById(R.id.imageViewRestaurante);

        // Establecer los valores en los TextView
        bannertop.setText(nom_rest);

        // Configurar el botón para abrir WhatsApp con el número de teléfono
        whatsappButton.setOnClickListener(v -> openWhatsApp(String.valueOf(celular)));

        // Cargar la imagen desde tu servidor utilizando Glide
        Glide.with(this)
                .load(modeloURL)
                .into(imageViewRestaurante);
    }

    private void loadFragment(Fragment fragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("restaurant_id", restaurante_id);  // Pasa el restaurante_id al fragmento
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorlista, fragment, tag)
                .commit();
    }

    private void openWhatsApp(String celular) {
        String url = "https://wa.me/591" + celular;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}