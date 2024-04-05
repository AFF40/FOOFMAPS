package com.example.foofmaps.dueño;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int estadoRestaurante;
    private LatLng restauranteLocation;
    private Marker restauranteMarker;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_dueno);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map_dueno, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configurar el mapa
        if (restauranteLocation != null) {
            // Agregar marcador al mapa
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(restauranteLocation)
                    .title("Restaurante")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            restauranteMarker = mMap.addMarker(markerOptions);

            // Mover la cámara a la ubicación del restaurante
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restauranteLocation, 15));

            // Configurar el listener para el mapa
            mMap.setOnMarkerClickListener(marker -> {
                // Cambiar el estado del restaurante cuando se hace clic en el marcador
                estadoRestaurante = (estadoRestaurante == 1) ? 0 : 1;
                updateMarkerAndText();
                return true; // Indica que el evento ha sido consumido
            });

            // Actualizar el marcador y el texto del estado
            updateMarkerAndText();
        }
    }

    // Método para actualizar el marcador y el texto del estado del restaurante
    private void updateMarkerAndText() {
        if (restauranteMarker != null) {
            float markerColor = (estadoRestaurante == 1) ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
            restauranteMarker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColor));

            // Actualizar el texto del estado en la vista
            TextView textViewEstado = requireView().findViewById(R.id.estado_rest);
            textViewEstado.setText((estadoRestaurante == 1) ? "abierto" : "cerrado");
        }
    }

    // Método para establecer la ubicación del restaurante y su estado
    public void setRestauranteLocationAndEstado(LatLng location, int estado) {
        this.restauranteLocation = location;
        this.estadoRestaurante = estado;

        if (mMap != null) {
            // Si el mapa ya está listo, actualiza el marcador y el texto del estado
            updateMarkerAndText();
        }
    }
}
