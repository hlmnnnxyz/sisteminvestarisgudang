package com.joo.sisteminvestarisbarang;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class LogMutasiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_mutasi);
        int itemId = getIntent().getIntExtra("item_id", -1);
        ListView listView = findViewById(R.id.listLog);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> logs = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_LOG, null, DatabaseHelper.LOG_ITEM_ID + "=?", new String[]{String.valueOf(itemId)}, null, null, DatabaseHelper.LOG_DATE + " DESC");
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.LOG_DATE));
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.LOG_AMOUNT));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.LOG_DESC));
            logs.add(date + " | " + (amount > 0 ? "+" : "") + amount + " | " + desc);
        }
        cursor.close();
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logs));
    }
}
