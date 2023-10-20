package com.example.foofmaps.dueño;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foofmaps.HttpUtils;
import com.example.foofmaps.Plato;
import com.example.foofmaps.PlatoAdapter;
import com.example.foofmaps.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class dueno_menu extends Fragment  {
    public static dueno_menu newInstance(int restauranteId) {
        dueno_menu fragment = new dueno_menu();
        Bundle args = new Bundle();
        args.putInt("restaurante_id", restauranteId);
        Log.d("ID_DEBUGid_enviado", "restaurante_id: " + restauranteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dueno_menu, container, false);
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        Log.d("ID_DEBUGid_recibido", "restaurante_id: " + restauranteId);

        // Ahora puedes utilizar restauranteId directamente para obtener la lista de platos
        new GetPlatosTask().execute(restauranteId);
        return view;
    }


    //añade la transicion hacia el fragmento de bebidas con el navbar menu_doble_1
    public void bebidas(View view){
        dueno_bebidas fragment = new dueno_bebidas();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.viewPlatos, fragment)
                .addToBackStack(null)
                .commit();
    }


    private class GetPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Plato> platos = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String apiUrl = "http://192.168.1.3/modelo/getPlatos.php?restaurante_id=" + idRestaurante;
                Log.d("URL_DEBUGurl", "apiUrl: " + apiUrl);
                String jsonResponse = HttpUtils.get(apiUrl);
                Log.d("JSON_RESPONSEs", jsonResponse);

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
            RecyclerView recyclerViewPlatos = getView().findViewById(R.id.viewPlatos);
            PlatoAdapter platoAdapter = new PlatoAdapter(platos);
            recyclerViewPlatos.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewPlatos.setAdapter(platoAdapter);
        }
    }

    // Método para analizar el JSON y obtener una lista de platos
    private List<Plato> parsePlatosFromJSON(String json) {
        List<Plato> platos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject platoJson = jsonArray.getJSONObject(i);
                String nombre = platoJson.getString("nom_plato");
                String descripcion = platoJson.getString("descripcion");
                float precio = (float) platoJson.getDouble("precio");
                int disponible = platoJson.getInt("disponible");

                // La imagen en formato Base64 (si está disponible)
                String imagenBase64 = platoJson.getString("imagen");

                // Decodificar la imagen desde Base64 a bytes (si está disponible)
                byte[] imagen = decodeBase64(imagenBase64);

                // Crear un objeto Plato con los datos
                Plato plato = new Plato(nombre, descripcion, precio, imagen, disponible );
                platos.add(plato);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return platos;
    }

    // Método para decodificar una cadena Base64 en un arreglo de bytes
    private byte[] decodeBase64(String base64) {
        return android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
    }


}
