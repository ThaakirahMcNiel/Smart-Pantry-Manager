package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView txtRecipeTitle = findViewById(R.id.txtRecipeTitle);
        TextView txtIngredients = findViewById(R.id.txtIngredients);
        TextView txtInstructions = findViewById(R.id.txtInstructions);
        Button btnBack = findViewById(R.id.btnBack);

        databaseHelper = new PantryDatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId != -1) {
            loadRecipe(
                    recipeId,
                    txtRecipeTitle,
                    txtIngredients,
                    txtInstructions
            );
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRecipe(
            int recipeId,
            TextView txtRecipeTitle,
            TextView txtIngredients,
            TextView txtInstructions) {

        try (Cursor recipeCursor = databaseHelper.getRecipeById(recipeId)) {
            if (recipeCursor.moveToFirst()) {
                txtRecipeTitle.setText(recipeCursor.getString(1));

                String instructions = recipeCursor.getString(2);
                String[] steps = instructions.split("\\n");

                StringBuilder numberedSteps = new StringBuilder();
                int stepNumber = 1;

                for (String rawStep : steps) {
                    String step = rawStep.trim();

                    if (!step.isEmpty()) {
                        numberedSteps.append(stepNumber)
                                .append(". ")
                                .append(step)
                                .append("\n\n");

                        stepNumber++;
                    }
                }

                txtInstructions.setText(numberedSteps.toString().trim());
            }
        }

        StringBuilder ingredientsText = new StringBuilder();

        try (Cursor ingredientCursor =
                     databaseHelper.getRecipeIngredients(recipeId)) {

            while (ingredientCursor.moveToNext()) {
                ingredientsText.append("• ")
                        .append(ingredientCursor.getDouble(1))
                        .append(" ")
                        .append(ingredientCursor.getString(2))
                        .append(" ")
                        .append(ingredientCursor.getString(0))
                        .append("\n");
            }
        }

        txtIngredients.setText(ingredientsText.toString().trim());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
