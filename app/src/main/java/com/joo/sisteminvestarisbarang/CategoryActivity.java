package com.joo.sisteminvestarisbarang;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    private EditText editCategory;
    private Button btnAdd;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ArrayList<String> categories = new ArrayList<>();
    private DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        dbHelper = new DatabaseHelper(this);
        editCategory = findViewById(R.id.editCategory);
        btnAdd = findViewById(R.id.btnAddCategory);
        recyclerView = findViewById(R.id.recyclerCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categories);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String role = prefs.getString("role", "viewer");
        if (!"admin".equals(role)) {
            btnAdd.setEnabled(false);
            btnAdd.setAlpha(0.5f);
            editCategory.setEnabled(false);
            adapter.setAllowDelete(false);
        } else {
            adapter.setAllowDelete(true);
        }

        adapter.setOnCategoryDeleteListener(name -> {
            if (isCategoryUsed(name)) {
                Toast.makeText(this, "Kategori sedang dipakai barang, tidak bisa dihapus!", Toast.LENGTH_SHORT).show();
            } else {
                deleteCategory(name);
            }
        });
        loadCategories();
        btnAdd.setOnClickListener(v -> addCategory());
    }
    private void loadCategories() {
        categories.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORY, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_NAME)));
        }
        cursor.close();
        if (adapter != null) adapter.notifyDataSetChanged();
    }
    private void addCategory() {
        String name = editCategory.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nama kategori harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CATEGORY_NAME, name);
        long result = db.insert(DatabaseHelper.TABLE_CATEGORY, null, values);
        if (result == -1) {
            Toast.makeText(this, "Kategori sudah ada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Kategori ditambahkan", Toast.LENGTH_SHORT).show();
            editCategory.setText("");
            loadCategories();
        }
    }
    private void deleteCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CATEGORY, DatabaseHelper.CATEGORY_NAME + "=?", new String[]{name});
        loadCategories();
        Toast.makeText(this, "Kategori dihapus", Toast.LENGTH_SHORT).show();
    }
    private boolean isCategoryUsed(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Cari id kategori berdasarkan nama
        int categoryId = -1;
        Cursor c = db.query(DatabaseHelper.TABLE_CATEGORY, null, DatabaseHelper.CATEGORY_NAME + "=?", new String[]{name}, null, null, null);
        if (c.moveToFirst()) {
            categoryId = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.CATEGORY_ID));
        }
        c.close();
        if (categoryId == -1) return false;
        // Cek apakah ada barang yang memakai kategori ini
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, DatabaseHelper.COLUMN_TYPE + "=?", new String[]{String.valueOf(categoryId)}, null, null, null);
        boolean used = cursor.moveToFirst();
        cursor.close();
        return used;
    }
}
