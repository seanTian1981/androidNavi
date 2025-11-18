package com.soundcampus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "soundcampus.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";

    public static final String TABLE_ROUTES = "routes";
    public static final String COLUMN_START_ID = "start_id";
    public static final String COLUMN_END_ID = "end_id";
    public static final String COLUMN_DISTANCE = "distance";

    private static final String CREATE_TABLE_LOCATIONS =
            "CREATE TABLE " + TABLE_LOCATIONS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_LATITUDE + " REAL NOT NULL, " +
                    COLUMN_LONGITUDE + " REAL NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT" +
                    ");";

    private static final String CREATE_TABLE_ROUTES =
            "CREATE TABLE " + TABLE_ROUTES + " (" +
                    COLUMN_START_ID + " TEXT NOT NULL, " +
                    COLUMN_END_ID + " TEXT NOT NULL, " +
                    COLUMN_DISTANCE + " REAL NOT NULL, " +
                    "PRIMARY KEY (" + COLUMN_START_ID + ", " + COLUMN_END_ID + ")" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_ROUTES);
        insertDefaultLocations(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        onCreate(db);
    }

    private void insertDefaultLocations(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc1', '第一教学楼', 39.9042, 116.4074, '主要教学区域', 'teaching');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc2', '第二教学楼', 39.9052, 116.4084, '理工科教学楼', 'teaching');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc3', '图书馆', 39.9062, 116.4094, '大学图书馆', 'library');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc4', '食堂', 39.9072, 116.4104, '学生食堂', 'cafeteria');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc5', '宿舍楼', 39.9082, 116.4114, '学生宿舍', 'dormitory');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc6', '实验楼', 39.9092, 116.4124, '实验室大楼', 'laboratory');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc7', '体育馆', 39.9102, 116.4134, '体育运动中心', 'gym');");
        db.execSQL("INSERT INTO " + TABLE_LOCATIONS + " VALUES ('loc8', '行政楼', 39.9112, 116.4144, '行政办公区', 'admin');");
    }
}
