package com.example.foofmaps.dueño.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.Config;
import com.example.foofmaps.HttpUtils;
import com.example.foofmaps.R;
import com.example.foofmaps.dueño.agregar_bebidas;
import com.example.foofmaps.dueño.editar_bebida;
import com.example.foofmaps.platosybebidas.Bebida;
import com.example.foofmaps.platosybebidas.BebidaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dueno_bebidas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dueno_bebidas extends Fragment {

    public static dueno_bebidas newInstance(int restauranteId) {
        dueno_bebidas fragment = new dueno_bebidas();
        Bundle args = new Bundle();
        args.putInt("restaurante_id", restauranteId);
        Log.d("ID_DEBUGid_enviado", "restaurante_id: " + restauranteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dueno_bebidas, container, false);
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        Log.d("ID_DEBUGid_recibido", "restaurante_id: " + restauranteId);
        // Ahora puedes utilizar restauranteId directamente para obtener la lista de platos
        new dueno_bebidas.GetBebidasTask().execute(restauranteId);
        // Obtén una referencia a los botones
        LinearLayout btnAñadirbebida = view.findViewById(R.id.añadirbebida);
        LinearLayout btnEditarBebida = view.findViewById(R.id.editarbebidas);
        LinearLayout btnActualizarlista = view.findViewById(R.id.actualizarlistabebidas);
        btnAñadirbebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el restaurante_id
                int restauranteId = getArguments().getInt("restaurante_id", -1);
                // Abre la primera actividad cuando se hace clic en "Añadir plato"
                Intent intent = new Intent(requireContext(), agregar_bebidas.class);
                intent.putExtra("restaurante_id", restauranteId);
                Log.d("ID_DEBUGid_enviado_intent", "restaurante_id: " + restauranteId);
                startActivity(intent);
            }
        });

        // Configura un OnClickListener para el botón "Editar plato"
        btnEditarBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el restaurante_id
                int restauranteId = getArguments().getInt("restaurante_id", -1);
                // Abre la segunda actividad cuando se hace clic en "Editar plato"
                Intent intent = new Intent(requireContext(), editar_bebida.class);
                intent.putExtra("restaurante_id", restauranteId);
                Log.d("ID_DEBUGid_enviado_intent", "restaurante_id: " + restauranteId);
                startActivity(intent);
            }
        });


        // Configura un OnClickListener para el botón "Actualizar lista de platos"
        btnActualizarlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama al método para actualizar la lista de platos
                actualizarListaBebidas();
            }
        });

        // Realiza la carga inicial de platos
        int restaurante_id = getArguments().getInt("restaurante_id", -1);
        new dueno_bebidas.GetBebidasTask().execute(restauranteId);
        return view;
    }

    // Método para actualizar la lista de platos
    private void actualizarListaBebidas() {
        // Realiza la carga de platos nuevamente
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        new dueno_bebidas.GetBebidasTask().execute(restauranteId);
    }

    private class GetBebidasTask extends AsyncTask<Integer, Void, List<Bebida>> {
        @Override
        protected List<Bebida> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Bebida> bebidas = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String modeloURL = Config.MODELO_URL+"getBebidas.php?restaurante_id=" + idRestaurante;
                Log.d("URL_DEBUGurl", "apiUrl: " + modeloURL);
                String jsonResponse = HttpUtils.get(modeloURL);
                Log.d("JSON_RESPONSEs", jsonResponse);

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
            RecyclerView recyclerViewBebidas = getView().findViewById(R.id.viewbebidas);
            BebidaAdapter bebidaAdapter = new BebidaAdapter(bebidas,false);
            recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewBebidas.setAdapter(bebidaAdapter);
        }
    }

    // Método para analizar el JSON y obtener una lista de platos
    private List<Bebida> parseBebidasFromJSON(String json) {
        List<Bebida> bebidas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bebidaJson = jsonArray.getJSONObject(i);
                int id_bebida= bebidaJson.getInt("id_bebida");
                String nombre = bebidaJson.getString("nom_bebida");
                String descripcion = bebidaJson.getString("descripcion");
                float precio = (float) bebidaJson.getDouble("precio");
                int disponible = bebidaJson.getInt("disponible");

                // La imagen en formato Base64 (si está disponible)
                String imagen = bebidaJson.getString("imagen");


                // Crear un objeto Plato con los datos
                Bebida bebida = new Bebida(id_bebida,nombre, descripcion, precio, imagen, disponible );
                bebidas.add(bebida);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bebidas;
    }
}