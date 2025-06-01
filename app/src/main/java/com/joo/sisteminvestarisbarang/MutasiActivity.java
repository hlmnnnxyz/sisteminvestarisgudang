package com.joo.sisteminvestarisbarang;

import android.content.ContentValues;
import android.content.SharedPreferences;
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

public class MutasiActivity extends AppCompatActivity {
    private EditText editJumlah, editKeterangan;
    private Button btnTambah, btnKurang;
    private int itemId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutasi);
        dbHelper = new DatabaseHelper(this);
        editJumlah = findViewById(R.id.editJumlah);
        editKeterangan = findViewById(R.id.editKeterangan);
        btnTambah = findViewById(R.id.btnTambah);
        btnKurang = findViewById(R.id.btnKurang);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String role = prefs.getString("role", "viewer");
        if ("viewer".equals(role)) {
            btnTambah.setEnabled(false);
            btnKurang.setEnabled(false);
            btnTambah.setAlpha(0.5f);
            btnKurang.setAlpha(0.5f);
        }

        itemId = getIntent().getIntExtra("item_id", -1);

        btnTambah.setOnClickListener(v -> mutasiBarang(true));
        btnKurang.setOnClickListener(v -> mutasiBarang(false));
    }

    private void mutasiBarang(boolean tambah) {
        String jumlahStr = editJumlah.getText().toString().trim();
        String ket = editKeterangan.getText().toString().trim();
        if (TextUtils.isEmpty(jumlahStr)) {
            Toast.makeText(this, "Jumlah harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        int jumlah = Integer.parseInt(jumlahStr);
        if (!tambah) jumlah = -jumlah;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_NAME + " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + DatabaseHelper.COLUMN_QUANTITY + " + ? WHERE " + DatabaseHelper.COLUMN_ID + "=?", new Object[]{jumlah, itemId});
        // Simpan log mutasi
        ContentValues log = new ContentValues();
        log.put(DatabaseHelper.LOG_ITEM_ID, itemId);
        log.put(DatabaseHelper.LOG_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        log.put(DatabaseHelper.LOG_AMOUNT, jumlah);
        log.put(DatabaseHelper.LOG_DESC, ket);
        db.insert(DatabaseHelper.TABLE_LOG, null, log);
        Toast.makeText(this, "Mutasi berhasil", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK); // agar MainActivity tahu harus refresh
        finish();
    }
}
