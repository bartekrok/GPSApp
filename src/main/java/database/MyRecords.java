package database;

public class MyRecords {

    public static class Route{
        public static final String TABLE_NAME = "route";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ALT = "altitude";
        public static final String COLUMN_LONG = "longitude";
        public static final String COLUMN_LAT = "latitude";

    }
    public static class Time{
        public static final String TABLE_NAME = "time";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_ROUTE_ID = "route_id";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";

    }



}