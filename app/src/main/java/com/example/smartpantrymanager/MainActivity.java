package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.AdapterView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listPantry;
    private TextView txtEmptyPantry;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> pantryItems = new ArrayList<>();
    private final ArrayList<Integer> pantryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        Button btnSettings = findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        databaseHelper = new PantryDatabaseHelper(this);
        databaseHelper.seedRecipes();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                pantryItems
        );

        listPantry.setAdapter(adapter);

        listPantry.setOnItemLongClickListener((parent, view, position, id) -> {
            int ingredientId = pantryIds.get(position);
            String ingredientName = pantryItems.get(position);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete ingredient")
                    .setMessage("Delete this ingredient?\n\n" + ingredientName)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Delete", (dialog, which) -> {
                        databaseHelper.deletePantryItem(ingredientId);
                        loadPantryItems();
                    })
                    .show();

            return true;
        });

        listPantry.setOnItemClickListener((parent, view, position, id) -> {
            int ingredientId = pantryIds.get(position);

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            intent.putExtra("ingredient_id", ingredientId);
            startActivity(intent);
        });

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        pantryItems.clear();
        pantryIds.clear();

        try (Cursor cursor = databaseHelper.getAllPantryItems()) {
            while (cursor.moveToNext()) {
                int ingredientId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_ID
                        )
                );
                pantryIds.add(ingredientId);
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
                        name + "\nQuantity: " + quantity + " " + unit
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

