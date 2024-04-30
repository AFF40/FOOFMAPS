package com.example.foofmaps.dueño;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.fragments.SearchFragment;
import com.example.foofmaps.clientes.restaurantes.fragments.SettingsFragment;
import com.example.foofmaps.dueño.fragments.MapsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dueno_MapsActivity extends AppCompatActivity {

    private MapsFragment mapsFragment;
    private SearchFragment searchFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        mapsFragment = new MapsFragment();
        searchFragment = new SearchFragment();
        settingsFragment = new SettingsFragment();

        // Agregar todos los fragmentos al inicio y ocultar los que no son necesarios
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapsFragment, "MapsFragment")
                .add(R.id.fragment_container, searchFragment, "SearchFragment")
                .hide(searchFragment)
                .add(R.id.fragment_container, settingsFragment, "SettingsFragment")
                .hide(settingsFragment)
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.maps:
                        transaction.hide(searchFragment);
                        transaction.hide(settingsFragment);
                        transaction.show(mapsFragment);
                        break;
                    case R.id.search:
                        transaction.hide(mapsFragment);
                        transaction.hide(settingsFragment);
                        transaction.show(searchFragment);
                        break;
                    case R.id.ajustes:
                        transaction.hide(mapsFragment);
                        transaction.hide(searchFragment);
                        transaction.show(settingsFragment);
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
}   
