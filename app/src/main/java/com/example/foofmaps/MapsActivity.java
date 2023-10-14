package com.example.foofmaps;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean centerCameraOnce = true;
    private LocationCallback locationCallback;
    private BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtainMap();
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.maps:
                    Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.search:
                    Fragment fragmentSearch = new SearchFragment();
                    loadFragment(fragmentSearch);
                    return true;
                case R.id.ajustes:
                    Fragment fragmentSettings = new SettingsFragment();
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
            transaction.add(R.id.map, fragment, fragment.getClass().getName());
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


    private void obtainMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            if (centerCameraOnce) {
                obtainMyLocation();
                centerCameraOnce = false;
            }
        }
        int styleResId = R.raw.map_style_no_labels;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, styleResId));
        fetchLocationsFromDatabase();
    }
    private void obtainMyLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location lastLocation = locationResult.getLastLocation();
                    if (lastLocation != null) {
                        double latitude = lastLocation.getLatitude();
                        double longitude = lastLocation.getLongitude();

                        LatLng myLocation = new LatLng(latitude, longitude);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 17);
                        mMap.animateCamera(cameraUpdate);

                        stopLocationUpdates();
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_navigation_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maps:
                Intent intentMap = new Intent(this, MapsActivity.class);
                startActivity(intentMap);
                return true;
            case R.id.search:
                Intent intentBuscar = new Intent(this, SearchFragment.class);
                startActivity(intentBuscar);
                return true;
            case R.id.ajustes:
                Intent intentAjustes = new Intent(this, SettingsFragment.class);
                startActivity(intentAjustes);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void fetchLocationsFromDatabase() {
        String url = "http://192.168.1.3/web2/controlador/controlador_Rest.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d("JSON Response", jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int restaurante_id = jsonObject.getInt("restaurante_id"); // ID del restaurante del marcador
                    int celular = jsonObject.getInt("celular"); //  ID del restaurante del marcador
                    String nomRest = jsonObject.getString("nom_rest");
                    JSONObject ubicacion = jsonObject.getJSONObject("ubicacion");
                    double latitud = ubicacion.getDouble("latitud");
                    double longitud = ubicacion.getDouble("longitud");
                    int estadoRestaurante = jsonObject.getInt("estado"); // Asumiendo que el estado se llama "estado" en el JSON
                    // Determinar el color del marcador según el estado del restaurante
                    float hue = (estadoRestaurante == 0) ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN;
                    // Agregar marcador en el mapa
                    LatLng location = new LatLng(latitud, longitud);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(nomRest)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)); // Configurar el color del marcador
                    Marker marker = mMap.addMarker(markerOptions);
                    // Crear un objeto Restaurante con los datos del restaurante
                    Restaurante restaurante = new Restaurante(restaurante_id, celular, nomRest);
                    // Establecer el restaurante como etiqueta del marcador
                    marker.setTag(restaurante);
                    // Configurar el InfoWindow para que no sea clickeable
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            // Obtener el restaurante desde la etiqueta del marcador
                            Restaurante restaurante = (Restaurante) marker.getTag();
                            if (restaurante != null) {
                                //  se hace clic en el InfoWindow,
                                Intent intent = new Intent(MapsActivity.this, MenuRest.class);
                                intent.putExtra("restaurant_id", restaurante.getRestauranteId());
                                intent.putExtra("restaurant_name", restaurante.getNomRest());
                                intent.putExtra("restaurant_phone", restaurante.getCelular());
                                startActivity(intent);
                            }
                        }
                    });
                    // Configurar la vista personalizada para la ventana de información del marcador
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null; // Usar la vista predeterminada en blanco
                        }
                        @Override
                        public View getInfoContents(Marker marker) {
                            // Crear una vista personalizada para la ventana de información
                            View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                            // Obtener referencias a los elementos de la vista personalizada
                            TextView markerTitle = infoView.findViewById(R.id.marker_title);

                            // Configurar el contenido de la vista personalizada
                            markerTitle.setText(marker.getTitle());
                            return infoView;
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON Error", "Error al procesar los datos JSON: " + e.getMessage());
            }
        }, error -> {
            Log.e("Volley Error", "Error al obtener datos desde el servidor: " + error.getMessage());
        });

        requestQueue.add(stringRequest);
    }

}