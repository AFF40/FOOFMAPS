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
import com.example.foofmaps.platosybebidas.Bebida;
import com.example.foofmaps.platosybebidas.BebidaAdapter;

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

public class editar_bebida extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BebidaAdapter adapter;
    private boolean isFromSpecificActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_bebida);

        // Recuperar el id y nombre del restaurante
        int restauranteId = getIntent().getIntExtra("restaurante_id", 0);

        recyclerView = findViewById(R.id.recyclerEditBebidas); // Reemplaza con tu ID de RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Llama a la tarea asincrónica para obtener los datos
        new FetchBebidasTask().execute(restauranteId);
    }
    //actualizar la lista de bebidas al regresar a la actividad
    @Override
    protected void onResume() {
        super.onResume();
        //limpiar la lista de platos
        adapter = null;
        //recuperar el id del restaurante
        int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
        // Llama a la tarea asincrónica para obtener los datos
        new FetchBebidasTask().execute(restauranteId);
    }

    private class FetchBebidasTask extends AsyncTask<Integer, Void, List<Bebida>> {
        @Override
        protected List<Bebida> doInBackground(Integer... params) {
            int restauranteId = params[0];
            List<Bebida> bebidas = new ArrayList<>();

            try {
                // Construye la URL
                String modeloURL = Config.MODELO_URL + "listarBebidas.php?restaurante_id=" + restauranteId;
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
                    int id_bebida = jsonObject.getInt("id_mebeb");
                    String nombre_bebida = jsonObject.getString("nombre");
                    String descripcion_bebida = jsonObject.getString("descripcion");
                    double precio_bebida = jsonObject.getDouble("precio");
                    String imagen_bebida = jsonObject.getString("imagen");
                    int disponible_bebida = jsonObject.getInt("disponible");

                    Bebida bebida = new Bebida(id_bebida, nombre_bebida, descripcion_bebida, precio_bebida, imagen_bebida, disponible_bebida, 0, null);
                    bebidas.add(bebida);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return bebidas;
        }

        @Override
        protected void onPostExecute(List<Bebida> bebidas) {
            super.onPostExecute(bebidas);
            if (adapter == null) {
                //recuperar el id del restaurante
                int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
                String nombreRestaurante = getIntent().getStringExtra("nombre_restaurante");
                adapter = new BebidaAdapter(bebidas, isFromSpecificActivity, restauranteId, nombreRestaurante);
                Log.d("log_editarbebida_aladapter", String.valueOf(restauranteId));
                Log.d("log_editarbebida_aladapter", nombreRestaurante);

                adapter.setOnBebidaClickListener(new BebidaAdapter.OnBebidaClickListener() {
                    @Override
                    public void onBebidaClick(Bebida bebida) {
                        // Obtener los datos del restaurante correspondientes a la bebida seleccionada
                        int restauranteId = getIntent().getIntExtra("restaurante_id", 0);
                        String nombreRestaurante = getIntent().getStringExtra("nombre_restaurante");

                        // Crear el intent para abrir la actividad Editarestabebida
                        Intent intent = new Intent(editar_bebida.this, Editarestabebida.class);

                        // Agregar los datos de la bebida al intent
                        intent.putExtra("id_mebeb", bebida.getId());
                        intent.putExtra("nombre_bebida", bebida.getNombre());
                        intent.putExtra("descripcion_bebida", bebida.getDescripcion());
                        intent.putExtra("precio_bebida", bebida.getPrecio());
                        intent.putExtra("imagen_bebida", bebida.getImagen());
                        intent.putExtra("disponible_bebida", bebida.getDisponible());

                        // Agregar los datos del restaurante al intent
                        intent.putExtra("restaurante_id", restauranteId);
                        intent.putExtra("nombre_restaurante", nombreRestaurante);

                        // Iniciar la actividad Editarestabebida
                        startActivity(intent);

                        // Logs de todos los datos
                        Log.d("log_editarbebida_id_bebida", String.valueOf(bebida.getId()));
                        Log.d("log_editarbebida_nombre_bebida", bebida.getNombre());
                        Log.d("log_editarbebida_descripcion_bebida", bebida.getDescripcion());
                        Log.d("log_editarbebida_precio_bebida", String.valueOf(bebida.getPrecio()));
                        Log.d("log_editarbebida_imagen_bebida", bebida.getImagen());
                        Log.d("log_editarbebida_disponible_bebida", String.valueOf(bebida.getDisponible()));
                        Log.d("log_editarbebida_restaurante_id", String.valueOf(restauranteId));
                        Log.d("log_editarbebida_nombre_restaurante", nombreRestaurante);
                    }
                });adapter.setOnUpdateBebidaClickListener(new BebidaAdapter.OnUpdateBebidaClickListener() {
                    @Override
                    public void onUpdateBebidaClick(Bebida bebida) {

                    }
                });

                recyclerView.setAdapter(adapter);
            } else {
                adapter.setBebidas(bebidas);
            }
        }
    }
}
