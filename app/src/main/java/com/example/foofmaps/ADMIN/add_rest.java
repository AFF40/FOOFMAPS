package com.example.foofmaps.ADMIN;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.foofmaps.Config;
import com.example.foofmaps.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class add_rest extends Fragment {

    private EditText usernameEditText;
    private EditText pass1EditText;
    private EditText tematicaEditText;
    private EditText nomRestEditText;
    private EditText celularEditText;
    private EditText ubicacionEditText;
    private EditText celularRestEditText;
    private ImageView imagen_rest;
    private Button registrarDueñoButton;
    private Button registrarRestauranteButton;

    private Bitmap imageBitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    public add_rest(int id_usuario) {
        // Constructor
        Log.d("Log_add_rest", "ID Usuario: " + id_usuario);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_rest, container, false);

        // Inicializar vistas
        usernameEditText = rootView.findViewById(R.id.username);
        celularEditText = rootView.findViewById(R.id.celular);
        pass1EditText = rootView.findViewById(R.id.password);
        nomRestEditText = rootView.findViewById(R.id.nom_rest);
        celularRestEditText = rootView.findViewById(R.id.celularRest);
        ubicacionEditText = rootView.findViewById(R.id.ubicacion);
        tematicaEditText = rootView.findViewById(R.id.tematica);
        imagen_rest = rootView.findViewById(R.id.imagen);
        registrarDueñoButton = rootView.findViewById(R.id.registrarDueñoButton);
        registrarRestauranteButton = rootView.findViewById(R.id.registrarRestauranteButton);

        // Manejar el clic en la imagen para mostrar el diálogo de selección
        imagen_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });

        registrarDueñoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener datos de los campos
                String username = usernameEditText.getText().toString();
                String celular = celularEditText.getText().toString();
                String password = pass1EditText.getText().toString();
                Log.d("add_rest_user", usernameEditText.getText().toString());
                Log.d("add_rest_cel", celularEditText.getText().toString());
                Log.d("add_rest_pass", pass1EditText.getText().toString());
                Log.d("add_rest_nom", nomRestEditText.getText().toString());
                Log.d("add_rest_celRest", celularRestEditText.getText().toString());
                Log.d("add_rest_ubic", ubicacionEditText.getText().toString());

                // Crear una cola de solicitudes Volley
                RequestQueue queue = Volley.newRequestQueue(requireActivity());

                // Crear una solicitud de cadena (POST)
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.MODELO_URL + "reg_dueno.php",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                // Procesar la respuesta del servidor (éxito)
                                showToast("Registro exitoso: " + response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Procesar la respuesta del servidor (error)
                                showToast("Error en el registro. Por favor, inténtalo de nuevo."+ error );
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Agregar los parámetros del formulario
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("celular", celular);
                        params.put("passwordD", password);
                        return params;
                    }
                };
                Log.d("add_rest_url", Config.MODELO_URL + "reg_dueno.php");

                // Agregar la solicitud a la cola
                queue.add(stringRequest);
            }
        });

        // Dentro de registrarRestauranteButton.setOnClickListener
        registrarRestauranteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener datos del segundo formulario
                String nomRest = nomRestEditText.getText().toString();
                String celular = celularEditText.getText().toString();
                String celularRest = celularRestEditText.getText().toString();
                String ubicacion = ubicacionEditText.getText().toString();
                String tematica = tematicaEditText.getText().toString();

                // Convertir la imagen a Base64
                String imageBase64 = bitmapToBase64(imageBitmap);

                // Obtener el id_usuario de las SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int id_usuario = sharedPreferences.getInt("id_usuario", -1);

                // Crear una cola de solicitudes Volley
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                // Crear una solicitud de cadena (POST) para el segundo formulario
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.MODELO_URL + "reg_rest.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Procesar la respuesta del servidor (éxito)
                                showToast(response);
                                Log.d("RESPUESTA_SERVIDOR", response); // Agregar esta línea para ver la respuesta del servidor en el logcat
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Procesar la respuesta del servidor (error)
                                showToast("Error en el registro del restaurante. Por favor, inténtalo de nuevo.");
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Agregar los parámetros del formulario del restaurante
                        Map<String, String> params = new HashMap<>();
                        params.put("username", usernameEditText.getText().toString());
                        params.put("password", pass1EditText.getText().toString());
                        params.put("nom_rest", nomRest);
                        params.put("celular", celular);
                        params.put("celularRest", celularRest);
                        params.put("ubicacion", ubicacion);
                        params.put("tematica", tematica);
                        params.put("id_admin", String.valueOf(id_usuario)); // Agregar el id_usuario
                        params.put("imagen", imageBase64); // Agregar la imagen en Base64

                        Log.d("PARAMSagregar_rest", params.toString());
                        return params;
                    }
                };

// Agregar la solicitud a la cola
                queue.add(stringRequest);
            }
        });

        return rootView;
    }

    private void showImageSelectionDialog() {
        final CharSequence[] items = {"Cargar desde Galería", "Cargar mediante la Cámara"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione un método de carga");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
// Cargar desde Galería
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } else if (item == 1) {
// Cargar mediante la Cámara
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
// Procesar la imagen seleccionada desde la galería
            Uri selectedImageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
// Asignar la imagen al ImageView
                imagen_rest.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
// Procesar la imagen capturada desde la cámara
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
// Asignar la imagen al ImageView
            imagen_rest.setImageBitmap(imageBitmap);
        }
    }

    private void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
