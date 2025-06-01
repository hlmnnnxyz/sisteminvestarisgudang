package com.joo.sisteminvestarisbarang;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editOld, editNew, editConfirm;
    private Button btnSave;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        editOld = findViewById(R.id.editOldPassword);
        editNew = findViewById(R.id.editNewPassword);
        editConfirm = findViewById(R.id.editConfirmPassword);
        btnSave = findViewById(R.id.btnSavePassword);
        prefs = getSharedPreferences("user", MODE_PRIVATE);
        btnSave.setOnClickListener(v -> {
            String old = editOld.getText().toString();
            String n = editNew.getText().toString();
            String c = editConfirm.getText().toString();
            String saved = prefs.getString("password", "admin");
            if (TextUtils.isEmpty(old) || TextUtils.isEmpty(n) || TextUtils.isEmpty(c)) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            } else if (!old.equals(saved)) {
                Toast.makeText(this, "Password lama salah", Toast.LENGTH_SHORT).show();
            } else if (!n.equals(c)) {
                Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("password", n).apply();
                Toast.makeText(this, "Password berhasil diganti", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
