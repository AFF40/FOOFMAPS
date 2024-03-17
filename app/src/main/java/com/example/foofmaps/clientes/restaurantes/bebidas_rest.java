package com.example.foofmaps.clientes.restaurantes;

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
                String modeloURL = Config.MODELO_URL+"getBebidas.php?restaurante_id=" + idRestaurante;
                String jsonResponse = HttpUtils.get(modeloURL);
                Log.d("modeloURL", modeloURL);

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
            BebidaAdapter bebidaAdapter = new BebidaAdapter(bebidas,false);
            recyclerViewBeidas.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewBeidas.setAdapter(bebidaAdapter);
        }
    }

    private List<Bebida> parseBebidasFromJSON(String json) {
        Log.d("DEBUG", "parsePlatosFromJSON iniciado"); // Log adicional

        List<Bebida> bebidas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bebidaJson = jsonArray.getJSONObject(i);
                int id_bebida = bebidaJson.getInt("id_bebida");
                String nombre = bebidaJson.getString("nombre");
                String descripcion = bebidaJson.getString("descripcion");
                float precio = (float) bebidaJson.getDouble("precio");
                int disponible = bebidaJson.getInt("disponible"); // Usar el campo "disponible" de la respuesta JSON
                String imagen = bebidaJson.getString("imagen"); // Usar el campo "imagen" de la respuesta JSON


                //log para verificar todos los datos
                Log.d("PLATO_DEBUG", "id: " + id_bebida);
                Log.d("PLATO_DEBUG", "nombre: " + nombre);
                Log.d("PLATO_DEBUG", "descripcion: " + descripcion);
                Log.d("PLATO_DEBUG", "precio: " + precio);
                Log.d("PLATO_DEBUG", "disponible: " + disponible);
                Log.d("PLATO_DEBUG", "imagen: " + imagen);
                Log.d("PLATO_DEBUG", "Platos: " + bebidas.toString());

                Bebida bebida = new Bebida(id_bebida, nombre, descripcion, precio, imagen, disponible);
                bebidas.add(bebida);
                Log.d("PLATO_DEBUG", "Plato: " + bebida.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bebidas;
    }

}