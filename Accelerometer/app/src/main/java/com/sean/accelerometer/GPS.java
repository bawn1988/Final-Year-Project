package com.sean.accelerometer;

import android.location.Location;
import android.os.Bundle;

/**
 * Created by Sean on 5/6/2017.
 */
public interface GPS {
    void onLocationChanged(Location location);

    void onStatusChanged(String s, int i, Bundle bundle);

    void onProviderEnabled(String s);

    void onProviderDisabled(String s);
}
