package com.example.foofmaps.dueño;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foofmaps.R;

public class MapsFragment extends Fragment {

    private int restauranteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Obtener los argumentos del Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            restauranteId = bundle.getInt("restaurante_id", -1);
            Log.d("id_rest_enmap", String.valueOf(restauranteId));
        }

        return view;
    }

}
