package com.example.foofmaps.dueño.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.Config;
import com.example.foofmaps.HttpUtils;
import com.example.foofmaps.R;
import com.example.foofmaps.dueño.agregar_platos;
import com.example.foofmaps.dueño.editar_plato;
import com.example.foofmaps.platosybebidas.Plato;
import com.example.foofmaps.platosybebidas.PlatoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class dueno_platos extends Fragment {
    private boolean toastShown = false; // Variable para rastrear si el Toast ya se ha mostrado

    public static dueno_platos newInstance(int restauranteId) {
    dueno_platos fragment = new dueno_platos();
    Bundle args = new Bundle();
    args.putInt("restaurante_id", restauranteId);
    Log.d("dueno_plato_id_rest", "restaurante_id: " + restauranteId);
    fragment.setArguments(args);
    return fragment;
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dueno_platos, container, false);
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        Log.d("dueno_plato_recibido", "restaurante_id: " + restauranteId);
        // Ahora puedes utilizar restauranteId directamente para obtener la lista de platos
        new dueno_platos.GetPlatosTask().execute(restauranteId);
        // Obtén una referencia a los botones
        LinearLayout btnAñadirPlato = view.findViewById(R.id.añadirplato);
        LinearLayout btnEditarPlato = view.findViewById(R.id.editarplato);
        LinearLayout btnActualizarlista = view.findViewById(R.id.actualizarlistaplatos);

        // Configura un OnClickListener para el botón "Añadir plato"
        btnAñadirPlato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el restaurante_id
                int restauranteId = getArguments().getInt("restaurante_id", -1);

                // Dentro del método onCreateView() o cualquier método donde desees obtener el nombre del restaurante
                String nombreRestaurante = getArguments().getString("nombre_restaurante");

                // Abre la primera actividad cuando se hace clic en "Añadir plato"
                Intent intent = new Intent(requireContext(), agregar_platos.class);
                intent.putExtra("restaurante_id", restauranteId);
                intent.putExtra("nombre_restaurante", nombreRestaurante);
                Log.d("dueno_plato_id_enviado_intent", "restaurante_id: " + restauranteId);
                Log.d("nombre_restaurante_enviado_intent", "nombre_restaurante: " + nombreRestaurante);
                startActivity(intent);
            }
        });
        // Configura un OnClickListener para el botón "Editar plato"
        btnEditarPlato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén el restaurante_id
                int restauranteId = getArguments().getInt("restaurante_id", -1);
                String nombreRestaurante = getArguments().getString("nombre_restaurante");
                // Abre la segunda actividad cuando se hace clic en "Editar plato"
                Intent intent = new Intent(requireContext(), editar_plato.class);
                intent.putExtra("restaurante_id", restauranteId);
                intent.putExtra("nombre_restaurante", nombreRestaurante);
                Log.d("Log_duenoplato_idrest", "restaurante_id" + restauranteId);
                Log.d("Log_duenoplato_nomrest", "nombre_restaurante" + nombreRestaurante);
                startActivity(intent);
            }
        });

        // Configura un OnClickListener para el botón "Actualizar lista de platos"
        btnActualizarlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama al método para actualizar la lista de platos
                actualizarListaPlatos();
            }
        });

        // Realiza la carga inicial de platos
        new dueno_platos.GetPlatosTask().execute(restauranteId);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Actualiza la lista de platos
        actualizarListaPlatos();
    }


    // Método para actualizar la lista de platos
    private void actualizarListaPlatos() {
        // Realiza la carga de platos nuevamente
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        new GetPlatosTask().execute(restauranteId);
    }


    private class GetPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Plato> platos = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String modeloURL = Config.MODELO_URL+"listarPlatos.php?restaurante_id=" + idRestaurante;
                Log.d("dueno_plato_url", "apiUrl: " + modeloURL);
                String jsonResponse = HttpUtils.get(modeloURL);
                Log.d("dueno_plato_JSON_RESPONSEs", jsonResponse);

                // Procesar el JSON y obtener la lista de platos
                platos = parsePlatosFromJSON(jsonResponse);
                Log.d("dueno_plato_platos", platos.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return platos;
        }

        @Override
        protected void onPostExecute(List<Plato> platos) {
            if (platos.isEmpty() && !toastShown) {
                // Mostrar un Toast indicando que la lista de bebidas está vacía
                Toast.makeText(requireContext(), "Lista de platos vacía", Toast.LENGTH_SHORT).show();
                toastShown = true; // Marcar que el Toast ya se ha mostrado
            }
            // Configurar el RecyclerView con la lista de platos
            RecyclerView recyclerViewPlatos = getView().findViewById(R.id.viewPlatos);
            PlatoAdapter platoAdapter = new PlatoAdapter(platos,false,0,null);
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
                int id_comida = platoJson.getInt("id_meplat");
                String nombre = platoJson.getString("nombre");
                String descripcion = platoJson.getString("descripcion");
                float precio = (float) platoJson.getDouble("precio");
                int disponible = platoJson.getInt("disponible");

                String imagen = platoJson.getString("imagen");

                // Crear un objeto Plato con los datos
                Plato plato = new Plato(id_comida, nombre, descripcion, precio, imagen, disponible,0,null );
                platos.add(plato);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return platos;
    }

}