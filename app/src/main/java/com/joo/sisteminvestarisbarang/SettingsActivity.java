package com.joo.sisteminvestarisbarang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView text = findViewById(R.id.textSettings);
        text.setText("Pengaturan aplikasi\n\n- Ganti password\n- Tentang aplikasi\n- Logout");
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnCategory = findViewById(R.id.btnCategory);
        Button btnReport = findViewById(R.id.btnReport);
        Button btnUserManagement = findViewById(R.id.btnUserManagement);
        Button btnAuditLog = findViewById(R.id.btnAuditLog);
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String role = prefs.getString("role", "user");
        if (!"admin".equals(role)) {
            btnUserManagement.setVisibility(Button.GONE);
            btnAuditLog.setVisibility(Button.GONE);
        } else {
            btnUserManagement.setOnClickListener(v -> {
                startActivity(new Intent(this, UserManagementActivity.class));
            });
            btnAuditLog.setOnClickListener(v -> {
                startActivity(new Intent(this, AuditLogActivity.class));
            });
        }
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        btnCategory.setOnClickListener(v -> {
            startActivity(new Intent(this, CategoryActivity.class));
        });
        btnReport.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
        });
    }
}
