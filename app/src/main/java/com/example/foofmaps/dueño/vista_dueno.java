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
import com.bumptech.glide.Glide;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class vista_dueno extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_dueno);

        Log.d("vista_dueno", "onCreate");

        int restaurante_id = getIntent().getIntExtra("restaurante_id", -1);
        Log.d("recibido_id_rest", "El valor de id_rest es: " + restaurante_id);

        if (restaurante_id != -1) {
            Log.d("recibido_id_rest", "El valor de id_rest es: " + restaurante_id);
            fetchRestaurantDataFromDatabase(restaurante_id);
        } else {
            Log.e("recibido_id_rest", "Error: restaurante_id no recibido correctamente");
            Intent intent = new Intent(vista_dueno.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        bottomNavigationView = findViewById(R.id.navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.maps:
                    Log.d("BottomNav", "Selected: Maps");
                    loadFragment(new MapsFragment());
                    return true;
                case R.id.alimentos:
                    Log.d("BottomNav", "Selected: Alimentos");
                    loadFragment(dueno_menu.newInstance(restaurante_id));
                    return true;
                case R.id.bebidas:
                    Log.d("BottomNav", "Selected: Bebidas");
                    loadFragment(dueno_bebidas.newInstance(restaurante_id));  // Asegúrate de pasar el restaurante_id
                    return true;
                case R.id.ajustes:
                    Log.d("BottomNav", "Selected: Ajustes");
                    loadFragment(new SettingsDuenoFragment());
                    return true;
                default:
                    return false;
            }
        });

        // Inflar el MapsFragment al iniciar la actividad
        loadFragment(new MapsFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Función para obtener los datos del restaurante desde la base de datos
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

                        if (estadoRestaurante == 1) {
                            switchEstado.setChecked(true);
                            restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            TextView textViewEstado = findViewById(R.id.estado_rest);
                            textViewEstado.setText("abierto");
                        } else {
                            switchEstado.setChecked(false);
                            restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            TextView textViewEstado = findViewById(R.id.estado_rest);
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
