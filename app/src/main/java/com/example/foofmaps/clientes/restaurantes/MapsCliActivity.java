package com.example.foofmaps.clientes.restaurantes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.fragments.MapsCliFragment;
import com.example.foofmaps.clientes.restaurantes.fragments.SearchFragment;
import com.example.foofmaps.clientes.restaurantes.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsCliActivity extends AppCompatActivity {

    private MapsCliFragment mapsCliFragment;
    private SearchFragment searchFragment;
    private SettingsFragment settingsFragment;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inicializar los fragmentos
        mapsCliFragment = new MapsCliFragment();
        searchFragment = new SearchFragment();
        settingsFragment = new SettingsFragment();

        // Agregar los fragmentos al contenedor solo si no hay una instancia guardada
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_cli, mapsCliFragment, "MapsFragment")
                    .add(R.id.fragment_container_cli, searchFragment, "SearchFragment")
                    .hide(searchFragment)
                    .add(R.id.fragment_container_cli, settingsFragment, "SettingsFragment")
                    .hide(settingsFragment)
                    .commit();
        }

        // Configurar la navegación con BottomNavigationView para cambiar entre fragmentos y ocultarlos o mostrarlos según sea necesario
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked }, // estado cuando el item está seleccionado
                new int[] { -android.R.attr.state_checked } // estado cuando el item no está seleccionado
        };

        int[] colors = new int[] {
                Color.WHITE, // color cuando el item está seleccionado
                Color.TRANSPARENT // color cuando el item no está seleccionado
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigationView.setItemTextColor(colorStateList);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //el texto de los items debe ser igual a los ids de los fragments
            switch (item.getItemId()) {
                case R.id.maps:
                    transaction.hide(searchFragment)
                            .hide(settingsFragment)
                            .show(mapsCliFragment);
                    break;

                case R.id.ajustes:
                    transaction.hide(mapsCliFragment)
                            .hide(searchFragment)
                            .show(settingsFragment);
                    break;
            }
            transaction.commit();
            return true;
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // Si se presiona de nuevo dentro de los 2 segundos, finalizar la actividad
                    finishAffinity(); // Finaliza la actividad
                } else {
                    doubleBackToExitPressedOnce = true;
                    // Mostrar un mensaje de advertencia
                    Toast.makeText(MapsCliActivity.this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        });

    }
}
