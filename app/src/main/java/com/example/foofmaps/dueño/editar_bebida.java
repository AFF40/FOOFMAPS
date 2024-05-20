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
    // ...
    private RecyclerView recyclerView;
    private BebidaAdapter adapter;

    boolean isFromSpecificActivity = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_bebida);


        recyclerView = findViewById(R.id.recyclerEditBebidas); // Reemplaza con tu ID de RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener el restauranteId de la actividad anterior (ajusta según cómo lo estés pasando)
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
                String modeloURL = Config.MODELO_URL+"getBebidas.php?restaurante_id=" + restauranteId;
                URL url = new URL(modeloURL);
                Log.d("urledit", "apiUrl: " +modeloURL );

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
                    int id_bebida = jsonObject.getInt("id_bebida");
                    String nombre_bebida = jsonObject.getString("nombre");
                    String descripcion_bebida = jsonObject.getString("descripcion");
                    double precio_bebida = jsonObject.getDouble("precio");
                    String imagen_bebida = jsonObject.getString("imagen");
                    int disponible_bebida = jsonObject.getInt("disponible");

                    // Crea los items Bebida y agrégalo a la lista
                    Bebida bebida = new Bebida(id_bebida, nombre_bebida, descripcion_bebida, precio_bebida, imagen_bebida, disponible_bebida);
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
                adapter = new BebidaAdapter(bebidas, isFromSpecificActivity);
                adapter.setOnBebidaClickListener(new BebidaAdapter.OnBebidaClickListener() {
                    @Override
                    public void onBebidaClick(Bebida bebida) {
                        // Aquí abres el nuevo Activity y pasas los datos del bebida
                        Intent intent = new Intent(editar_bebida.this, Editarestabebida.class);
                        intent.putExtra("id_bebida", bebida.getId());
                        intent.putExtra("nombre", bebida.getNombre());
                        intent.putExtra("descripcion", bebida.getDescripcion());
                        intent.putExtra("precio", bebida.getPrecio());
                        intent.putExtra("imagen", bebida.getImagen());
                        startActivity(intent);

                    }
                });
                adapter.setOnUpdateBebidaClickListener(new BebidaAdapter.OnUpdateBebidaClickListener() {
                    @Override
                    public void onUpdateBebidaClick(Bebida bebida) {

                    }
                });
                recyclerView.setAdapter(adapter);
            } else {
                // Si ya existe un adaptador, actualiza la lista de bebidas
                adapter.notifyDataSetChanged();
            }


        }


    }
}