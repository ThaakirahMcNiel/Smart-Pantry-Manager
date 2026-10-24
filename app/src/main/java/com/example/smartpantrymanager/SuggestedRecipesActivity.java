package com.example.smartpantrymanager;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private PantryDatabaseHelper databaseHelper;
    private ListView listSuggestedRecipes;
    private TextView txtNoRecipes;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> recipeNames = new ArrayList<>();

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
        listSuggestedRecipes.setOnItemClickListener((parent, view, position, id) ->{
            String selectedRecipe = recipeNames.get(position);
            int recipeId = databaseHelper.getRecipeIdByName(selectedRecipe);

            if(recipeId != -1){
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
    protected void onResume(){
        super.onResume();

        if(databaseHelper != null && adapter != null){
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes(){
        recipeNames.clear();
        recipeNames.addAll(databaseHelper.getMatchingRecipes());
        adapter.notifyDataSetChanged();

        boolean noRecipes = recipeNames.isEmpty();
        txtNoRecipes.setVisibility(noRecipes ? View.VISIBLE : View.GONE);
        listSuggestedRecipes.setVisibility(
                noRecipes ? View.GONE : View.VISIBLE
        );
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(databaseHelper != null){
            databaseHelper.close();
        }
    }
}