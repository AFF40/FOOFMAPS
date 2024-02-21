package com.example.foofmaps.clientes.restaurantes;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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

import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    private Map<String, Bitmap> loadedBitmaps = new HashMap<>();

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

        LatLng plazaQuintanilla = new LatLng(-17.382202, -66.151789); // Coordenadas de la Plaza Quintanilla

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGpsEnabled) {
                mMap.setMyLocationEnabled(true);
                mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(plazaQuintanilla));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                    startLocationUpdates();
                });
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(plazaQuintanilla));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(plazaQuintanilla));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        fetchLocationsFromDatabase();
    }
    private BroadcastReceiver gpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGpsEnabled) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        startLocationUpdates();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(false);
                    }
                }
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(gpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(gpsSwitchStateReceiver);
    }
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                }
            }
        }
    };


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
                    String imagen = jsonObject.getString("imagen");
                    int estadoRestaurante = jsonObject.getInt("estado"); // Asumiendo que el estado se llama "estado" en el JSON
                    // Determinar el color del marcador según el estado del restaurante
                    float hue = (estadoRestaurante == 0) ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN;

                    Log.d("Restaurante", "ID: " + restaurante_id + ", Nombre: " + nomRest + ", Latitud: " + latitud + ", Longitud: " + longitud);

                    // Agregar marcador en el mapa
                    LatLng location = new LatLng(latitud, longitud);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(nomRest)
                            .icon(BitmapDescriptorFactory.defaultMarker(hue)); // Configurar el color del marcador
                    Marker restaurantMarker = mMap.addMarker(markerOptions);
                    // Crear un objeto Restaurante con los datos del restaurante
                    Restaurante restaurante = new Restaurante(restaurante_id, celular, nomRest,imagen);
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
                            intent.putExtra("restaurant_image", restaurante1.getImagen());
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
                            ImageView markerImage = infoView.findViewById(R.id.marker_image);

                            // Configurar el contenido de la vista personalizada
                            markerTitle.setText(marker.getTitle());

                            // Obtener el restaurante desde la etiqueta del marcador
                            Restaurante restaurante = (Restaurante) marker.getTag();
                            if (restaurante != null) {
                                // Obtener la URL de la imagen del restaurante
                                String imageUrl = restaurante.getImagen();

// Reemplazar "localhost" con "10.0.2.2" si estás usando un emulador de Android
                                imageUrl = imageUrl.replace("localhost", "192.168.100.5");

// Crear una variable final que contenga el valor de imageUrl
                                final String finalImageUrl = imageUrl;

// Crear un Target personalizado para Glide
                                SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        // Cuando la imagen esté lista, almacenarla en el mapa
                                        loadedBitmaps.put(finalImageUrl, resource);
                                        // Actualizar la ventana de información del marcador
                                        marker.showInfoWindow();
                                    }
                                };

// Verificar si la imagen ya ha sido cargada
                                if (loadedBitmaps.containsKey(finalImageUrl)) {
                                    // Si la imagen ya ha sido cargada, establecerla en el ImageView
                                    markerImage.setImageBitmap(loadedBitmaps.get(finalImageUrl));
                                } else {
                                    // Si la imagen no ha sido cargada, cargarla con Glide
                                    Glide.with(getActivity())
                                            .asBitmap() // Indicar que queremos un Bitmap, no un Drawable
                                            .load(finalImageUrl)
                                            .into(target); // Cargar la imagen en el Target personalizado
                                }
                            }

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