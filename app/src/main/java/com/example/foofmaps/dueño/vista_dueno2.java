package com.example.foofmaps.dueño;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class vista_dueno2 extends AppCompatActivity {

    private MapsFragment mapsFragment;
    private SettingsDuenoFragment settingsDuenoFragment;
    private dueno_platos platos_Fragment;
    private dueno_bebidas2 bebidas_Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        // Obtiene el id del restaurante desde el intent desde la actividad anterior
        int id_rest = getIntent().getIntExtra("restaurante_id", -1);
        //imprime el id del restaurante
        Log.d("id_rest", String.valueOf(id_rest));

        mapsFragment = new MapsFragment();
        settingsDuenoFragment = new SettingsDuenoFragment();
        platos_Fragment = new dueno_platos();
        bebidas_Fragment = new dueno_bebidas2();

        // Crear un Bundle y agregar el restauranteId
        Bundle bundle = new Bundle();
        bundle.putInt("restaurante_id", id_rest);
        mapsFragment.setArguments(bundle); // Asignar el Bundle al MapsFragment

        // Carga el fragmento inicialmente
        loadFragment(mapsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.maps:
                        loadFragment(mapsFragment);
                        return true;
                    case R.id.ajustes:
                        loadFragment(settingsDuenoFragment);
                        return true;
                    case R.id.alimentos:
                        loadFragment(platos_Fragment);
                        return true;
                    case R.id.bebidas:
                        loadFragment(bebidas_Fragment);
                        return true;
                }
                transaction.commit();
                return false;
            }
        });
        fetchRestaurantDataFromDatabase(id_rest);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mapsFragment != null && mapsFragment.isVisible()) {
            transaction.hide(mapsFragment);
        }
        if (platos_Fragment != null && platos_Fragment.isVisible()) {
            transaction.hide(platos_Fragment);
        }
        if (bebidas_Fragment != null && bebidas_Fragment.isVisible()) {
            transaction.hide(bebidas_Fragment);
        }
        if (settingsDuenoFragment != null && settingsDuenoFragment.isVisible()) {
            transaction.hide(settingsDuenoFragment);
        }

        if (fragment != null) {
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(R.id.fragment_container, fragment, fragment.getClass().getName());
            }
        }

        transaction.commit();
    }

    private void fetchRestaurantDataFromDatabase(int restauranteId) {
        String controladorURL1 = Config.CONTROLADOR_URL + "cont_rest.php?restaurante_id=" + restauranteId;
        Log.d("url_rest", controladorURL1);
        String modeloURL2 = Config.MODELO_URL + "icono_rest.php?id=" + restauranteId;
        Log.d("url_sql", modeloURL2);
        Switch switchEstado = findViewById(R.id.boton_estado_rest);
        TextView nomrest_tx = findViewById(R.id.estado_rest);
        ImageView imageViewRestaurante = findViewById(R.id.icono_res);

        //obtener la url de la imagen
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, modeloURL2, response -> {
            try {
                String imagen = response;
                //convertir localhost por la ip del servidor
                imagen = imagen.replace("http://localhost", Config.ip);
                Log.d("imagen", imagen);
                if (!imagen.isEmpty()) {
                    // Cargar la imagen desde tu servidor utilizando Glide
                    Glide.with(this)
                            .load(imagen) // Aquí debes poner la URL correcta del servidor
                            .into(imageViewRestaurante); // Aquí debes poner el ImageView correcto
                }
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Maneja el error si la solicitud falla
            Log.e("FetchDataError", "Error fetching data: " + error.toString());
        });
        requestQueue2.add(stringRequest2);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, controladorURL1, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int restaurante_id = jsonObject.getInt("restaurante_id");
                    String nomRest = jsonObject.getString("nom_rest");

                    int estadoRestaurante = jsonObject.getInt("estado");

                    Log.d("Restaurante", "Nombre: " + nomRest);
                    Log.d("Restaurante", "Estado: " + estadoRestaurante);

                    TextView textViewNomRest = findViewById(R.id.nom_rest);
                    // Actualiza el contenido de la vista con el valor.
                    textViewNomRest.setText(nomRest);

                    // Actualiza el estado del restaurante en la vista
                    if (estadoRestaurante == 1) {
                        switchEstado.setChecked(true);
                        nomrest_tx.setText("Abierto");
                    } else {
                        switchEstado.setChecked(false);
                        nomrest_tx.setText("Cerrado");
                    }
                    switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        int nuevoEstado = isChecked ? 1 : 0;
                        sendRequest(restaurante_id, nuevoEstado);
                        // Actualiza el estado del restaurante en la vista si está abierto o cerrado
                        if (isChecked) {
                            nomrest_tx.setText("Abierto");
                        } else {
                            nomrest_tx.setText("Cerrado");
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Maneja el error si la solicitud falla
            Log.e("FetchDataError", "Error fetching data: " + error.toString());
        });
        requestQueue.add(stringRequest);
    }

    private void sendRequest(int restauranteId, int estado) {
        // Construye la URL con los parámetros
        String modeloURL = Config.MODELO_URL + "cambiar_estado.php?restaurante_id=" + restauranteId + "&estado=" + estado;
        Log.d("url_estado", modeloURL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, modeloURL, response -> {
        }, error -> {
        });

        requestQueue.add(stringRequest);
    }
}
