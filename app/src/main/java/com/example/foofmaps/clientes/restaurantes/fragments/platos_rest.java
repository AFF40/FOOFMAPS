// platos_rest.java
package com.example.foofmaps.clientes.restaurantes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.fragments.viewmodel.PlatosViewModel;
import com.example.foofmaps.platosybebidas.PlatoAdapter;

public class platos_rest extends Fragment {

    private PlatosViewModel platosViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_platos_rest, container, false);

        // Obtén el restaurante_id de los argumentos del fragmento
        Bundle args = getArguments();
        if (args != null) {
            int restaurante_id = args.getInt("restaurant_id", 0);
            Log.d("RESTAURANT_ID_menurest", String.valueOf(restaurante_id));

            platosViewModel = new ViewModelProvider(this).get(PlatosViewModel.class);
            platosViewModel.getPlatos(restaurante_id).observe(getViewLifecycleOwner(), platos -> {
                RecyclerView recyclerViewPlatos = view.findViewById(R.id.recyclerViewPlatos);
                PlatoAdapter platoAdapter = new PlatoAdapter(platos, false, 0, null);
                recyclerViewPlatos.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerViewPlatos.setAdapter(platoAdapter);
            });
        }

        return view;
    }
}
