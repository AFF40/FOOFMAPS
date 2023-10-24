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

import java.util.List;

public class BebidaAdapter extends RecyclerView.Adapter<BebidaAdapter.ViewHolder> {
    private List<Bebida> bebidas;

    public BebidaAdapter(List<Bebida> bebidas) {
        this.bebidas = bebidas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bebida_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bebida bebida = bebidas.get(position);
        Log.d("PLATO_NAME", bebida.getNombre()); // Agregar esta línea
        Log.d("PLATO_DESCRIPTION", bebida.getDescripcion()); // Agregar esta línea
        Log.d("PLATO_PRECIO", String.valueOf(bebida.getPrecio())); // Agregar esta línea


        // Asigna los datos del plato a las vistas en el diseño
        holder.nombreTextView.setText(bebida.getNombre());
        holder.descripcionTextView.setText(bebida.getDescripcion());
        holder.precioTextView.setText(String.valueOf(bebida.getPrecio()+" Bs."));

        // Convierte el array de bytes en un objeto Bitmap
        Bitmap imagenBitmap = BitmapFactory.decodeByteArray(bebida.getImagen(), 0, bebida.getImagen().length);

        // Establece el Bitmap en el ImageView
        holder.imagenImageView.setImageBitmap(imagenBitmap);

        // Establece el icono de disponibilidad
        if (bebida.getDisponible() == 1) {
            holder.ic.setImageResource(R.drawable.en_stock);
        } else {
            holder.ic.setImageResource(R.drawable.agotado);
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

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nom_bebida);
            descripcionTextView = itemView.findViewById(R.id.desc_bebida);
            precioTextView = itemView.findViewById(R.id.precio);
            imagenImageView = itemView.findViewById(R.id.imageViewBebida);
            ic = itemView.findViewById(R.id.icon_disponible);
        }
    }
}


