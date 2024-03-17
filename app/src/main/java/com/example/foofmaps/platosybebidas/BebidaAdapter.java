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
import com.example.foofmaps.dueño.Editarestabebida;

import java.util.List;

public class BebidaAdapter extends RecyclerView.Adapter<BebidaAdapter.ViewHolder> {
    private List<Bebida> bebidas;
    boolean isFromSpecificActivity;

    public BebidaAdapter(List<Bebida> bebidas,boolean isFromSpecificActivity) {
        this.bebidas = bebidas;
        this.isFromSpecificActivity = isFromSpecificActivity;
    }

    @NonNull
    @Override
    public BebidaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;


        if (isFromSpecificActivity) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editbebida_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bebida_item, parent, false);
        }

        return new BebidaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BebidaAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bebida bebida = bebidas.get(holder.getAdapterPosition());

        // Configuración de la vista con los datos del bebida
        holder.nombreTextView.setText(bebida.getNombre());
        holder.descripcionTextView.setText(bebida.getDescripcion());
        holder.precioTextView.setText(String.valueOf(bebida.getPrecio() + " Bs."));
        String ip = Config.ip;
        //convertir el localhost a la ip de la maquina
        bebida.setImagen(bebida.getImagen().replace("http://localhost", ip));
        Log.d("platoimagen", String.valueOf(bebida.getImagen()));
        // Cargar la imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(bebida.getImagen())
                .into(holder.imagenImageView);
        if (bebida.getDisponible() == 1) {
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
                    Intent intent = new Intent(context, Editarestabebida.class);

                    // Agrega los datos del bebida como extras en el Intent
                    intent.putExtra("id_comida", bebida.getId());
                    intent.putExtra("nombre_bebida", bebida.getNombre());
                    intent.putExtra("descripcion_bebida", bebida.getDescripcion());
                    intent.putExtra("precio_bebida", bebida.getPrecio());
                    intent.putExtra("imagen_bebida", bebida.getImagen());
                    Log.d("bebidaimagen", String.valueOf(bebida.getImagen()));
                    Log.d("bebidaimagen", String.valueOf(bebida.getNombre()));
                    Log.d("bebidaimagen", String.valueOf(bebida.getDescripcion()));
                    Log.d("bebidaimagen", String.valueOf(bebida.getPrecio()));
                    Log.d("bebidaimagen", String.valueOf(bebida.getId()));

                    // Inicia la nueva Activity editar este bebida
                    context.startActivity(intent);

                }
            });

            //boton para cambiar disponibilidad
            holder.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onUpdateBebidaClickListener != null) {
                        onUpdateBebidaClickListener.onUpdateBebidaClick(bebida);

                        bebida.setDisponible(1 - bebida.getDisponible());

                        // Configurar la imagen en función del estado del bebida
                        if (bebida.getDisponible() == 1) {
                            // Cambiar el estado del bebida en la lista
                            holder.ic.setImageResource(R.drawable.agotado);
                            // Notifica al adaptador que los datos han cambiado para que actualice la vista
                            notifyItemChanged(position);
                        } else if(bebida.getDisponible() == 0) {
                            // Cambiar el estado del bebida en la lista
                            holder.ic.setImageResource(R.drawable.en_stock);
                            // Notifica al adaptador que los datos han cambiado para que actualice la vista
                            notifyItemChanged(position);
                        }

                        Log.d("estabebidadisponible", String.valueOf(bebida.getDisponible()));

                        // Crea la URL para cambiar el estado del bebida
                        String modeloURL = Config.MODELO_URL + "cambiar_estado_bebida.php?id_bebida=" + bebida.getId();
                        Log.d("url", "serverUrldeestabebida: " + modeloURL);

                        // Crea una cola de solicitudes Volley
                        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                        // Crea una solicitud GET utilizando StringRequest
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, modeloURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Maneja la respuesta del servidor si es necesario
                                // Aquí puedes actualizar el estado de bebida según la respuesta del servidor si es necesario
                                if (response.equals("success")) {
                                    bebida.setDisponible(1 - bebida.getDisponible()); // Cambia el estado
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

            //eliminar bebida
            holder.button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Eliminar bebida");
                    builder.setMessage("¿Está seguro de que desea eliminar esta bebida?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Lógica para eliminar el bebida
                            if (onDeleteBebidaClickListener != null) {
                                onDeleteBebidaClickListener.onDeleteBebidaClick(bebida);
                            }
                            // Elimina la bebida de la lista y notifica al adaptador
                            removeBebida(bebida);
                            // Crea la URL para eliminar el bebida
                            String eliminarBebidaURL = Config.MODELO_URL + "eliminar_bebida.php?id_bebida=" + bebida.getId();

                            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());

                            StringRequest eliminarBebidaRequest = new StringRequest(Request.Method.GET, eliminarBebidaURL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.equals("success")) {
                                        // Eliminación exitosa
                                        Toast.makeText(view.getContext(), "Eliminación exitosa", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // La eliminación falló, maneja el caso de error si es necesario
                                        // Puedes considerar restaurar el bebida en caso de error en la eliminación
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Error", "Error en la solicitud HTTP: " + error.getMessage());
                                }
                            });

                            requestQueue.add(eliminarBebidaRequest);
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
        return bebidas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreTextView;
        public TextView descripcionTextView;
        public TextView precioTextView;
        public ImageView imagenImageView;
        public ImageView ic;
        public View button;
        public View button2;
        public View button3;

        public ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.boton_editar_bebida);
            button2 = itemView.findViewById(R.id.btn_cambiar_disponibilidad);
            button3 = itemView.findViewById(R.id.boton_eliminar_bebida);
            nombreTextView = itemView.findViewById(R.id.nom_bebida);
            descripcionTextView = itemView.findViewById(R.id.desc_bebida);
            precioTextView = itemView.findViewById(R.id.precio);
            imagenImageView = itemView.findViewById(R.id.imageViewBebida);
            ic = itemView.findViewById(R.id.icon_disponible);
        }
    }

    // Método para actualizar la lista de bebidas
    public void setBebidas(List<Bebida> bebidas) {
        this.bebidas = bebidas;
        notifyDataSetChanged();
    }
    public Bebida getBebidaAtPosition(int position) {
        if (position >= 0 && position < bebidas.size()) {
            return bebidas.get(position);
        } else {
            return null;
        }
    }

    // Botón para editar bebida
    public interface OnBebidaClickListener {
        void onBebidaClick(Bebida bebida);
    }

    private BebidaAdapter.OnBebidaClickListener onBebidaClickListener;

    public void setOnBebidaClickListener(BebidaAdapter.OnBebidaClickListener listener) {
        this.onBebidaClickListener = listener;
    }

    // Botón para cambiar disponibilidad
    private BebidaAdapter.OnUpdateBebidaClickListener onUpdateBebidaClickListener;

    public interface OnUpdateBebidaClickListener {
        void onUpdateBebidaClick(Bebida bebida);
    }

    public void setOnUpdateBebidaClickListener(BebidaAdapter.OnUpdateBebidaClickListener listener) {
        this.onUpdateBebidaClickListener = listener;
    }

    // Botón para eliminar bebida
    private BebidaAdapter.OnDeleteBebidaClickListener onDeleteBebidaClickListener;

    public interface OnDeleteBebidaClickListener {
        void onDeleteBebidaClick(Bebida bebida);
    }

    public void setOnDeleteBebidaClickListener(BebidaAdapter.OnDeleteBebidaClickListener listener) {
        this.onDeleteBebidaClickListener = listener;
    }

    // Método para eliminar un bebida y notificar al adaptador
    public void removeBebida(Bebida bebida) {
        int position = bebidas.indexOf(bebida);
        if (position != -1) {
            bebidas.remove(position);
            notifyItemRemoved(position);
        }
    }
}


