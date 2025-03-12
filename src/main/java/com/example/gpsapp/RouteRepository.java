package com.example.gpsapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import database.MyDBHelper;

public class RouteRepository {

    private final MyDBHelper dbHelper;

    public RouteRepository(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public long insertRoute(String name, String date, String routePoints) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.COLUMN_NAME, name);
        values.put(MyDBHelper.COLUMN_DATE, date);
        values.put(MyDBHelper.COLUMN_ROUTE_POINTS, routePoints);

        long id = db.insert(MyDBHelper.TABLE_ROUTES, null, values);
        db.close();
        return id;
    }

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(MyDBHelper.TABLE_ROUTES,
                null, null, null, null, null, MyDBHelper.COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_DATE));
                String routePoints = cursor.getString(cursor.getColumnIndexOrThrow(MyDBHelper.COLUMN_ROUTE_POINTS));

                routes.add(new Route(id, name, date, routePoints));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return routes;
    }

    public void deleteRoute(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MyDBHelper.TABLE_ROUTES, MyDBHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
