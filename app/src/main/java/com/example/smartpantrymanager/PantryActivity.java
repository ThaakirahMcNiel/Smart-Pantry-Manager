package com.example.smartpantrymanager;

import android.os.Bundle;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class PantryActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private PantryAdapter adapter;
    private ArrayList<PantryItem> pantryItems;

    private ListView listPantry;
    private TextView txtEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("open_drawer", false)) {
            androidx.drawerlayout.widget.DrawerLayout drawerLayout =
                    findViewById(R.id.drawerLayout);

            drawerLayout.post(() ->
                    drawerLayout.openDrawer(
                            androidx.core.view.GravityCompat.START
                    )
            );
        }

        androidx.drawerlayout.widget.DrawerLayout drawerLayout =
                findViewById(R.id.drawerLayout);

        android.widget.Button btnOpenDrawer =
                findViewById(R.id.btnOpenDrawer);

        btnOpenDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(
                    androidx.core.view.GravityCompat.START
            );
        });

        com.google.android.material.navigation.NavigationView navigationView =
                findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_pantry) {
                drawerLayout.closeDrawer(
                        androidx.core.view.GravityCompat.START
                );
            } else if (itemId == R.id.nav_recipes) {
                drawerLayout.closeDrawer(
                        androidx.core.view.GravityCompat.START
                );
                startActivity(new Intent(
                        PantryActivity.this,
                        SuggestedRecipesActivity.class
                ));
            } else if (itemId == R.id.nav_settings) {
                drawerLayout.closeDrawer(
                        androidx.core.view.GravityCompat.START
                );
                startActivity(new Intent(
                        PantryActivity.this,
                        SettingsActivity.class
                ));
            }

            return true;
        });

        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        databaseHelper = new PantryDatabaseHelper(this);
        pantryItems = new ArrayList<>();

        adapter = new PantryAdapter(this, pantryItems);
        listPantry.setAdapter(adapter);

        listPantry.setOnItemClickListener((parent, view, position, id) -> {
            PantryItem item = pantryItems.get(position);

            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );

            intent.putExtra("ingredient_id", item.getId());
            startActivity(intent);
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null) {
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

        boolean isEmpty = pantryItems.isEmpty();

        txtEmptyPantry.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );

        listPantry.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE
        );
    }
}

