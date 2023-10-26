package com.example.foofmaps.dueño;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    // ...
    private RecyclerView recyclerView;
    private PlatoAdapter adapter;

    boolean isFromSpecificActivity = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_plato);


        recyclerView = findViewById(R.id.recylerEditarPlatos); // Reemplaza con tu ID de RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener el restauranteId de la actividad anterior (ajusta según cómo lo estés pasando)
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
                String apiUrl = "http://192.168.1.3/modelo/getPlatos.php?restaurante_id=" + restauranteId;
                URL url = new URL(apiUrl);
                Log.d("urledit", "apiUrl: " + apiUrl);

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
                    int id_comida = jsonObject.getInt("id_comida");
                    String nombre_plato = jsonObject.getString("nom_plato");
                    String descripcion_plato = jsonObject.getString("descripcion");
                    double precio_plato = jsonObject.getDouble("precio");
                    byte[] imagen_plato = Base64.decode(jsonObject.getString("imagen"), Base64.DEFAULT);
                    int disponible_plato = jsonObject.getInt("disponible");

                    Plato plato = new Plato(id_comida, nombre_plato, descripcion_plato, precio_plato, imagen_plato, disponible_plato);
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
            // Establece isFromSpecificActivity en true

            // Crea una instancia del adaptador pasando el valor de isFromSpecificActivity
            adapter = new PlatoAdapter(platos, isFromSpecificActivity);
            recyclerView.setAdapter(adapter);
        }
    }
}
