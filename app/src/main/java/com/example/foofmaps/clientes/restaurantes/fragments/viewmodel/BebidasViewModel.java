// BebidasViewModel.java
package com.example.foofmaps.clientes.restaurantes.fragments.viewmodel;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foofmaps.Config;
import com.example.foofmaps.HttpUtils;
import com.example.foofmaps.platosybebidas.Bebida;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BebidasViewModel extends ViewModel {
    private MutableLiveData<List<Bebida>> bebidasLiveData;

    public LiveData<List<Bebida>> getBebidas(int restauranteId) {
        if (bebidasLiveData == null) {
            bebidasLiveData = new MutableLiveData<>();
            loadBebidas(restauranteId);
        }
        return bebidasLiveData;
    }

    private void loadBebidas(int restauranteId) {
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
            bebidasLiveData.setValue(bebidas);
        }
    }

    private List<Bebida> parseBebidasFromJSON(String json) {
        List<Bebida> bebidas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bebidaJson = jsonArray.getJSONObject(i);
                int id_bebida = bebidaJson.getInt("id_producto");
                String nombre = bebidaJson.getString("nombre");
                String descripcion = bebidaJson.getString("descripcion");
                float precio = (float) bebidaJson.getDouble("precio");
                int disponible = bebidaJson.getInt("disponible");
                String imagen = bebidaJson.getString("imagen");

                Bebida bebida = new Bebida(id_bebida, nombre, descripcion, precio, imagen, disponible, 0, null);
                bebidas.add(bebida);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bebidas;
    }
}
