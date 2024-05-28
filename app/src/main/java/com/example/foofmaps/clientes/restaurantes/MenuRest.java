package com.example.foofmaps.clientes.restaurantes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.fragments.bebidas_rest;
import com.example.foofmaps.clientes.restaurantes.fragments.platos_rest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuRest extends AppCompatActivity {
    private platos_rest platosFragment;
    private bebidas_rest bebidasFragment;
    private int restaurante_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rest);

        Intent intent = getIntent();
        restaurante_id = intent.getIntExtra("restaurant_id", 0);
        String nom_rest = intent.getStringExtra("restaurant_name");
        int celular = intent.getIntExtra("restaurant_phone", 0);
        String imagen = intent.getStringExtra("restaurant_image");

        TextView bannertop = findViewById(R.id.bannertop);
        ImageButton whatsappButton = findViewById(R.id.btnWhatsApp);
        ImageView imageViewRestaurante = findViewById(R.id.imageViewRestaurante);

        bannertop.setText(nom_rest);
        whatsappButton.setOnClickListener(v -> openWhatsApp(String.valueOf(celular)));
        Glide.with(this)
                .load(imagen)
                .into(imageViewRestaurante);

        if (savedInstanceState == null) {
            platosFragment = new platos_rest();
            Bundle args = new Bundle();
            args.putInt("restaurant_id", restaurante_id);
            platosFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorlista, platosFragment)
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.platosybebidas);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.comidas:
                    if (platosFragment == null) {
                        platosFragment = new platos_rest();
                        Bundle args = new Bundle();
                        args.putInt("restaurant_id", restaurante_id);
                        platosFragment.setArguments(args);
                    }
                    fragment = platosFragment;
                    break;
                case R.id.bebidas:
                    if (bebidasFragment == null) {
                        bebidasFragment = new bebidas_rest();
                        Bundle args = new Bundle();
                        args.putInt("restaurant_id", restaurante_id);
                        bebidasFragment.setArguments(args);
                    }
                    fragment = bebidasFragment;
                    break;
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contenedorlista, fragment)
                        .commit();
            }
            return true;
        });
    }

    private void openWhatsApp(String celular) {
        String url = "https://wa.me/591" + celular;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
