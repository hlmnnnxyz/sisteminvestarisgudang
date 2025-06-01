package com.joo.sisteminvestarisbarang;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        TextView textTotalItems = findViewById(R.id.textTotalItems);
        TextView textTotalStock = findViewById(R.id.textTotalStock);
        TextView textTotalCategories = findViewById(R.id.textTotalCategories);
        TextView textMostStock = findViewById(R.id.textMostStock);
        TextView textLeastStock = findViewById(R.id.textLeastStock);
        PieChart pieChart = findViewById(R.id.pieChartCategory);
        BarChart barChart = findViewById(R.id.barChartStock);

        // Total barang
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NAME, null);
        if (c1.moveToFirst()) textTotalItems.setText(String.valueOf(c1.getInt(0)));
        c1.close();
        // Total stok
        Cursor c2 = db.rawQuery("SELECT SUM(quantity) FROM " + DatabaseHelper.TABLE_NAME, null);
        if (c2.moveToFirst()) textTotalStock.setText(String.valueOf(c2.getInt(0)));
        c2.close();
        // Total kategori
        Cursor c3 = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CATEGORY, null);
        if (c3.moveToFirst()) textTotalCategories.setText(String.valueOf(c3.getInt(0)));
        c3.close();
        // Barang stok terbanyak
        Cursor c4 = db.rawQuery("SELECT name, quantity FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY quantity DESC LIMIT 1", null);
        if (c4.moveToFirst()) textMostStock.setText(c4.getString(0) + " (" + c4.getInt(1) + ")");
        else textMostStock.setText("-");
        c4.close();
        // Barang stok tersedikit
        Cursor c5 = db.rawQuery("SELECT name, quantity FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY quantity ASC LIMIT 1", null);
        if (c5.moveToFirst()) textLeastStock.setText(c5.getString(0) + " (" + c5.getInt(1) + ")");
        else textLeastStock.setText("-");
        c5.close();
        // PieChart: Jumlah barang per kategori
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Cursor cPie = db.rawQuery("SELECT c." + DatabaseHelper.CATEGORY_NAME + ", COUNT(i." + DatabaseHelper.COLUMN_ID + ") FROM " + DatabaseHelper.TABLE_CATEGORY + " c LEFT JOIN " + DatabaseHelper.TABLE_NAME + " i ON c." + DatabaseHelper.CATEGORY_ID + " = i." + DatabaseHelper.COLUMN_TYPE + " GROUP BY c." + DatabaseHelper.CATEGORY_NAME, null);
        while (cPie.moveToNext()) {
            String cat = cPie.getString(0);
            int count = cPie.getInt(1);
            pieEntries.add(new PieEntry(count, cat));
        }
        cPie.close();
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Barang per Kategori");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
        // BarChart: Stok per kategori
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> barLabels = new ArrayList<>();
        Cursor cBar = db.rawQuery("SELECT c." + DatabaseHelper.CATEGORY_NAME + ", SUM(i." + DatabaseHelper.COLUMN_QUANTITY + ") FROM " + DatabaseHelper.TABLE_CATEGORY + " c LEFT JOIN " + DatabaseHelper.TABLE_NAME + " i ON c." + DatabaseHelper.CATEGORY_ID + " = i." + DatabaseHelper.COLUMN_TYPE + " GROUP BY c." + DatabaseHelper.CATEGORY_NAME, null);
        int idx = 0;
        while (cBar.moveToNext()) {
            String cat = cBar.getString(0);
            int sum = cBar.getInt(1);
            barEntries.add(new BarEntry(idx, sum));
            barLabels.add(cat);
            idx++;
        }
        cBar.close();
        BarDataSet barDataSet = new BarDataSet(barEntries, "Stok per Kategori");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                return i >= 0 && i < barLabels.size() ? barLabels.get(i) : "";
            }
        });
        barChart.invalidate();
    }
}
