package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    private MapView mv_main;
    private MyLocationNewOverlay myLocationOverlay;
    private Button btn_admin, btn_start, btn_history;

    private LocationReceiver locationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_admin = findViewById(R.id.btn_admin);
        btn_admin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminPage.class);
            startActivity(intent);
        });

        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
        });

        btn_history = findViewById(R.id.btn_history);
        btn_history.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
        } else {
            initMap();
            startLocationService();
        }

        locationReceiver = new LocationReceiver(mv_main, true);
        //locationReceiver.setTextView(null);
    }

    private void initMap() {
        mv_main = findViewById(R.id.mv_main);
        mv_main.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mv_main.setBuiltInZoomControls(true);
        mv_main.setMultiTouchControls(true);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mv_main);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mv_main.getOverlays().add(myLocationOverlay);
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, MyGPSService.class);
        startService(serviceIntent);

        IntentFilter filter = new IntentFilter("com.example.gpsapp.LOCATION_UPDATE");
        registerReceiver(locationReceiver, filter, RECEIVER_EXPORTED);
    }

    public void updateMapView(double latitude, double longitude, double altitude) {
        if (mv_main != null) {
            GeoPoint startPoint = new GeoPoint(latitude, longitude, altitude);
            mv_main.getController().setCenter(startPoint);
            mv_main.getController().setZoom(15.0);
            Log.v("MainActivity", "Latitude: " + latitude);
            Log.v("MainActivity", "Longitude: " + longitude);
            Log.v("MainActivity", "Altitude: " + altitude);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationReceiver != null) {
            registerReceiver(locationReceiver, new IntentFilter("com.example.gpsapp.LOCATION_UPDATE"), RECEIVER_EXPORTED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationReceiver != null) {
            unregisterReceiver(locationReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
