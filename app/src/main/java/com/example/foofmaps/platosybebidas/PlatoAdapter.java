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

    public class PlatoAdapter extends RecyclerView.Adapter<PlatoAdapter.ViewHolder> {
        private List<Plato> platos;
        private boolean isFromSpecificActivity; // Nuevo miembro para indicar si proviene de la actividad específica


        public PlatoAdapter(List<Plato> platos, boolean isFromSpecificActivity) {
            this.platos = platos;
            this.isFromSpecificActivity = isFromSpecificActivity;
            // Resto del constructor
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (isFromSpecificActivity) {
                // Usa el diseño específico si proviene de la actividad específica
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editplato_item, parent, false);
            } else {
                // Usa un diseño predeterminado en caso contrario
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plato_item, parent, false);
            }

            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Plato plato = platos.get(position);
            Log.d("PLATO_NAME", plato.getNombre()); // Agregar esta línea
            Log.d("PLATO_DESCRIPTION", plato.getDescripcion()); // Agregar esta línea
            Log.d("PLATO_PRECIO", String.valueOf(plato.getPrecio())); // Agregar esta línea


            // Asigna los datos del plato a las vistas en el diseño
            holder.nombreTextView.setText(plato.getNombre());
            holder.descripcionTextView.setText(plato.getDescripcion());
            holder.precioTextView.setText(String.valueOf(plato.getPrecio()+" Bs."));

            // Convierte el array de bytes en un objeto Bitmap
            Bitmap imagenBitmap = BitmapFactory.decodeByteArray(plato.getImagen(), 0, plato.getImagen().length);

            // Establece el Bitmap en el ImageView
            holder.imagenImageView.setImageBitmap(imagenBitmap);

            // Establece el icono de disponibilidad
            if (plato.getDisponible() == 1) {
                holder.ic.setImageResource(R.drawable.en_stock);
            } else {
                holder.ic.setImageResource(R.drawable.agotado);
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

            public ViewHolder(View itemView) {
                super(itemView);
                nombreTextView = itemView.findViewById(R.id.nom_plato);
                descripcionTextView = itemView.findViewById(R.id.desc_plato);
                precioTextView = itemView.findViewById(R.id.precio);
                imagenImageView = itemView.findViewById(R.id.imageViewPlato);
                ic = itemView.findViewById(R.id.icon_disponible);
            }
        }
    }


