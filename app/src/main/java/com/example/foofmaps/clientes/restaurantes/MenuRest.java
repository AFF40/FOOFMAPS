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

import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.restaurantes.bebidas_rest;
import com.example.foofmaps.restaurantes.platos_rest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

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

        // Construir la URL para obtener la imagen
        String modeloURL = Config.MODELO_URL+"icono_rest.php?id=" + restaurante_id;

        // Cargar la imagen desde tu servidor utilizando Picasso
        Picasso.get().load(modeloURL).into(imageViewRestaurante);

        // Inicializar los fragmentos si no están creados
        if (platosFragment == null) {
            platosFragment = new platos_rest();
        }
        if (bebidasFragment == null) {
            bebidasFragment = new bebidas_rest();
        }

        // Mostrar el fragmento de platos_rest por defecto
        if (getSupportFragmentManager().findFragmentByTag("platosFragmentTag") == null) {
            loadFragment(platosFragment, "platosFragmentTag");
        }


        // Configurar el BottomNavigationView y establecer un oyente
        BottomNavigationView bottomNavigationView = findViewById(R.id.platosybebidas);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.comidas:
                    if (getSupportFragmentManager().findFragmentByTag("platosFragmentTag") == null) {
                        loadFragment(platosFragment, "platosFragmentTag");
                    }
                    break;
                case R.id.bebidas:
                    if (getSupportFragmentManager().findFragmentByTag("bebidasFragmentTag") == null) {
                        loadFragment(bebidasFragment, "bebidasFragmentTag");
                    }
                    break;
            }
            return true;
        });
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
