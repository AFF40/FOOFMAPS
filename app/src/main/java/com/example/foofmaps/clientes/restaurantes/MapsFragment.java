package com.example.foofmaps.clientes.restaurantes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.Restaurante;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private BitmapDescriptor defaultMarker;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla la vista desde el archivo de diseño XML
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Aplica el estilo personalizado
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style_no_labels));

            if (!success) {
                Log.e("MapsActivity", "Estilo de mapa no aplicado.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "No se puede encontrar el estilo. Error: ", e);
        }



        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
        fetchLocationsFromDatabase();
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100000000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (currentUserLocationMarker != null) {
                        currentUserLocationMarker.remove();
                    }

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
                }
            }
        };

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    private void fetchLocationsFromDatabase() {
        String controladorURL = Config.CONTROLADOR_URL+"controlador_Rest.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, controladorURL, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.d("JSON Response", jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int restaurante_id = jsonObject.getInt("restaurante_id"); // ID del restaurante del marcador
                    int celular = jsonObject.getInt("celular");
                    String nomRest = jsonObject.getString("nom_rest");
                    double latitud = jsonObject.getDouble("latitud");
                    double longitud = jsonObject.getDouble("longitud");
                    int estadoRestaurante = jsonObject.getInt("estado"); // Asumiendo que el estado se llama "estado" en el JSON
                    // Determinar el color del marcador según el estado del restaurante
                    float hue = (estadoRestaurante == 0) ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN;
                    // Agregar marcador en el mapa
                    LatLng location = new LatLng(latitud, longitud);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(nomRest)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)); // Configurar el color del marcador
                    Marker restaurantMarker = mMap.addMarker(markerOptions);
                    // Crear un objeto Restaurante con los datos del restaurante
                    Restaurante restaurante = new Restaurante(restaurante_id, celular, nomRest);
                    // Establecer el restaurante como etiqueta del marcador
                    restaurantMarker.setTag(restaurante);
                    // Configurar el InfoWindow para que no sea clickeable
                    mMap.setOnInfoWindowClickListener(marker -> {
                        // Obtener el restaurante desde la etiqueta del marcador
                        Restaurante restaurante1 = (Restaurante) marker.getTag();
                        if (restaurante1 != null) {
                            //  se hace clic en el InfoWindow,
                            Intent intent = new Intent(getActivity(), MenuRest.class);
                            intent.putExtra("restaurant_id", restaurante1.getRestauranteId());
                            intent.putExtra("restaurant_name", restaurante1.getNomRest());
                            intent.putExtra("restaurant_phone", restaurante1.getCelular());
                            startActivity(intent);
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