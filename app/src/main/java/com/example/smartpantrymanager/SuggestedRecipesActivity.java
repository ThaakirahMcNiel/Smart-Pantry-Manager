package com.example.smartpantrymanager;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listSuggestedRecipes;
    private TextView txtNoRecipes;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> recipeNames = new ArrayList<>();

    private final String[] dessertRecipes = {
            "Cinnamon Pumpkin",
            "Cape Malay Pumpkin Fritters",
            "Cape Malay Koesisters",
            "Boeber",
            "Cape Malay Milk Tart",
            "Sweet Yellow Rice",
            "Cape Malay Sago Pudding"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        listSuggestedRecipes = findViewById(R.id.listSuggestedRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);
        Button btnBackToPantry = findViewById(R.id.btnBackToPantry);

        databaseHelper = new PantryDatabaseHelper(this);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeNames
        );

        listSuggestedRecipes.setAdapter(adapter);

        listSuggestedRecipes.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = recipeNames.get(position);

            // Ignore category headings.
            if (selectedItem.startsWith("— ")) {
                return;
            }

            int recipeId = databaseHelper.getRecipeIdByName(selectedItem);

            if (recipeId != -1) {
                Intent intent = new Intent(
                        SuggestedRecipesActivity.this,
                        RecipeDetailActivity.class
                );

                intent.putExtra("recipe_id", recipeId);
                startActivity(intent);
            }
        });

        btnBackToPantry.setOnClickListener(v -> finish());

        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null) {
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes() {
        List<String> matchingRecipes = databaseHelper.getMatchingRecipes();

        Collections.sort(matchingRecipes);

        List<String> mains = new ArrayList<>();
        List<String> desserts = new ArrayList<>();

        for (String recipe : matchingRecipes) {
            if (isDessert(recipe)) {
                desserts.add(recipe);
            } else {
                mains.add(recipe);
            }
        }

        recipeNames.clear();

        if (!mains.isEmpty()) {
            recipeNames.add("— MAINS —");
            recipeNames.addAll(mains);
        }

        if (!desserts.isEmpty()) {
            recipeNames.add("— DESSERTS —");
            recipeNames.addAll(desserts);
        }

        adapter.notifyDataSetChanged();

        boolean noRecipes = matchingRecipes.isEmpty();

        txtNoRecipes.setVisibility(noRecipes ? View.VISIBLE : View.GONE);
        listSuggestedRecipes.setVisibility(noRecipes ? View.GONE : View.VISIBLE);

        if (noRecipes) {
            txtNoRecipes.setText(
                    "No recipes match your pantry yet. Add ingredients or update their quantities to see suggestions."
            );
        }
    }

    private boolean isDessert(String recipeName) {
        for (String dessert : dessertRecipes) {
            if (dessert.equalsIgnoreCase(recipeName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
