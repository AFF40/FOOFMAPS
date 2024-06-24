// PlatosViewModel.java
package com.example.foofmaps.clientes.restaurantes.fragments.viewmodel;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foofmaps.Config;
import com.example.foofmaps.HttpUtils;
import com.example.foofmaps.platosybebidas.Plato;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlatosViewModel extends ViewModel {
    private MutableLiveData<List<Plato>> platosLiveData;

    public LiveData<List<Plato>> getPlatos(int restauranteId) {
        if (platosLiveData == null) {
            platosLiveData = new MutableLiveData<>();
            loadPlatos(restauranteId);
        }
        return platosLiveData;
    }

    private void loadPlatos(int restauranteId) {
        new GetPlatosTask().execute(restauranteId);
    }

    private class GetPlatosTask extends AsyncTask<Integer, Void, List<Plato>> {
        @Override
        protected List<Plato> doInBackground(Integer... params) {
            int idRestaurante = params[0];
            List<Plato> platos = new ArrayList<>();

            try {
                // Realizar una solicitud HTTP para obtener los datos JSON de la API
                String modeloURL = Config.MODELO_URL + "listarPlatos.php?restaurante_id=" + idRestaurante;
                String jsonResponse = HttpUtils.get(modeloURL);
                Log.d("urledit", "apiUrl: " + modeloURL);
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
            platosLiveData.setValue(platos);
        }
    }

    private List<Plato> parsePlatosFromJSON(String json) {
        List<Plato> platos = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject platoJson = jsonArray.getJSONObject(i);
                int id_plato = platoJson.getInt("id_producto");
                String nombre = platoJson.getString("nombre");
                String descripcion = platoJson.getString("descripcion");
                float precio = (float) platoJson.getDouble("precio");
                int disponible = platoJson.getInt("disponible");
                String imagen = platoJson.getString("imagen");

                Plato plato = new Plato(id_plato, nombre, descripcion, precio, imagen, disponible, 0, null);
                platos.add(plato);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return platos;
    }
}
