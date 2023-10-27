package com.example.foofmaps.dueño;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.MainActivity;
import com.example.foofmaps.clientes.restaurantes.SettingsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
                case R.id.alimentos:
                    int restauranteId = getIntent().getIntExtra("restaurante_id", -1);
                    Fragment fragmentmenu = dueno_menu.newInstance(restauranteId);
                    loadFragment(fragmentmenu);
                    return true;

                case R.id.bebidas:
                    int restauranteId2 = getIntent().getIntExtra("restaurante_id", -1);
                    Fragment fragmentbebidas = dueno_bebidas.newInstance(restauranteId2);
                    loadFragment(fragmentbebidas);
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
            transaction.add(R.id.map_dueno, fragment, fragment.getClass().getName());
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
    // Función para obtener los datos del restaurante desde la base de datos

    private void fetchRestaurantDataFromDatabase(int restauranteId) {
        String controladorURL1 = Config.CONTROLADOR_URL+"cont_rest.php?restaurante_id=" + restauranteId;
        Log.d("url", controladorURL1);
        String modeloURL2 = Config.MODELO_URL+"icono_rest.php?id=" + restauranteId;
        Switch switchEstado = findViewById(R.id.boton_estado_rest);
        TextView nomrest_tx = findViewById(R.id.estado_rest);
        ImageView imageViewRestaurante = findViewById(R.id.icono_res);

        // Cargar la imagen desde tu servidor utilizando Picasso
        Picasso.get().load(modeloURL2).into(imageViewRestaurante);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, controladorURL1, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int restaurante_id = jsonObject.getInt("restaurante_id");
                    int celular = jsonObject.getInt("celular");
                    String nomRest = jsonObject.getString("nom_rest");
                    JSONObject ubicacion = jsonObject.getJSONObject("ubicacion");
                    double latitud = ubicacion.getDouble("latitud");
                    double longitud = ubicacion.getDouble("longitud");
                    int estadoRestaurante = jsonObject.getInt("estado");

                    Log.d("Restaurante_celular", "Celular: " + celular);
                    Log.d("Restaurante", "Nombre: " + nomRest);
                    Log.d("Restaurante", "Latitud: " + latitud);
                    Log.d("Restaurante", "Longitud: " + longitud);
                    Log.d("Restaurante", "Estado: " + estadoRestaurante);

                    TextView textViewNomRest = findViewById(R.id.nom_rest);

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_dueno);

                    mapFragment.getMapAsync(googleMap -> {
                        LatLng restauranteLocation = new LatLng(latitud, longitud);

                        // Agrega el marcador al mapa con el color inicial según el estado del restaurante
                        float markerColor = (estadoRestaurante == 1) ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
                        BitmapDescriptor markerIcon = BitmapDescriptorFactory.defaultMarker(markerColor);

                        // Aplica el estilo del mapa desde el archivo JSON
                        try {
                            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_no_labels));
                            if (!success) {
                                Log.e("MapActivity", "Style parsing failed.");
                            }
                        } catch (Resources.NotFoundException e) {
                            Log.e("MapActivity", "Can't find style. Error: ", e);
                        }

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(restauranteLocation)
                                .title("Restaurante")
                                .icon(markerIcon);
                        Marker restauranteMarker = googleMap.addMarker(markerOptions);

                        if (estadoRestaurante==1){
                            switchEstado.setChecked(true);
                            restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            TextView  textViewEstado = findViewById(R.id.estado_rest);
                            textViewEstado.setText("abierto");
                        }else {
                            switchEstado.setChecked(false);
                            restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            TextView  textViewEstado = findViewById(R.id.estado_rest);
                            textViewEstado.setText("cerrado");
                        }

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(restauranteLocation));
                        float zoomLevel = 15;
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

                        // Configura el listener para el Switch
                        switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            // Cambia el color del marcador y actualiza el estado del restaurante
                            if (isChecked) {
                                sendRequest(restaurante_id, 1);
                                nomrest_tx.setText("abierto");
                                restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            } else {
                                sendRequest(restaurante_id, 0);
                                nomrest_tx.setText("cerrado");
                                restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            }
                        });
                    });

                    // Actualiza el contenido de la vista con el valor.
                    textViewNomRest.setText(nomRest);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Maneja el error si la solicitud falla
        });
        requestQueue.add(stringRequest);
    }



    private void sendRequest(int restauranteId, int estado) {
        // Construye la URL con los parámetros
        String modeloURL = Config.MODELO_URL+"cambiar_estado.php?restaurante_id=" + restauranteId + "&estado=" + estado;
        Log.d("url_estado", modeloURL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, modeloURL, response -> {
            // Procesa la respuesta de la solicitud si es necesario
            // Puedes agregar código para manejar la respuesta aquí
        }, error -> {
            // Maneja el error si la solicitud falla
        });

        requestQueue.add(stringRequest);
    }



}