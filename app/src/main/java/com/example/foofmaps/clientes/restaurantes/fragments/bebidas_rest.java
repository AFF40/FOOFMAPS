// bebidas_rest.java
package com.example.foofmaps.clientes.restaurantes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.R;
import com.example.foofmaps.clientes.restaurantes.fragments.viewmodel.BebidasViewModel;
import com.example.foofmaps.platosybebidas.BebidaAdapter;

public class bebidas_rest extends Fragment {

    private BebidasViewModel bebidasViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bebidas_rest, container, false);

        // Obtén el restaurante_id de los argumentos del fragmento
        Bundle args = getArguments();
        if (args != null) {
            int restaurante_id = args.getInt("restaurant_id", 0);

            bebidasViewModel = new ViewModelProvider(this).get(BebidasViewModel.class);
            bebidasViewModel.getBebidas(restaurante_id).observe(getViewLifecycleOwner(), bebidas -> {
                RecyclerView recyclerViewBebidas = view.findViewById(R.id.recyclerViewBebidas);
                BebidaAdapter bebidaAdapter = new BebidaAdapter(bebidas, false, 0, null);
                recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerViewBebidas.setAdapter(bebidaAdapter);
            });
        }

        return view;
    }
}
