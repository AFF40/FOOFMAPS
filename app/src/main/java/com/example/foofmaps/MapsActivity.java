package com.example.foofmaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean centerCameraOnce = true; // Flag para centrar la cámara solo una vez
    private LocationCallback locationCallback; // Callback para recibir actualizaciones de ubicación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inicializar el cliente de ubicación fusionada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verificar y solicitar permiso de ubicación si es necesario
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Si se tiene permiso, obtener el mapa
            obtainMap();
        }
    }

    private void obtainMap() {
        // Obtener el Fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Verificar nuevamente si se tiene permiso de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Configurar la capa "Mi ubicación" y habilitarla en el mapa
            mMap.setMyLocationEnabled(true);

            if (centerCameraOnce) {
                // Centrar la cámara en la ubicación actual solo una vez al ingresar
                obtainMyLocation();
                centerCameraOnce = false;
            }
        }

        // Agregar un marcador en Cochabamba (esto es opcional)
        LatLng cochabamba = new LatLng(-17.373663, -66.142863);
        mMap.addMarker(new MarkerOptions().position(cochabamba).title("Marcador en Cochabamba"));

        // Cargar el estilo del mapa desde un archivo JSON (map_style_no_labels)
        int styleResId = R.raw.map_style_no_labels;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, styleResId));
    }

    private void obtainMyLocation() {
        // Crear una solicitud de ubicación
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000); // Intervalo de actualización de ubicación en milisegundos

        // Configurar el callback para recibir actualizaciones de ubicación
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location lastLocation = locationResult.getLastLocation();
                    if (lastLocation != null) {
                        // Obtener la latitud y longitud de la ubicación actual
                        double latitude = lastLocation.getLatitude();
                        double longitude = lastLocation.getLongitude();

                        // Centrar la cámara en la ubicación actual
                        LatLng myLocation = new LatLng(latitude, longitude);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 17);
                        mMap.animateCamera(cameraUpdate);

                        // Detener las actualizaciones de ubicación después de la primera vez
                        stopLocationUpdates();
                    }
                }
            }
        };

        // Solicitar actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        // Detener las actualizaciones de ubicación si no se desea seguir ajustando la cámara
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegurarse de detener las actualizaciones de ubicación al salir de la actividad
        stopLocationUpdates();
    }
}
