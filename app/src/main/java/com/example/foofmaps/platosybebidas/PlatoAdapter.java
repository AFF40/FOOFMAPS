package com.example.foofmaps.platosybebidas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;

import java.util.List;

public class PlatoAdapter extends RecyclerView.Adapter<PlatoAdapter.ViewHolder> {
    private List<Plato> platos;
    private boolean isFromSpecificActivity;

    public PlatoAdapter(List<Plato> platos, boolean isFromSpecificActivity) {
        this.platos = platos;
        this.isFromSpecificActivity = isFromSpecificActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (isFromSpecificActivity) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editplato_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plato_item, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plato plato = platos.get(position);

        // Configuración de la vista con los datos del plato
        holder.nombreTextView.setText(plato.getNombre());
        holder.descripcionTextView.setText(plato.getDescripcion());
        holder.precioTextView.setText(String.valueOf(plato.getPrecio() + " Bs."));
        Bitmap imagenBitmap = BitmapFactory.decodeByteArray(plato.getImagen(), 0, plato.getImagen().length);
        holder.imagenImageView.setImageBitmap(imagenBitmap);

        if (plato.getDisponible() == 1) {
            holder.ic.setImageResource(R.drawable.en_stock);
        } else {
            holder.ic.setImageResource(R.drawable.agotado);
        }

        if (isFromSpecificActivity) {
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPlatoClickListener != null) {
                        onPlatoClickListener.onPlatoClick(plato);
                    }
                }
            });

            holder.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onUpdatePlatoClickListener != null) {
                        onUpdatePlatoClickListener.onUpdatePlatoClick(plato);

                        Log.d("esteplatodisponible", String.valueOf(plato.getDisponible()));

                        String modeloURL = Config.MODELO_URL + "cambiar_estado_plato.php?id_comida=" + plato.getId();
                        Log.d("url", "serverUrldeesteplato: " + modeloURL);

                        // Crea una cola de solicitudes Volley
                        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

                        // Crea una solicitud GET utilizando StringRequest
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, modeloURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Maneja la respuesta del servidor si es necesario
                                // Aquí puedes actualizar el estado de plato según la respuesta del servidor si es necesario
                                if (response.equals("success")) {
                                    plato.setDisponible(1 - plato.getDisponible()); // Cambia el estado
                                    notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Maneja los errores de la solicitud
                                Log.e("Error", "Error en la solicitud HTTP: " + error.getMessage());
                            }
                        });

                        // Agrega la solicitud a la cola de solicitudes
                        requestQueue.add(stringRequest);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return platos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreTextView;
        public TextView descripcionTextView;
        public TextView precioTextView;
        public ImageView imagenImageView;
        public ImageView ic;
        public TextView disponibleTextView;
        public View button;
        public View button2;

        public ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.boton_editar_plato);
            button2 = itemView.findViewById(R.id.btn_cambiar_disponibilidad);
            nombreTextView = itemView.findViewById(R.id.nom_plato);
            descripcionTextView = itemView.findViewById(R.id.desc_plato);
            precioTextView = itemView.findViewById(R.id.precio);
            imagenImageView = itemView.findViewById(R.id.imageViewPlato);
            ic = itemView.findViewById(R.id.icon_disponible);
        }
    }

    public interface OnPlatoClickListener {
        void onPlatoClick(Plato plato);
    }

    private OnPlatoClickListener onPlatoClickListener;

    public void setOnPlatoClickListener(OnPlatoClickListener listener) {
        this.onPlatoClickListener = listener;
    }

    public interface OnUpdatePlatoClickListener {
        void onUpdatePlatoClick(Plato plato);
    }

    private OnUpdatePlatoClickListener onUpdatePlatoClickListener;

    public void setOnUpdatePlatoClickListener(OnUpdatePlatoClickListener listener) {
        this.onUpdatePlatoClickListener = listener;
    }
}
