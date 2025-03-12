package com.example.gpsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class LocationReceiver extends BroadcastReceiver {

    private MapView mapView;
    private Polyline routeLine;
    private boolean shouldZoom;
    private boolean shouldDrawRoute;

    public LocationReceiver(MapView mapView, boolean shouldZoom, boolean shouldDrawRoute) {
        this.mapView = mapView;
        this.shouldZoom = shouldZoom;
        this.shouldDrawRoute = shouldDrawRoute;

        if (shouldDrawRoute) {
            this.routeLine = new Polyline();
            this.routeLine.setWidth(5f);
            this.routeLine.setColor(0xFFFF0000);
            this.mapView.getOverlayManager().add(routeLine);
        }
    }

    public LocationReceiver(MapView mapView, boolean shouldZoom) {
        this.mapView = mapView;
        this.routeLine = null;
        this.shouldZoom = shouldZoom;
        this.shouldDrawRoute = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.gpsapp.LOCATION_UPDATE")) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            double altitude = intent.getDoubleExtra("altitude", 0.0);

            updateLocation(latitude, longitude, altitude);
        }
    }

    private void updateLocation(double latitude, double longitude, double altitude) {
        if (mapView != null) {
            GeoPoint newPoint = new GeoPoint(latitude, longitude, altitude);

            if (shouldDrawRoute && routeLine != null) {
                ArrayList<GeoPoint> points = new ArrayList<>(routeLine.getPoints());
                points.add(newPoint);
                routeLine.setPoints(points);
            }

            if (shouldZoom) {
                mapView.getController().setZoom(15.0);
                mapView.getController().setCenter(newPoint);
            }

            mapView.invalidate();
        } else {
            Log.w("LocationReceiver", "MapView is not initialized.");
        }
    }
}