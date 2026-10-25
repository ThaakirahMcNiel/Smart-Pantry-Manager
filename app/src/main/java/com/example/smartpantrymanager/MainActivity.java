package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listPantry;
    private TextView txtEmptyPantry;
    private PantryAdapter adapter;
    private final ArrayList<PantryItem> pantryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        android.widget.Button btnOpenDrawer =
                findViewById(R.id.btnHomeOpenDrawer);

        btnOpenDrawer.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            PantryActivity.class
                    );
            intent.putExtra("open_drawer", true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null){
            loadPantryItems();
        }
    }

    private void loadPantryItems() {
        pantryItems.clear();

        try (Cursor cursor = databaseHelper.getAllPantryItems()) {
            while (cursor.moveToNext()) {
                int ingredientId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_UNIT
                        )
                );

                pantryItems.add(
                        new PantryItem(ingredientId, name, quantity, unit)
                );
            }
        }

        adapter.notifyDataSetChanged();

        android.util.Log.d("PANTRY_DEBUG",
                "Pantry loaded. Item count: " + pantryItems.size());

        boolean isEmpty = pantryItems.isEmpty();

        txtEmptyPantry.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );

        listPantry.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

