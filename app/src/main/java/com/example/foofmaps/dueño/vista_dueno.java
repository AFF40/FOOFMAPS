package com.example.foofmaps.dueño;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.MainActivity;
import com.example.foofmaps.R;
import com.example.foofmaps.SearchFragment;
import com.example.foofmaps.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class vista_dueno extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_dueno);


        int restaurante_id = getIntent().getIntExtra("restaurante_id", -1);

        // Verificar si el id_rest se ha pasado correctamente
        if (restaurante_id != -1) {
            // El id_rest se pasó correctamente, puedes usarlo aquí
            // Por ejemplo, imprimirlo en el logcat
            Log.d("recibido_id_rest", "El valor de id_rest es: " + restaurante_id);

            // Llamar a la función para obtener datos del restaurante
            fetchRestaurantDataFromDatabase(restaurante_id);
        } else {
            // El id_rest no se pasó correctamente, maneja esta situación
            // Puedes mostrar un mensaje de error o tomar otras medidas
            // Por ejemplo, regresar a MainActivity
            Intent intent = new Intent(vista_dueno.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.maps:
                    // No necesitas iniciar la actividad actual nuevamente
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

    private void fetchRestaurantDataFromDatabase(int restauranteId) {
        String url = "http://192.168.1.3/web2/controlador/cont_rest.php?restaurante_id=" + restauranteId;
        Log.d("url", url);

        String imageUrl = "http://192.168.1.3/modelo/icono_rest.php?id=" + restauranteId;
        Switch switchEstado = findViewById(R.id.boton_estado_rest);
        TextView nomrest_tx = findViewById(R.id.estado_rest);
        ImageView imageViewRestaurante = findViewById(R.id.icono_res);

        // Cargar la imagen desde tu servidor utilizando Picasso
        Picasso.get().load(imageUrl).into(imageViewRestaurante);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                // Aquí puedes procesar los datos del restaurante desde jsonObject
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int restaurante_id = jsonObject.getInt("restaurante_id"); // ID del restaurante del marcador
                    int celular = jsonObject.getInt("celular");
                    String nomRest = jsonObject.getString("nom_rest");
                    JSONObject ubicacion = jsonObject.getJSONObject("ubicacion");
                    double latitud = ubicacion.getDouble("latitud");
                    double longitud = ubicacion.getDouble("longitud");
                    int estadoRestaurante = jsonObject.getInt("estado");

                    // Muestra los valores en el logcat
                    Log.d("Restaurante_celular", "Celular: " + celular);
                    Log.d("Restaurante", "Nombre: " + nomRest);
                    Log.d("Restaurante", "Latitud: " + latitud);
                    Log.d("Restaurante", "Longitud: " + longitud);
                    Log.d("Restaurante", "Estado: " + estadoRestaurante);

                    // Busca la vista correspondiente en tu diseño por su ID.
                    TextView textViewNomRest = findViewById(R.id.nom_rest); // Asegúrate de que el ID sea el correcto.

                    switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            // Cuando el Switch está activado, manda la solicitud GET con "estado = 0"
                            sendRequest(restaurante_id, 1);
                            nomrest_tx.setText("abierto");
                        } else {
                            // Cuando el Switch está desactivado, puedes manejar esta situación si es necesario
                            sendRequest(restaurante_id, 0);
                            nomrest_tx.setText("cerrado");
                            // Aquí puedes tomar medidas adicionales si lo necesitas.
                        }
                    });

// Actualiza el contenido de la vista con el valor.
                    textViewNomRest.setText(nomRest);
                }
                // Luego, puedes utilizar estos datos para mostrar información en el mapa o hacer lo que necesites.
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        requestQueue.add(stringRequest);
    }
    private void sendRequest(int restauranteId, int estado) {
        // Construye la URL con los parámetros
        String url = "http://192.168.1.3/web2/modelo/cambiar_estado.php?restaurante_id=" + restauranteId + "&estado=" + estado;
        Log.d("url_estado", url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            // Procesa la respuesta de la solicitud si es necesario
            // Puedes agregar código para manejar la respuesta aquí
        }, error -> {
            // Maneja el error si la solicitud falla
        });

        requestQueue.add(stringRequest);
    }


}
