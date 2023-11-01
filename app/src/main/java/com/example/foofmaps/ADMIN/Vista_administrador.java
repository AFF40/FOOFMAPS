package com.example.foofmaps.ADMIN;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Vista_administrador extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_administrador);

        // Obtén una referencia al BottomNavigationView
        bottomNavigationView = findViewById(R.id.platosybebidas);

        // Configura un oyente para manejar los cambios en la selección de elementos
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                // Verifica el elemento seleccionado
                if (item.getItemId() == R.id.registro_admin) {
                    fragment = new add_rest();
                } else if (item.getItemId() == R.id.ajustes_admin) {
                    fragment = new ajustes_admin();
                }

                // Reemplaza el fragmento en el contenedor
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_admin, fragment)
                            .commit();
                }

                return true;
            }
        });

        // Establece el fragmento "add_rest" como el fragmento inicial
        Fragment initialFragment = new add_rest();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_admin, initialFragment)
                .commit();
    }
}