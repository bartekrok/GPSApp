package com.example.gpsapp;

public class Route {
    private long id;
    private String name;
    private String date;
    private String routePoints;

    public Route(long id, String name, String date, String routePoints) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.routePoints = routePoints;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getRoutePoints() {
        return routePoints;
    }
}