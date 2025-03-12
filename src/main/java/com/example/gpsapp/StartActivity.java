package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {

    private MapView mv_main;
    private MyLocationNewOverlay myLocationOverlay;
    private Polyline routeOverlay;
    private LocationReceiver locationReceiver;
    private RouteRepository routeRepository;

    private ImageButton imgbtn_return;
    private Handler handler;
    private ArrayList<GeoPoint> currentRoutePoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        routeRepository = new RouteRepository(this);

        initMap();

        locationReceiver = new LocationReceiver(mv_main, true, true);
        registerReceiver(locationReceiver, new IntentFilter("com.example.gpsapp.LOCATION_UPDATE"), RECEIVER_EXPORTED);

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::recordLocation, 1000);

        imgbtn_return = findViewById(R.id.imgbtn_return);
        imgbtn_return.setOnClickListener(v -> {
            saveRouteToDatabase();
            finish();
        });
    }
    // Color, TileSourceFactory, MapView (mv_main), Polyline (routeOverlay), GpsMyLocationProvider
    private void initMap() {
        mv_main = findViewById(R.id.mv_main);
        mv_main.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mv_main.setBuiltInZoomControls(true);
        mv_main.setMultiTouchControls(true);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mv_main);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mv_main.getOverlays().add(myLocationOverlay);

        routeOverlay = new Polyline();
        routeOverlay.setColor(Color.RED);
        mv_main.getOverlays().add(routeOverlay);
    }

    //GeoPoint
    private void recordLocation() {
        Location currentLocation = myLocationOverlay.getLastFix();

        if (currentLocation != null) {
            GeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
            currentRoutePoints.add(geoPoint);

            routeOverlay.setPoints(currentRoutePoints);
            mv_main.invalidate();
        }

        handler.postDelayed(this::recordLocation, 1000);
    }

    private void saveRouteToDatabase() {
        if (currentRoutePoints.isEmpty()) {
            Toast.makeText(this, "No route points to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder routePointsJson = new StringBuilder("[");
        for (int i = 0; i < currentRoutePoints.size(); i++) {
            GeoPoint point = currentRoutePoints.get(i);
            routePointsJson.append("[").append(point.getLatitude()).append(",").append(point.getLongitude()).append("]");
            if (i < currentRoutePoints.size() - 1) {
                routePointsJson.append(",");
            }
        }
        routePointsJson.append("]");

        String routeName = "Route " + System.currentTimeMillis();
        String routeDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());

        long routeId = routeRepository.insertRoute(routeName, routeDate, routePointsJson.toString());

        if (routeId != -1) {
            Toast.makeText(this, "Route saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save the route!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReceiver);
        handler.removeCallbacksAndMessages(null);
    }
}
