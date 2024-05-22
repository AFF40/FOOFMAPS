package com.example.foofmaps.clientes.restaurantes.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.Config;
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
            Log.d("RESTAURANT_ID_menurest", String.valueOf(restaurante_id));
            // Realizar una solicitud para obtener la lista de platos en segundo plano
            new GetPlatosTask().execute(restaurante_id);
        }

        return view;
    }

    private class GetPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            Log.d("DEBUG", "doInBackground iniciado"); // Log adicional


            int idRestaurante = params[0];
            List<Plato> platos = new ArrayList<>();


            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String modeloURL = Config.MODELO_URL+"getPlatos.php?restaurante_id=" + idRestaurante;
                String jsonResponse = HttpUtils.get(modeloURL);
                Log.d ("urledit", "apiUrl: " +modeloURL );
                Log.d("API Response", jsonResponse);

                // Procesar el JSON y obtener la lista de platos
                platos = parsePlatosFromJSON(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return platos;
        }

        @Override
        protected void onPostExecute(List<Plato> platos) {
            Log.d("DEBUG", "onPostExecute iniciado"); // Log adicional

            // Configurar el RecyclerView con la lista de platos
            RecyclerView recyclerViewPlatos = requireView().findViewById(R.id.recyclerViewPlatos);
            PlatoAdapter platoAdapter = new PlatoAdapter(platos,false,0,null);
            recyclerViewPlatos.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewPlatos.setAdapter(platoAdapter);
        }
    }

    private List<Plato> parsePlatosFromJSON(String json) {
        Log.d("DEBUG", "parsePlatosFromJSON iniciado"); // Log adicional

        List<Plato> platos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject platoJson = jsonArray.getJSONObject(i);
                int id_plato = platoJson.getInt("id_plato");
                String nombre = platoJson.getString("nombre");
                String descripcion = platoJson.getString("descripcion");
                float precio = (float) platoJson.getDouble("precio");
                int disponible = platoJson.getInt("disponible"); // Usar el campo "disponible" de la respuesta JSON
                String imagen = platoJson.getString("imagen"); // Usar el campo "imagen" de la respuesta JSON


                //log para verificar todos los datos
                Log.d("PLATO_DEBUG", "id: " + id_plato);
                Log.d("PLATO_DEBUG", "nombre: " + nombre);
                Log.d("PLATO_DEBUG", "descripcion: " + descripcion);
                Log.d("PLATO_DEBUG", "precio: " + precio);
                Log.d("PLATO_DEBUG", "disponible: " + disponible);
                Log.d("PLATO_DEBUG", "imagen: " + imagen);
                Log.d("PLATO_DEBUG", "Platos: " + platos.toString());

                Plato plato = new Plato(id_plato, nombre, descripcion, precio, imagen, disponible,0,null);
                platos.add(plato);
                Log.d("PLATO_DEBUG", "Plato: " + plato.toString());
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