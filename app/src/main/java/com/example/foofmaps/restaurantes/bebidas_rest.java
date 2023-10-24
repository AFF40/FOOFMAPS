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
import com.example.foofmaps.platosybebidas.Bebida;
import com.example.foofmaps.platosybebidas.BebidaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class bebidas_rest extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bebidas_rest, container, false);

        // Obtén el restaurante_id de los argumentos del fragmento
        Bundle args = getArguments();
        if (args != null) {
            int restaurante_id = args.getInt("restaurant_id", 0);

            // Realizar una solicitud para obtener la lista de platos en segundo plano
            new GetBebidasTask().execute(restaurante_id);
        }

        return view;
    }

    private class GetBebidasTask extends AsyncTask<Integer, Void, List<Bebida>> {
        @Override
        protected List<Bebida> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Bebida> bebidas = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String apiUrl = "http://192.168.1.3/modelo/getBebidas.php?restaurante_id=" + idRestaurante;
                String jsonResponse = HttpUtils.get(apiUrl);

                // Procesar el JSON y obtener la lista de platos
                bebidas = parseBebidasFromJSON(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bebidas;
        }

        @Override
        protected void onPostExecute(List<Bebida> bebidas) {
            // Configurar el RecyclerView con la lista de platos
            RecyclerView recyclerViewBeidas = requireView().findViewById(R.id.recyclerViewBebidas);
            BebidaAdapter bebidaAdapter = new BebidaAdapter(bebidas);
            recyclerViewBeidas.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewBeidas.setAdapter(bebidaAdapter);
        }
    }

    private List<Bebida> parseBebidasFromJSON(String json) {
        List<Bebida> bebidas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bebidaJson = jsonArray.getJSONObject(i);
                String nombre = bebidaJson.getString("nom_bebida");
                String descripcion = bebidaJson.getString("descripcion");
                float precio = (float) bebidaJson.getDouble("precio");
                int disponible = bebidaJson.getInt("disponible");
                String imagenBase64 = bebidaJson.getString("imagen");
                byte[] imagen = decodeBase64(imagenBase64);
                Bebida bebida = new Bebida(nombre, descripcion, precio, imagen, disponible);
                bebidas.add(bebida);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bebidas;
    }

    private byte[] decodeBase64(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
    }
}