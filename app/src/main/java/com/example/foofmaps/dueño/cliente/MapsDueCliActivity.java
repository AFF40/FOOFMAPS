package com.example.foofmaps.dueño.cliente;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.foofmaps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapsDueCliActivity extends AppCompatActivity {

    private MapsDueCliFragment mapsDueCliFragment;
    private SearchDueCliFragment searchDueCliFragment;
    private SettingsDuenoCliFragment settingsDuenoCliFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inicializar los fragmentos
        mapsDueCliFragment = new MapsDueCliFragment();
        searchDueCliFragment = new SearchDueCliFragment();
        settingsDuenoCliFragment = new SettingsDuenoCliFragment();

        // Agregar los fragmentos al contenedor solo si no hay una instancia guardada
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_cli, mapsDueCliFragment, "MapsFragment")
                    .add(R.id.fragment_container_cli, searchDueCliFragment, "SearchFragment")
                    .hide(searchDueCliFragment)
                    .add(R.id.fragment_container_cli, settingsDuenoCliFragment, "SettingsFragment")
                    .hide(settingsDuenoCliFragment)
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
                    transaction.hide(searchDueCliFragment)
                            .hide(settingsDuenoCliFragment)
                            .show(mapsDueCliFragment);
                    break;
                case R.id.search:
                    transaction.hide(mapsDueCliFragment)
                            .hide(settingsDuenoCliFragment)
                            .show(searchDueCliFragment);
                    break;
                case R.id.ajustes:
                    transaction.hide(mapsDueCliFragment)
                            .hide(searchDueCliFragment)
                            .show(settingsDuenoCliFragment);
                    break;
            }
            transaction.commit();
            return true;
        });
    }
}
