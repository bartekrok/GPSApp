package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView lvHistory;
    private MapView mapView;
    private ImageButton btnReturn;
    private RouteRepository routeRepository;
    private List<Route> routes = new ArrayList<>();
    private ArrayList<String> routeDescriptions = new ArrayList<>();
    private Polyline routeOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvHistory = findViewById(R.id.lv_history);
        mapView = findViewById(R.id.mpv_history);
        btnReturn = findViewById(R.id.imgbtn_return);

        initMap();
        routeRepository = new RouteRepository(this);

        loadRoutes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routeDescriptions);
        lvHistory.setAdapter(adapter);

        lvHistory.setOnItemClickListener((parent, view, position, id) -> updateMap(position));

        btnReturn.setOnClickListener(v -> finish());
    }

    private void initMap() {
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        routeOverlay = new Polyline();
        routeOverlay.setColor(getResources().getColor(android.R.color.holo_red_dark));
        mapView.getOverlays().add(routeOverlay);
    }

    private void loadRoutes() {
        routes = routeRepository.getAllRoutes();

        if (routes.isEmpty()) {
            Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Route route : routes) {
            routeDescriptions.add(route.getName() + " (" + route.getDate() + ")");
        }
    }

    private void updateMap(int position) {
        mapView.getOverlays().remove(routeOverlay);

        Route selectedRoute = routes.get(position);

        try {
            ArrayList<GeoPoint> geoPoints = new ArrayList<>();
            String routePoints = selectedRoute.getRoutePoints();
            JSONArray pointsArray = new JSONArray(routePoints);

            for (int i = 0; i < pointsArray.length(); i++) {
                JSONArray latLngArray = pointsArray.getJSONArray(i);
                GeoPoint geoPoint = new GeoPoint(latLngArray.getDouble(0), latLngArray.getDouble(1));
                geoPoints.add(geoPoint);
            }

            routeOverlay.setPoints(geoPoints);
            mapView.getOverlays().add(routeOverlay);

            if (!geoPoints.isEmpty()) {
                mapView.getController().setCenter(geoPoints.get(0));
                mapView.getController().setZoom(15.0);
            }

            mapView.invalidate();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to display route", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        routeRepository = null;
    }
}
