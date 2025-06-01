package com.joo.sisteminvestarisbarang;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText editUsername, editPassword;
    private Button btnLogin;
    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;
    private final String USERNAME = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        prefs = getSharedPreferences("user", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
                if (cursor.moveToFirst()) {
                    String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.putString("role", role);
                    editor.apply();
                    // Catat audit log login
                    ContentValues audit = new ContentValues();
                    audit.put(DatabaseHelper.AUDIT_USER, username);
                    audit.put(DatabaseHelper.AUDIT_ACTION, "login");
                    audit.put(DatabaseHelper.AUDIT_TARGET, "-");
                    audit.put(DatabaseHelper.AUDIT_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    db.insert(DatabaseHelper.TABLE_AUDIT, null, audit);
                    cursor.close();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    cursor.close();
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
