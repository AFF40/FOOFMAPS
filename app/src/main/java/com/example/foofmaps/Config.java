    package com.example.foofmaps;

    public class Config {
        // configuracion para el servidor cwpanel

        public static final String ip = "https://aflores2.infocalcbba.org";
        // Asegúrate de que las rutas sean correctas según la estructura de tu servidor en cPanel
        public static final String MODELO_URL = ip + "/FMbackend//modelo/";
        public static final String CONTROLADOR_URL = ip + "/FMbackend/web2/controlador/";


/*
        // configuracion para el servidor local
        public static final String ip = "http://192.168.100.5";
        // Asegúrate de que las rutas sean correctas según la estructura de tu servidor en cPanel
        public static final String MODELO_URL = ip + "/FMbackend/modelo/";
        public static final String CONTROLADOR_URL = ip + "/FMbackend/web2/controlador/";



 */
        // Cambia la IP local por tu subdominio de producción
       /*public static final String ip = "http://192.168.100.5:8000";
        // Asegúrate de que las rutas sean correctas según la estructura de tu servidor en cPanel
        public static final String MODELO_URL = ip + "/FMbackendlaravel/app/http/models";
        public static final String CONTROLADOR_URL = ip + "/FMbackendlaravel/app/http/Controllers/";
        public static final String API_URL = ip + "/api";*/
    }
