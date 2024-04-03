
package com.example.foofmaps.dueño;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.bebidas_rest;
import com.example.foofmaps.clientes.restaurantes.platos_rest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        String imagen = intent.getStringExtra("restaurant_image");
        //convertir el localhost a la ip de la maquina
        imagen = imagen.replace("http://localhost", "http://192.168.100.5");
        Log.d("RESTAURANT_ID", String.valueOf(restaurante_id));
        Log.d("RESTAURANT_NAME", nom_rest);
        Log.d("RESTAURANT_CELULAR", String.valueOf(celular));
        Log.d("RESTAURANT_IMAGE", imagen);

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
                .load(imagen) // Aquí debes poner la URL correcta del servidor
                .into(imageViewRestaurante);

        // Inflar el fragmento platos_rest en el contenedor contenedorlista
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Crear una instancia del fragmento platos_rest
        platosFragment = new platos_rest();

        // Crear un Bundle para pasar el restaurante_id al fragmento
        Bundle args = new Bundle();
        args.putInt("restaurant_id", restaurante_id);
        platosFragment.setArguments(args);

        // Agregar el fragmento al contenedor contenedorlista
        fragmentTransaction.replace(R.id.contenedorlista, platosFragment);
        fragmentTransaction.commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.platosybebidas);

        // Configurar un OnNavigationItemSelectedListener en el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            args.putInt("restaurant_id", restaurante_id);
            switch (item.getItemId()) {
                case R.id.comidas: // Reemplaza esto con el id de tu elemento de menú para platos
                    if (platosFragment == null) {
                        platosFragment = new platos_rest();
                        platosFragment.setArguments(args);
                    }
                    fragment = platosFragment;
                    break;
                case R.id.bebidas: // Reemplaza esto con el id de tu elemento de menú para bebidas
                    // Crear una instancia del fragmento bebidas_rest si aún no existe
                    if (bebidasFragment == null) {
                        bebidasFragment = new bebidas_rest();
                        bebidasFragment.setArguments(args);
                    }
                    fragment = bebidasFragment;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            // Reemplaza el fragmento en el contenedor contenedorlista
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedorlista, fragment).commit();
            return true;
        });
    }



    private void openWhatsApp(String celular) {
        String url = "https://wa.me/591" + celular;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}