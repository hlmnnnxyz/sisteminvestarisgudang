package com.joo.sisteminvestarisbarang;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import android.text.Editable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private FloatingActionButton fabAdd;
    private static final int REQ_ADD_EDIT = 1;
    private static final int REQ_DETAIL = 2;
    private EditText editSearch;
    private Spinner spinnerFilterType, spinnerFilterLocation;
    private List<Item> allItems = new ArrayList<>();
    private String role;
    private final ActivityResultLauncher<Intent> importLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) importFromCSV(uri);
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        role = prefs.getString("role", "viewer");
        fabAdd = findViewById(R.id.fabAdd);
        if ("viewer".equals(role)) {
            fabAdd.setVisibility(View.GONE);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        editSearch = findViewById(R.id.editSearch);
        spinnerFilterType = findViewById(R.id.spinnerFilterType);
        spinnerFilterLocation = findViewById(R.id.spinnerFilterLocation);

        loadItems();

        adapter.setOnItemActionListener(new ItemAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Item item) {
                if ("admin".equals(role)) {
                    Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                    intent.putExtra("item_id", item.getId());
                    startActivityForResult(intent, REQ_ADD_EDIT);
                } else {
                    Toast.makeText(MainActivity.this, "Hanya admin yang bisa edit barang", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDelete(Item item) {
                if ("admin".equals(role)) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(item.getId())});
                    // Audit log hapus barang
                    SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                    String username = prefs.getString("username", "");
                    ContentValues audit = new ContentValues();
                    audit.put(DatabaseHelper.AUDIT_USER, username);
                    audit.put(DatabaseHelper.AUDIT_ACTION, "hapus_barang");
                    audit.put(DatabaseHelper.AUDIT_TARGET, item.getName());
                    audit.put(DatabaseHelper.AUDIT_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    db.insert(DatabaseHelper.TABLE_AUDIT, null, audit);
                    loadItems();
                    Toast.makeText(MainActivity.this, "Barang dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Hanya admin yang bisa hapus barang", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDetail(Item item) {
                Intent intent = new Intent(MainActivity.this, DetailItemActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivityForResult(intent, REQ_DETAIL);
            }
        });

        fabAdd.setOnClickListener(v -> {
            if ("admin".equals(role) || "petugas".equals(role)) {
                Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
                startActivityForResult(intent, REQ_ADD_EDIT);
            } else {
                Toast.makeText(MainActivity.this, "Hanya admin/petugas yang bisa menambah barang", Toast.LENGTH_SHORT).show();
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        spinnerFilterType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterAndSearch();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        spinnerFilterLocation.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterAndSearch();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQ_ADD_EDIT || requestCode == REQ_DETAIL) && resultCode == RESULT_OK) {
            loadItems();
        }
    }

    private void loadItems() {
        allItems.clear();
        itemList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT i.*, c." + DatabaseHelper.CATEGORY_NAME + " as typeName FROM " + DatabaseHelper.TABLE_NAME + " i LEFT JOIN " + DatabaseHelper.TABLE_CATEGORY + " c ON i." + DatabaseHelper.COLUMN_TYPE + " = c." + DatabaseHelper.CATEGORY_ID;
        Cursor cursor = db.rawQuery(sql, null);
        List<String> types = new ArrayList<>();
        List<String> locations = new ArrayList<>();
        types.add("Semua Jenis");
        locations.add("Semua Lokasi");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                int typeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE));
                String typeName = cursor.getString(cursor.getColumnIndexOrThrow("typeName"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));
                allItems.add(new Item(id, name, typeId, typeName, location, quantity));
                if (typeName != null && !types.contains(typeName)) types.add(typeName);
                if (!locations.contains(location)) locations.add(location);
            } while (cursor.moveToNext());
        }
        cursor.close();
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterType.setAdapter(typeAdapter);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterLocation.setAdapter(locationAdapter);
        filterAndSearch();
    }

    private void filterAndSearch() {
        String search = editSearch.getText().toString().toLowerCase();
        String type = spinnerFilterType.getSelectedItem() != null ? spinnerFilterType.getSelectedItem().toString() : "Semua Jenis";
        String location = spinnerFilterLocation.getSelectedItem() != null ? spinnerFilterLocation.getSelectedItem().toString() : "Semua Lokasi";
        itemList.clear();
        for (Item item : allItems) {
            boolean match = (type.equals("Semua Jenis") || (item.getTypeName() != null && item.getTypeName().equals(type))) &&
                            (location.equals("Semua Lokasi") || item.getLocation().equals(location)) &&
                            (item.getName().toLowerCase().contains(search) || (item.getTypeName() != null && item.getTypeName().toLowerCase().contains(search)) || item.getLocation().toLowerCase().contains(search));
            if (match) itemList.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_export) {
            exportToCSV();
            return true;
        } else if (id == R.id.menu_import) {
            openImportFilePicker();
            return true;
        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openImportFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        importLauncher.launch(Intent.createChooser(intent, "Pilih file CSV"));
    }

    private void exportToCSV() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (dir == null) dir = getFilesDir();
        File file = new File(dir, "inventaris_export.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("ID,Nama,Jenis,Lokasi,Jumlah\n");
            for (Item item : itemList) {
                writer.append(item.getId() + "," + item.getName() + "," + item.getType() + "," + item.getLocation() + "," + item.getQuantity() + "\n");
            }
            writer.flush();
            Toast.makeText(this, "Data diekspor ke: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Gagal ekspor: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void importFromCSV(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean first = true;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int imported = 0;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                String[] cols = line.split(",");
                if (cols.length < 5) continue;
                ContentValues values = new ContentValues();
                values.put("id", cols[0]);
                values.put("name", cols[1]);
                values.put("type", cols[2]);
                values.put("location", cols[3]);
                values.put("quantity", cols[4]);
                long res = db.insertWithOnConflict(DatabaseHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (res != -1) imported++;
            }
            Toast.makeText(this, "Import selesai: " + imported + " data", Toast.LENGTH_LONG).show();
            loadItems();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal import: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}