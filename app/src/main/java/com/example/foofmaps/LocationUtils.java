package com.example.foofmaps;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtils {

    public static boolean areLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = true;
        boolean networkEnabled = true;
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gpsEnabled || networkEnabled;
    }

}
