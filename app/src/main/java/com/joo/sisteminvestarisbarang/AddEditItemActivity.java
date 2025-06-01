package com.joo.sisteminvestarisbarang;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditItemActivity extends AppCompatActivity {
    private EditText editName, editQuantity, editLocation;
    private Button btnSave;
    private Spinner spinnerType;
    private DatabaseHelper dbHelper;
    private int itemId = -1;
    private List<Integer> categoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        dbHelper = new DatabaseHelper(this);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editLocation = findViewById(R.id.editLocation);
        btnSave = findViewById(R.id.btnSave);
        spinnerType = findViewById(R.id.spinnerType);
        loadCategories();

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String role = prefs.getString("role", "viewer");
        if ("viewer".equals(role)) {
            btnSave.setEnabled(false);
            btnSave.setAlpha(0.5f);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("item_id")) {
            itemId = intent.getIntExtra("item_id", -1);
            loadItem(itemId);
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void loadCategories() {
        categoryIds.clear();
        List<String> categoryNames = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORY, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            categoryIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ID)));
            categoryNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_NAME)));
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void loadItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            editName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            int typeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE));
            for (int i = 0; i < categoryIds.size(); i++) {
                if (categoryIds.get(i) == typeId) {
                    spinnerType.setSelection(i);
                    break;
                }
            }
            editLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION)));
            editQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY))));
        }
        cursor.close();
    }

    private void saveItem() {
        String name = editName.getText().toString().trim();
        int typeIdx = spinnerType.getSelectedItemPosition();
        String location = editLocation.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(name) || typeIdx < 0 || TextUtils.isEmpty(location) || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        int typeId = categoryIds.get(typeIdx);
        int quantity = Integer.parseInt(quantityStr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_TYPE, typeId);
        values.put(DatabaseHelper.COLUMN_LOCATION, location);
        values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String action;
        String target = name;
        if (itemId == -1) {
            db.insert(DatabaseHelper.TABLE_NAME, null, values);
            action = "tambah_barang";
        } else {
            db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(itemId)});
            action = "edit_barang";
        }
        // Audit log
        ContentValues audit = new ContentValues();
        audit.put(DatabaseHelper.AUDIT_USER, username);
        audit.put(DatabaseHelper.AUDIT_ACTION, action);
        audit.put(DatabaseHelper.AUDIT_TARGET, target);
        audit.put(DatabaseHelper.AUDIT_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        db.insert(DatabaseHelper.TABLE_AUDIT, null, audit);
        setResult(RESULT_OK);
        finish();
    }
}
