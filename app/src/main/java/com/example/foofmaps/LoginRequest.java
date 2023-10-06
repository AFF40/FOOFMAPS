package com.example.foofmaps;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    //conexion con el archivo php
    private static final String Login_Request_URL="http://192.168.100.179/web2/controlador/controladorLogin.php";
    private final Map<String,String>params;
    public LoginRequest(String username, String pass1,  Response.Listener<String> listener){
        super(Method.POST, Login_Request_URL,listener,null);
        params=new HashMap<>();
        Log.e("info",username);
        params.put("username",username);
        params.put("pass1",pass1);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}