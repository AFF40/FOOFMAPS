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

import com.example.foofmaps.R;

import java.net.HttpURLConnection;
import java.net.URL;
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
                        // URL para cambiar el estado del plato en el servidor
                        String serverUrl = "http://192.168.1.3/modelo/cambiar_estado_plato.php?id_comida=" + plato.getId();

                        Log.d("url", "serverUrldeesteplato: " + serverUrl);

                        // Aquí puedes realizar la solicitud HTTP a la URL "serverUrl" utilizando una biblioteca de red como Retrofit, Volley, o HttpURLConnection.

                        // Asegúrate de manejar la solicitud HTTP de manera segura y manejar los errores y las respuestas del servidor de acuerdo a tus necesidades.

                        try {
                            URL urlObject = new URL(serverUrl);
                            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                            connection.setRequestMethod("GET");

                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                // La solicitud se realizó con éxito, puedes realizar acciones adicionales si es necesario.
                            } else {
                                // Ocurrió un error al realizar la solicitud.
                                // Puedes manejar el error de la manera que prefieras.
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
