package com.joo.sisteminvestarisbarang;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class AuditLogActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private ArrayList<HashMap<String, String>> auditList = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_log);
        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listAudit);
        adapter = new SimpleAdapter(this, auditList, android.R.layout.simple_list_item_2,
                new String[]{"info", "time"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        loadAudit();
    }

    private void loadAudit() {
        auditList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT username, action, target, timestamp FROM audit_log ORDER BY id DESC LIMIT 100", null);
        while (c.moveToNext()) {
            String info = c.getString(0) + " - " + c.getString(1) + (c.getString(2) != null && !c.getString(2).equals("-") ? (" (" + c.getString(2) + ")") : "");
            String time = c.getString(3);
            HashMap<String, String> map = new HashMap<>();
            map.put("info", info);
            map.put("time", time);
            auditList.add(map);
        }
        c.close();
        adapter.notifyDataSetChanged();
    }
}
