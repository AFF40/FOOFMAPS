package com.example.foofmaps.dueño;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.foofmaps.MapsActivity;
import com.example.foofmaps.R;
import com.example.foofmaps.SearchFragment;
import com.example.foofmaps.SettingsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class vista_dueno extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean centerCameraOnce = true;
    private LocationCallback locationCallback;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_dueno);




        bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.maps:
                    Intent intent = new Intent(vista_dueno.this, vista_dueno.class);
                    startActivity(intent);
                    return true;
                case R.id.search:
                    Fragment fragmentSearch = new SearchFragment(); // Reemplaza con el nombre correcto de tu fragmento
                    loadFragment(fragmentSearch);
                    return true;
                case R.id.ajustes:
                    Fragment fragmentSettings = new SettingsFragment(); // Reemplaza con el nombre correcto de tu fragmento
                    loadFragment(fragmentSettings);
                    return true;
            }
            return false;
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Verifica si el fragmento ya está en el contenedor
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());

        if (existingFragment != null) {
            // Si el fragmento existe, simplemente muestra el fragmento
            transaction.show(existingFragment);
        } else {
            // Si el fragmento no existe, agrega la nueva instancia al contenedor
            transaction.add(R.id.map_dueño, fragment, fragment.getClass().getName());
        }

        // Oculta los fragmentos que no se están mostrando
        for (Fragment fragmentToHide : getSupportFragmentManager().getFragments()) {
            if (fragmentToHide != existingFragment) {
                transaction.hide(fragmentToHide);
            }
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }
}