package com.joo.sisteminvestarisbarang;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 2; // dinaikkan agar onUpgrade dipanggil
    public static final String TABLE_NAME = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LOCATION = "location";
    public static final String TABLE_LOG = "log_mutasi";
    public static final String LOG_ID = "id";
    public static final String LOG_ITEM_ID = "item_id";
    public static final String LOG_DATE = "date";
    public static final String LOG_AMOUNT = "amount";
    public static final String LOG_DESC = "description";
    public static final String TABLE_CATEGORY = "categories";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_NAME = "name";
    public static final String TABLE_USER = "users";
    public static final String USER_ID = "id";
    public static final String USER_USERNAME = "username";
    public static final String USER_PASSWORD = "password";
    public static final String USER_ROLE = "role";
    public static final String TABLE_AUDIT = "audit_log";
    public static final String AUDIT_ID = "id";
    public static final String AUDIT_USER = "username";
    public static final String AUDIT_ACTION = "action";
    public static final String AUDIT_TARGET = "target";
    public static final String AUDIT_TIME = "timestamp";

    private static final String TABLE_CATEGORY_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " (" +
                    CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CATEGORY_NAME + " TEXT UNIQUE);";

    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_TYPE + " INTEGER, " + // foreign key ke categories
                    COLUMN_LOCATION + " TEXT, " +
                    COLUMN_QUANTITY + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_TYPE + ") REFERENCES " + TABLE_CATEGORY + "(" + CATEGORY_ID + ")" +
                    ");";

    private static final String TABLE_LOG_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOG + " (" +
                    LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LOG_ITEM_ID + " INTEGER, " +
                    LOG_DATE + " TEXT, " +
                    LOG_AMOUNT + " INTEGER, " +
                    LOG_DESC + " TEXT);";

    private static final String TABLE_USER_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
                    USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_USERNAME + " TEXT UNIQUE, " +
                    USER_PASSWORD + " TEXT, " +
                    USER_ROLE + " TEXT);";

    private static final String TABLE_AUDIT_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_AUDIT + " (" +
                    AUDIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AUDIT_USER + " TEXT, " +
                    AUDIT_ACTION + " TEXT, " +
                    AUDIT_TARGET + " TEXT, " +
                    AUDIT_TIME + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CATEGORY_CREATE);
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_LOG_CREATE);
        db.execSQL(TABLE_USER_CREATE); // Tambah tabel user
        db.execSQL(TABLE_AUDIT_CREATE); // Tambah tabel audit
        // Insert default admin jika belum ada
        db.execSQL("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin', 'admin', 'admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(TABLE_CATEGORY_CREATE);
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIT);
        onCreate(db);
    }
}
