package com.example.foofmaps.restaurantes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.HttpUtils;
import com.example.foofmaps.R;
import com.example.foofmaps.platosybebidas.Plato;
import com.example.foofmaps.platosybebidas.PlatoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class platos_rest extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_platos_rest, container, false);

        // Obtén el restaurante_id de los argumentos del fragmento
        Bundle args = getArguments();
        if (args != null) {
            int restaurante_id = args.getInt("restaurant_id", 0);

            // Realizar una solicitud para obtener la lista de platos en segundo plano
            new GetPlatosTask().execute(restaurante_id);
        }

        return view;
    }

    private class GetPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Plato> platos = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String apiUrl = "http://192.168.1.3/modelo/getPlatos.php?restaurante_id=" + idRestaurante;
                String jsonResponse = HttpUtils.get(apiUrl);

                // Procesar el JSON y obtener la lista de platos
                platos = parsePlatosFromJSON(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return platos;
        }

        @Override
        protected void onPostExecute(List<Plato> platos) {
            // Configurar el RecyclerView con la lista de platos
            RecyclerView recyclerViewPlatos = requireView().findViewById(R.id.recyclerViewPlatos);
            PlatoAdapter platoAdapter = new PlatoAdapter(platos,false);
            recyclerViewPlatos.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewPlatos.setAdapter(platoAdapter);
        }
    }

    private List<Plato> parsePlatosFromJSON(String json) {
        List<Plato> platos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject platoJson = jsonArray.getJSONObject(i);
                int id_comida = platoJson.getInt("id_comida");
                String nombre = platoJson.getString("nom_plato");
                String descripcion = platoJson.getString("descripcion");
                float precio = (float) platoJson.getDouble("precio");
                int disponible = platoJson.getInt("disponible");
                String imagenBase64 = platoJson.getString("imagen");
                byte[] imagen = decodeBase64(imagenBase64);
                Plato plato = new Plato(id_comida, nombre, descripcion, precio, imagen, disponible);
                platos.add(plato);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return platos;
    }

    private byte[] decodeBase64(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
    }
}