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

public class dueno_bebidas extends Fragment {

    public static dueno_bebidas newInstance(int restauranteId) {
        dueno_bebidas fragment = new dueno_bebidas();
        Bundle args = new Bundle();
        args.putInt("restaurante_id", restauranteId);
        Log.d("dueno_bebidas_id_enviado", "restaurante_id: " + restauranteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dueno_bebidas, container, false);
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        String nombreRestaurante = getArguments().getString("nombre_restaurante"); // Recuperar el nombre del restaurante

        Log.d("dueno_bebidas_id_recibido", "restaurante_id: " + restauranteId);
        Log.d("dueno_bebidas_nombre_recibido", "nombre_restaurante: " + nombreRestaurante);

        // Ahora puedes utilizar restauranteId y nombreRestaurante directamente para obtener la lista de bebidas
        new GetBebidasTask().execute(restauranteId);

        // Obtén una referencia a los botones
        LinearLayout btnAñadirBebida = view.findViewById(R.id.añadirbebida);
        LinearLayout btnEditarBebida = view.findViewById(R.id.editarbebidas);
        LinearLayout btnActualizarLista = view.findViewById(R.id.actualizarlistabebidas);

        // Configura un OnClickListener para el botón "Añadir bebida"
        btnAñadirBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de agregar bebida
                Intent intent = new Intent(requireContext(), agregar_bebidas.class);
                intent.putExtra("restaurante_id", restauranteId);
                intent.putExtra("nombre_restaurante", nombreRestaurante);
                Log.d("dueno_bebidas_nombre_restaurante_enviado", "nombre_restaurante: " + nombreRestaurante);
                Log.d("dueno_bebidas_id_enviado", "restaurante_id: " + restauranteId);
                startActivity(intent);
            }
        });

        // Configura un OnClickListener para el botón "Editar bebida"
        btnEditarBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad de editar bebida
                Intent intent = new Intent(requireContext(), editar_bebida.class);
                intent.putExtra("restaurante_id", restauranteId);
                intent.putExtra("nombre_restaurante", nombreRestaurante); // Enviar el nombre del restaurante
                startActivity(intent);
            }
        });

        // Configura un OnClickListener para el botón "Actualizar lista de bebidas"
        btnActualizarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama al método para actualizar la lista de bebidas
                actualizarListaBebidas();
            }
        });

        // Realiza la carga inicial de bebidas
        new GetBebidasTask().execute(restauranteId);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Actualiza la lista de platos
        actualizarListaBebidas();
    }




    // Método para actualizar la lista de bebidas
    private void actualizarListaBebidas() {
        // Realiza la carga de bebidas nuevamente
        int restauranteId = getArguments().getInt("restaurante_id", -1);
        new GetBebidasTask().execute(restauranteId);
    }

    private class GetBebidasTask extends AsyncTask<Integer, Void, List<Bebida>> {
        @Override
        protected List<Bebida> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Bebida> bebidas = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String modeloURL = Config.MODELO_URL + "listarBebidas.php?restaurante_id=" + idRestaurante;
                Log.d("dueno_bebidas_url", "apiUrl: " + modeloURL);
                String jsonResponse = HttpUtils.get(modeloURL);
                Log.d("dueno_bebidas_JSON_RESPONSE", jsonResponse);

                // Procesar el JSON y obtener la lista de bebidas
                bebidas = parseBebidasFromJSON(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bebidas;
        }

        @Override
        protected void onPostExecute(List<Bebida> bebidas) {
            // Configurar el RecyclerView con la lista de bebidas
            RecyclerView recyclerViewBebidas = getView().findViewById(R.id.viewbebidas);
            BebidaAdapter bebidaAdapter = new BebidaAdapter(bebidas, false,0,null);
            recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewBebidas.setAdapter(bebidaAdapter);
        }
    }

    // Método para analizar el JSON y obtener una lista de bebidas
    private List<Bebida> parseBebidasFromJSON(String json) {
        List<Bebida> bebidas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bebidaJson = jsonArray.getJSONObject(i);
                int id_bebida = bebidaJson.getInt("id_mebeb");
                String nombre = bebidaJson.getString("nombre");
                String descripcion = bebidaJson.getString("descripcion");
                float precio = (float) bebidaJson.getDouble("precio");
                int disponible = bebidaJson.getInt("disponible");

                // La imagen en formato Base64 (si está disponible)
                String imagen = bebidaJson.getString("imagen");

                // Crear un objeto Bebida con los datos
                Bebida bebida = new Bebida(id_bebida, nombre, descripcion, precio, imagen, disponible, 0, null);
                bebidas.add(bebida);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bebidas;
    }
}
