package com.joo.sisteminvestarisbarang;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DetailItemActivity extends AppCompatActivity {
    private TextView textName, textQuantity, textType, textLocation;
    private Button btnMutasi;
    private int itemId;
    private DatabaseHelper dbHelper;
    private static final int REQ_MUTASI = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        dbHelper = new DatabaseHelper(this);
        textName = findViewById(R.id.textName);
        textQuantity = findViewById(R.id.textQuantity);
        textType = findViewById(R.id.textType);
        textLocation = findViewById(R.id.textLocation);
        btnMutasi = findViewById(R.id.btnMutasi);

        itemId = getIntent().getIntExtra("item_id", -1);
        loadItem(itemId);

        btnMutasi.setOnClickListener(v -> {
            Intent intent = new Intent(DetailItemActivity.this, MutasiActivity.class);
            intent.putExtra("item_id", itemId);
            startActivityForResult(intent, REQ_MUTASI);
        });

        Button btnLogMutasi = findViewById(R.id.btnLogMutasi);
        btnLogMutasi.setOnClickListener(v -> {
            Intent intent = new Intent(DetailItemActivity.this, LogMutasiActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
        });
    }

    private void loadItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            textName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            textType.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)));
            textLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION)));
            textQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY))));
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_MUTASI && resultCode == RESULT_OK) {
            setResult(RESULT_OK); // agar MainActivity refresh
            loadItem(itemId); // update tampilan detail
        }
    }
}
