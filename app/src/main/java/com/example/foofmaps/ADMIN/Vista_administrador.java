package com.example.foofmaps.ADMIN;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Vista_administrador extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_administrador);

        // Obtener el id_usuario del Intent
        int id_usuario = getIntent().getIntExtra("id_usuario", -1);

        // Usar el id_usuario según tus necesidades
        Log.d("Log_VistaAdministrador", "ID Usuario: " + id_usuario);

        // Obtén una referencia al BottomNavigationView
        bottomNavigationView = findViewById(R.id.platosybebidas);

        // Configura un oyente para manejar los cambios en la selección de elementos
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                // Verifica el elemento seleccionado
                if (item.getItemId() == R.id.registro_admin) {
                    fragment = new add_rest(id_usuario);

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

        // Establece el fragmento "add_rest" como el fragmento inicial y enviar el id_usuario
        Fragment initialFragment = new add_rest(id_usuario);
        Log.d("Log_VistaAdministrador_idenv", "ID Usuario: " + id_usuario);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_admin, initialFragment)
                .commit();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // Si se presiona de nuevo dentro de los 2 segundos, finalizar la actividad
                    finishAffinity(); // Finaliza la actividad
                } else {
                    doubleBackToExitPressedOnce = true;
                    // Mostrar un mensaje de advertencia
                    Toast.makeText(Vista_administrador.this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        });
    }
}