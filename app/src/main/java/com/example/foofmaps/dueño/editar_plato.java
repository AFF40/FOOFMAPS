package com.example.foofmaps.dueño;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.platosybebidas.Plato;
import com.example.foofmaps.platosybebidas.PlatoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class editar_plato extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlatoAdapter adapter;
    private boolean isFromSpecificActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("log_editarplato_estado", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_plato);

        // Recuperar el id y nombre del restaurante
        int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
        String nombreRestaurante = getIntent().getStringExtra("nombre_restaurante");
        Log.d("log_editarplato_restaurante_id_recibido", String.valueOf(restauranteId));
        Log.d("log_editarplato_nombre_restaurante_recibido", nombreRestaurante);

        recyclerView = findViewById(R.id.recylerEditarPlatos); // Reemplaza con tu ID de RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Llama a la tarea asincrónica para obtener los datos
        new FetchPlatosTask().execute(restauranteId);
    }


    //actualizar la lista de platos al regresar a la actividad
    @Override
    protected void onRestart() {
        super.onRestart();
        //limpiar la lista de platos
        adapter = null;
        //recuperar el id del restaurante
        int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
        // Llama a la tarea asincrónica para obtener los datos
        new FetchPlatosTask().execute(restauranteId);
    }


    private class FetchPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            int restauranteId = params[0];
            List<Plato> platos = new ArrayList<>();

            try {
                // Construye la URL
                String modeloURL = Config.MODELO_URL + "getPlatos.php?restaurante_id=" + restauranteId;
                URL url = new URL(modeloURL);
                Log.d("urledit", "apiUrl: " + modeloURL);

                // Realiza la solicitud HTTP
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                StringBuilder result = new StringBuilder();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }

                // Analiza los datos JSON
                JSONArray jsonArray = new JSONArray(result.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id_comida = jsonObject.getInt("id_plato");
                    String nombre_plato = jsonObject.getString("nombre");
                    String descripcion_plato = jsonObject.getString("descripcion");
                    double precio_plato = jsonObject.getDouble("precio");
                    String imagen_plato = jsonObject.getString("imagen");
                    int disponible_plato = jsonObject.getInt("disponible");

                    Plato plato = new Plato(id_comida, nombre_plato, descripcion_plato, precio_plato, imagen_plato, disponible_plato,0,null);
                    platos.add(plato);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return platos;
        }

        @Override
        protected void onPostExecute(List<Plato> platos) {
            super.onPostExecute(platos);
            if (adapter == null) {
                //recuperar el id del restaurante
                int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
                String nombreRestaurante = getIntent().getStringExtra("nombre_restaurante");
                adapter = new PlatoAdapter(platos, isFromSpecificActivity,restauranteId,nombreRestaurante);
                Log.d("log_editarplato_aladapter", String.valueOf(restauranteId));
                Log.d("log_editarplato_aladapter", nombreRestaurante);
                // En el listener del botón para cada elemento de la lista
                adapter.setOnPlatoClickListener(new PlatoAdapter.OnPlatoClickListener() {
                    @Override
                    public void onPlatoClick(Plato plato) {
                        // Obtener los datos del restaurante correspondientes al plato seleccionado
                        int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
                        String nombreRestaurante = getIntent().getStringExtra("nombre_restaurante");

                        // Crear el intent para abrir la actividad Editaresteplato
                        Intent intent = new Intent(editar_plato.this, Editaresteplato.class);

                        // Agregar los datos del plato al intent
                        intent.putExtra("id_comida", plato.getId());
                        intent.putExtra("nombre_plato", plato.getNombre());
                        intent.putExtra("descripcion_plato", plato.getDescripcion());
                        intent.putExtra("precio_plato", plato.getPrecio());
                        intent.putExtra("imagen_plato", plato.getImagen());
                        intent.putExtra("disponible_plato", plato.getDisponible());

                        // Agregar los datos del restaurante al intent
                        intent.putExtra("restaurante_id", restauranteId);
                        intent.putExtra("nombre_restaurante", nombreRestaurante);

                        // Iniciar la actividad Editaresteplato
                        startActivity(intent);

                        // Logs de todos los datos
                        Log.d("log_editarplato_id_comida", String.valueOf(plato.getId()));
                        Log.d("log_editarplato_nombre_plato", plato.getNombre());
                        Log.d("log_editarplato_descripcion_plato", plato.getDescripcion());
                        Log.d("log_editarplato_precio_plato", String.valueOf(plato.getPrecio()));
                        Log.d("log_editarplato_imagen_plato", plato.getImagen());
                        Log.d("log_editarplato_disponible_plato", String.valueOf(plato.getDisponible()));
                        Log.d("log_editarplato_restaurante_id_enviado", String.valueOf(restauranteId));
                        Log.d("log_editarplato_nombre_restaurante_enviado", nombreRestaurante);
                    }
                });

                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
