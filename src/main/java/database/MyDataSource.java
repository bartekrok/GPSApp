package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
public class MyDataSource {
    private SQLiteDatabase database;
    private MyDBHelper dbHelper;

    public MyDataSource(Context context) {
        dbHelper = new MyDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertDataRoute(float longitude, float altitude, float latitude) {
        ContentValues values = new ContentValues();
        values.put(MyRecords.Route.COLUMN_LONG, longitude);
        values.put(MyRecords.Route.COLUMN_LAT, latitude);
        values.put(MyRecords.Route.COLUMN_ALT, altitude);
        return database.insert(MyRecords.Route.TABLE_NAME, null, values);
    }
    public long insertDataTime(int id, String start_time, String end_time) {
        ContentValues values = new ContentValues();
        values.put(MyRecords.Time.COLUMN_ROUTE_ID, id);
        values.put(MyRecords.Time.COLUMN_START_TIME, start_time);
        values.put(MyRecords.Time.COLUMN_END_TIME, end_time);
        return database.insert(MyRecords.Time.TABLE_NAME, null, values);
    }

    public Cursor getAllDataRoute() {
        return database.query(MyRecords.Route.TABLE_NAME, null, null, null, null, null, null);
    }
    public Cursor getAllDataTime() {
        return database.query(MyRecords.Time.TABLE_NAME, null, null, null, null, null, null);
    }

}