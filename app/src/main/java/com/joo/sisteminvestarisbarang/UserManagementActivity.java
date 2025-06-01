package com.joo.sisteminvestarisbarang;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class UserManagementActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private EditText editUsername, editPassword;
    private Spinner spinnerRole;
    private Button btnAddUser;
    private ArrayList<HashMap<String, String>> userList = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listUsers);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnAddUser = findViewById(R.id.btnAddUser);
        adapter = new SimpleAdapter(this, userList, android.R.layout.simple_list_item_2,
                new String[]{"username", "role"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        // Setup spinner role
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"admin", "petugas", "viewer"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
        loadUsers();
        btnAddUser.setOnClickListener(v -> addUser());
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            HashMap<String, String> user = userList.get(position);
            String username = user.get("username");
            if ("admin".equals(username)) {
                Toast.makeText(this, "User admin tidak bisa dihapus", Toast.LENGTH_SHORT).show();
                return true;
            }
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_USER, DatabaseHelper.USER_USERNAME + "=?", new String[]{username});
            loadUsers();
            Toast.makeText(this, "User dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
    private void loadUsers() {
        userList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT username, role FROM users", null);
        while (c.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("username", c.getString(0));
            map.put("role", c.getString(1));
            userList.add(map);
        }
        c.close();
        adapter.notifyDataSetChanged();
    }
    private void addUser() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();
        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        if (c.moveToFirst()) {
            Toast.makeText(this, "Username sudah terdaftar", Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }
        c.close();
        db.execSQL("INSERT INTO users (username, password, role) VALUES (?, ?, ?)", new Object[]{username, password, role});
        loadUsers();
        editUsername.setText("");
        editPassword.setText("");
        spinnerRole.setSelection(0);
        Toast.makeText(this, "User ditambahkan", Toast.LENGTH_SHORT).show();
    }
}
