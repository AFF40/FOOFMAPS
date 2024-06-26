package com.example.foofmaps.clientes.restaurantes;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.foofmaps.Config;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    //conexion con el archivo php
    //private static final String controladorURL = Config.API_URL+"/login";

    private static final String controladorURL = Config.CONTROLADOR_URL+"controladorLogin.php";

    private final Map<String,String>params;
    public LoginRequest(String username, String password,  Response.Listener<String> listener){
        super(Method.POST, controladorURL,listener,null);
        Log.d("controladorURL",controladorURL);
        params=new HashMap<>();
        Log.e("info",username);
        Log.d("info",password);
        Log.d("urlLogin",controladorURL);

        params.put("username",username);
        params.put("password",password);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }

}