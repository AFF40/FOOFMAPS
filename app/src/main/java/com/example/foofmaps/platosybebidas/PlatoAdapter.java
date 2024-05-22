package com.example.foofmaps.platosybebidas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;
import com.example.foofmaps.dueño.Editaresteplato;

import java.util.List;

public class PlatoAdapter extends RecyclerView.Adapter<PlatoAdapter.ViewHolder> {
    private List<Plato> platos;
    private boolean isFromSpecificActivity;
    private int restauranteId;
    private String nombreRestaurante;

    public PlatoAdapter(List<Plato> platos, boolean isFromSpecificActivity, int restauranteId, String nombreRestaurante) {
        this.platos = platos;
        this.isFromSpecificActivity = isFromSpecificActivity;
        this.restauranteId = restauranteId;
        this.nombreRestaurante = nombreRestaurante;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Plato plato = platos.get(holder.getAdapterPosition());

        // Configuración de la vista con los datos del plato
        holder.nombreTextView.setText(plato.getNombre());
        holder.descripcionTextView.setText(plato.getDescripcion());
        holder.precioTextView.setText(String.valueOf(plato.getPrecio() + " Bs."));
        String ip = Config.ip;

        //convertir el localhost a la ip de la maquina
        plato.setImagen(plato.getImagen().replace("http://localhost", ip));
        Log.d("platoimagen", String.valueOf(plato.getImagen()));
        // Establecer el id y el nombre del restaurante en el objeto Plato
        plato.setRestaurante_id(restauranteId);
        plato.setNombre_restaurante(nombreRestaurante);
        // Cargar la imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(plato.getImagen())
                .into(holder.imagenImageView);

        if (plato.getDisponible() == 1) {
            holder.ic.setImageResource(R.drawable.en_stock);
        } else {
            holder.ic.setImageResource(R.drawable.agotado);
        }

        if (isFromSpecificActivity) {
            // Agrega un OnClickListener al botón del elemento
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crea un Intent para abrir la nueva Activity
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Editaresteplato.class);

                    // Agrega los datos del plato y del restaurante como extras en el Intent
                    intent.putExtra("id_comida", plato.getId());
                    intent.putExtra("nombre_plato", plato.getNombre());
                    intent.putExtra("descripcion_plato", plato.getDescripcion());
                    intent.putExtra("precio_plato", plato.getPrecio());
                    intent.putExtra("imagen_plato", plato.getImagen());
                    intent.putExtra("restaurante_id", plato.getRestaurante_id());
                    intent.putExtra("nombre_restaurante", plato.getNombre_restaurante());

                    // Inicia la nueva Activity editar este plato
                    context.startActivity(intent);
                }
            });

            //boton para cambiar disponibilidad
            holder.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onUpdatePlatoClickListener != null) {
                        onUpdatePlatoClickListener.onUpdatePlatoClick(plato);

                        plato.setDisponible(1 - plato.getDisponible());

                        // Configurar la imagen en función del estado del plato
                        if (plato.getDisponible() == 1) {
                            // Cambiar el estado del plato en la lista
                            holder.ic.setImageResource(R.drawable.agotado);
                            // Notifica al adaptador que los datos han cambiado para que actualice la vista
                            notifyItemChanged(position);
                        } else if(plato.getDisponible() == 0) {
                            // Cambiar el estado del plato en la lista
                            holder.ic.setImageResource(R.drawable.en_stock);
                            // Notifica al adaptador que los datos han cambiado para que actualice la vista
                            notifyItemChanged(position);
                        }

                        Log.d("esteplatodisponible", String.valueOf(plato.getDisponible()));

                        // Crea la URL para cambiar el estado del plato
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

            holder.button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Eliminar plato");
                    builder.setMessage("¿Está seguro de que desea eliminar este plato?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Lógica para eliminar el plator
                            if (onDeletePlatoClickListener != null) {
                                onDeletePlatoClickListener.onDeletePlatoClick(plato);
                            }
                            // Elimina el plato de la lista y notifica al adaptador
                            removePlato(plato);
                            // Crea la URL para eliminar el plato
                            String eliminarPlatoURL = Config.MODELO_URL + "eliminar_plato.php?id_comida=" + plato.getId();

                            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

                            StringRequest eliminarPlatoRequest = new StringRequest(Request.Method.GET, eliminarPlatoURL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("success")) {
                                        // Eliminación exitosa
                                        Toast.makeText(view.getContext(), "Eliminación exitosa", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // La eliminación falló, maneja el caso de error si es necesario
                                        // Puedes considerar restaurar el plato en caso de error en la eliminación
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Error", "Error en la solicitud HTTP: " + error.getMessage());
                                }
                            });

                            requestQueue.add(eliminarPlatoRequest);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
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
        public View button3;

        public ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.boton_editar_plato);
            button2 = itemView.findViewById(R.id.btn_cambiar_disponibilidad);
            button3 = itemView.findViewById(R.id.boton_eliminar_plato);
            nombreTextView = itemView.findViewById(R.id.nom_plato);
            descripcionTextView = itemView.findViewById(R.id.desc_plato);
            precioTextView = itemView.findViewById(R.id.precio);
            imagenImageView = itemView.findViewById(R.id.imageViewPlato);
            ic = itemView.findViewById(R.id.icon_disponible);
        }
    }
    // Método para actualizar la lista de platos
    public void setPlatos(List<Plato> platos) {
        this.platos = platos;
        notifyDataSetChanged();
    }
    public Plato getPlatoAtPosition(int position) {
        if (position >= 0 && position < platos.size()) {
            return platos.get(position);
        } else {
            return null;
        }
    }

    // Botón para editar plato
    public interface OnPlatoClickListener {
        void onPlatoClick(Plato plato);
    }

    private OnPlatoClickListener onPlatoClickListener;

    public void setOnPlatoClickListener(OnPlatoClickListener listener) {
        this.onPlatoClickListener = listener;
    }

    // Botón para cambiar disponibilidad
    private OnUpdatePlatoClickListener onUpdatePlatoClickListener;

    public interface OnUpdatePlatoClickListener {
        void onUpdatePlatoClick(Plato plato);
    }

    public void setOnUpdatePlatoClickListener(OnUpdatePlatoClickListener listener) {
        this.onUpdatePlatoClickListener = listener;
    }

    // Botón para eliminar plato
    private OnDeletePlatoClickListener onDeletePlatoClickListener;

    public interface OnDeletePlatoClickListener {
        void onDeletePlatoClick(Plato plato);
    }

    public void setOnDeletePlatoClickListener(OnDeletePlatoClickListener listener) {
        this.onDeletePlatoClickListener = listener;
    }

    // Método para eliminar un plato y notificar al adaptador
    public void removePlato(Plato plato) {
        int position = platos.indexOf(plato);
        if (position != -1) {
            platos.remove(position);
            notifyItemRemoved(position);
        }
    }
}
