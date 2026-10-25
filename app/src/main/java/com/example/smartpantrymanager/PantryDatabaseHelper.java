package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Locale;

public class PantryDatabaseHelper extends SQLiteOpenHelper implements AutoCloseable {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(createPantryTable);

        String createRecipesTable = "CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL)";
        db.execSQL(createRecipesTable);

        String createRecipeIngredientsTable =
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id))";
        db.execSQL(createRecipeIngredientsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }

    public long addPantryItem(String name, double quantity,
                              String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());
        values.put(COL_EXPIRY, expiryDate);

        return db.insert(TABLE_PANTRY, null, values);
    }

    public Cursor getAllPantryItems() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " COLLATE NOCASE ASC"
        );
    }
    public Cursor getPantryItemById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }
    public int updatePantryItem(int id, String name,
                                double quantity, String unit,
                                String expiryDate) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());
        values.put(COL_EXPIRY, expiryDate);

        return getWritableDatabase().update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
    public int deletePantryItem(int id) {
        return getWritableDatabase().delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
    public void seedRecipes() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes", null);

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();

        if (count > 0) {
            return;
        }

        db.beginTransaction();

            String[][] recipes = {
                    {"Vegetable Fried Rice",
                            "Cook the rice.\n" +
                                    "Stir-fry the vegetables.\n" +
                                    "Add the cooked rice and soy sauce, then mix well."},

                    {"Tomato Pasta",
                            "Boil the pasta until tender.\n" +
                                    "Cook the tomatoes with garlic.\n" +
                                    "Add the pasta and mix well."},

                    {"Pancakes",
                            "Combine the flour, milk and egg in a bowl.\n" +
                                    "Mix thoroughly until the batter is smooth.\n" +
                                    "Pour some batter into a lightly oiled pan.\n" +
                                    "Cook both sides until golden brown."},

                    {"Omelette",
                            "Crack the eggs into a bowl and beat them.\n" +
                                    "Heat a lightly oiled pan.\n" +
                                    "Pour in the eggs and cook until almost set.\n" +
                                    "Add cheese if available, fold and serve."},

                    {"Banana Smoothie",
                            "Peel the banana and break it into pieces.\n" +
                                    "Add the banana and milk to a blender.\n" +
                                    "Blend until smooth.\n" +
                                    "Pour into a glass and serve."},

                    {"Peanut Butter Toast",
                            "Place the bread in a toaster or toast it in a pan.\n" +
                                    "Spread peanut butter over the toasted bread.\n" +
                                    "Serve immediately."},

                    {"Garlic Rice",
                            "Cook the rice until tender.\n" +
                                    "Heat oil in a pan and add the garlic.\n" +
                                    "Stir-fry the garlic until fragrant.\n" +
                                    "Add the cooked rice and mix well."},

                    {"Cheese Sandwich",
                            "Place the cheese between two slices of bread.\n" +
                                    "Heat a pan or sandwich toaster.\n" +
                                    "Toast until the bread is golden and the cheese melts.\n" +
                                    "Cut and serve."},

                    {"Tomato Egg Scramble",
                            "Wash and chop the tomato.\n" +
                                    "Cook the tomato in a lightly oiled pan.\n" +
                                    "Beat the eggs in a bowl.\n" +
                                    "Add the eggs to the pan and stir until cooked."},

                    {"Simple Salad",
                            "Wash the cucumber, tomato and lettuce.\n" +
                                    "Chop the cucumber and tomato.\n" +
                                    "Tear or chop the lettuce into smaller pieces.\n" +
                                    "Combine everything in a bowl and mix."},

                    {"French Toast",
                            "Beat the egg and milk together in a bowl.\n" +
                                    "Dip each slice of bread into the mixture.\n" +
                                    "Heat a lightly oiled pan.\n" +
                                    "Fry both sides until golden brown."},

                    {"Milk Porridge",
                            "Add the oats and milk to a saucepan.\n" +
                                    "Heat over a low to medium heat.\n" +
                                    "Stir regularly until the oats soften and the mixture thickens.\n" +
                                    "Allow to cool slightly before serving."},

                    {"Flour Flatbread",
                            "Place the flour in a bowl.\n" +
                                    "Gradually add water and mix to form a dough.\n" +
                                    "Knead the dough until smooth.\n" +
                                    "Divide and flatten into thin rounds.\n" +
                                    "Cook in a hot pan until both sides are lightly browned."},

                    {"Egg Fried Rice",
                            "Cook the rice and allow it to cool slightly.\n" +
                                    "Heat oil in a pan and cook the vegetables.\n" +
                                    "Add the egg and stir until cooked.\n" +
                                    "Add the rice and stir-fry everything together."},

                    {"Banana Pancakes",
                            "Peel and mash the banana in a bowl.\n" +
                                    "Add the egg and flour.\n" +
                                    "Mix until the batter is combined.\n" +
                                    "Cook small portions in a lightly oiled pan until both sides are golden."},

                    {"Tomato Cheese Toast",
                            "Wash and slice the tomato.\n" +
                                    "Place the tomato and cheese on the bread.\n" +
                                    "Toast in a pan or sandwich toaster.\n" +
                                    "Cook until the cheese melts and serve."},

                    {"Peanut Banana Toast",
                            "Toast the bread until lightly golden.\n" +
                                    "Spread peanut butter over the toast.\n" +
                                    "Peel and slice the banana.\n" +
                                    "Arrange the banana slices on top and serve."},

                    {"Garlic Pasta",
                            "Boil the pasta until tender, then drain it.\n" +
                                    "Heat oil in a pan.\n" +
                                    "Add the chopped garlic and cook until fragrant.\n" +
                                    "Add the pasta and toss until evenly coated."},

                    {"Cucumber Sandwich",
                            "Wash the cucumber and slice it thinly.\n" +
                                    "Arrange the cucumber slices on a piece of bread.\n" +
                                    "Place the second slice of bread on top.\n" +
                                    "Cut the sandwich and serve."},

                    {"Oat Banana Bowl",
                            "Place the oats in a bowl.\n" +
                                    "Peel and slice the banana.\n" +
                                    "Add the banana and milk to the oats.\n" +
                                    "Mix well and serve."}
            };

            String[][][] ingredients = {
                    {{"rice", "1", "cup"}, {"vegetables", "1", "cup"}, {"soy sauce", "1", "tbsp"}},
                    {{"pasta", "1", "cup"}, {"tomato", "2", "items"}, {"garlic", "1", "clove"}},
                    {{"flour", "1", "cup"}, {"milk", "1", "cup"}, {"egg", "1", "item"}},
                    {{"egg", "2", "items"}},
                    {{"banana", "1", "item"}, {"milk", "1", "cup"}},
                    {{"bread", "2", "slices"}, {"peanut butter", "1", "tbsp"}},
                    {{"rice", "1", "cup"}, {"garlic", "1", "clove"}, {"oil", "1", "tbsp"}},
                    {{"bread", "2", "slices"}, {"cheese", "1", "slice"}},
                    {{"tomato", "1", "item"}, {"egg", "2", "items"}},
                    {{"cucumber", "1", "item"}, {"tomato", "1", "item"}},
                    {{"bread", "2", "slices"}, {"egg", "1", "item"}, {"milk", "0.25", "cup"}},
                    {{"oats", "0.5", "cup"}, {"milk", "1", "cup"}},
                    {{"flour", "1", "cup"}, {"water", "0.5", "cup"}},
                    {{"rice", "1", "cup"}, {"egg", "1", "item"}, {"vegetables", "1", "cup"}},
                    {{"banana", "1", "item"}, {"egg", "1", "item"}, {"flour", "0.5", "cup"}},
                    {{"bread", "2", "slices"}, {"tomato", "1", "item"}, {"cheese", "1", "slice"}},
                    {{"bread", "2", "slices"}, {"peanut butter", "1", "tbsp"}, {"banana", "1", "item"}},
                    {{"pasta", "1", "cup"}, {"garlic", "1", "clove"}, {"oil", "1", "tbsp"}},
                    {{"bread", "2", "slices"}, {"cucumber", "0.5", "item"}},
                    {{"oats", "0.5", "cup"}, {"banana", "1", "item"}, {"milk", "0.5", "cup"}}
            };

            for (int i = 0; i < recipes.length; i++) {
                ContentValues recipeValues = new ContentValues();
                recipeValues.put("name", recipes[i][0]);
                recipeValues.put("instructions", recipes[i][1]);

                long recipeId = db.insert("recipes", null, recipeValues);

                for (String[] ingredient : ingredients[i]) {
                    ContentValues ingredientValues = new ContentValues();
                    ingredientValues.put("recipe_id", recipeId);
                    ingredientValues.put("ingredient_name", ingredient[0]);
                    ingredientValues.put("required_quantity",
                            Double.parseDouble(ingredient[1]));
                    ingredientValues.put("unit", ingredient[2]);

                    db.insert("recipe_ingredients", null, ingredientValues);
                }
            }

            db.setTransactionSuccessful();
        }

    public void updateRecipeInstructions() {
        SQLiteDatabase db = getWritableDatabase();

        String[][] recipes = {
                {"Vegetable Fried Rice",
                        "Cook the rice.\nStir-fry the vegetables.\nAdd the cooked rice and soy sauce, then mix well."},
                {"Tomato Pasta",
                        "Boil the pasta until tender.\nCook the tomatoes with garlic.\nAdd the pasta and mix well."},
                {"Pancakes",
                        "Combine the flour, milk and egg in a bowl.\nMix thoroughly until smooth.\nPour the batter into a lightly oiled pan.\nCook both sides until golden brown."},
                {"Omelette",
                        "Crack the eggs into a bowl and beat them.\nHeat a lightly oiled pan.\nPour in the eggs and cook until almost set.\nAdd cheese if available, fold and serve."},
                {"Banana Smoothie",
                        "Peel and break the banana into pieces.\nAdd the banana and milk to a blender.\nBlend until smooth.\nPour into a glass and serve."},
                {"Peanut Butter Toast",
                        "Toast the bread.\nSpread peanut butter over the toast.\nServe immediately."},
                {"Garlic Rice",
                        "Cook the rice until tender.\nHeat oil in a pan and add the garlic.\nStir-fry until fragrant.\nAdd the cooked rice and mix well."},
                {"Cheese Sandwich",
                        "Place the cheese between two slices of bread.\nHeat a pan or sandwich toaster.\nToast until golden and the cheese melts.\nCut and serve."},
                {"Tomato Egg Scramble",
                        "Wash and chop the tomato.\nCook the tomato in a lightly oiled pan.\nBeat the eggs in a bowl.\nAdd the eggs and stir until cooked."},
                {"Simple Salad",
                        "Wash the cucumber, tomato and lettuce.\nChop the cucumber and tomato.\nTear or chop the lettuce.\nCombine everything in a bowl and mix."},
                {"French Toast",
                        "Beat the egg and milk together.\nDip each slice of bread into the mixture.\nHeat a lightly oiled pan.\nFry both sides until golden brown."},
                {"Milk Porridge",
                        "Add the oats and milk to a saucepan.\nHeat over low to medium heat.\nStir until the oats soften and the mixture thickens.\nAllow to cool slightly before serving."},
                {"Flour Flatbread",
                        "Place the flour in a bowl.\nGradually add water and mix into a dough.\nKnead the dough until smooth.\nDivide and flatten into rounds.\nCook in a hot pan until both sides are lightly browned."},
                {"Egg Fried Rice",
                        "Cook the rice and allow it to cool slightly.\nHeat oil in a pan and cook the vegetables.\nAdd the egg and stir until cooked.\nAdd the rice and stir-fry everything together."},
                {"Banana Pancakes",
                        "Peel and mash the banana.\nAdd the egg and flour.\nMix until combined.\nCook small portions in a lightly oiled pan until both sides are golden."},
                {"Tomato Cheese Toast",
                        "Wash and slice the tomato.\nPlace the tomato and cheese on the bread.\nToast in a pan or sandwich toaster.\nCook until the cheese melts and serve."},
                {"Peanut Banana Toast",
                        "Toast the bread.\nSpread peanut butter over the toast.\nPeel and slice the banana.\nArrange the banana slices on top and serve."},
                {"Garlic Pasta",
                        "Boil the pasta until tender, then drain it.\nHeat oil in a pan.\nAdd the garlic and cook until fragrant.\nAdd the pasta and toss until coated."},
                {"Cucumber Sandwich",
                        "Wash and slice the cucumber thinly.\nArrange the cucumber slices on a piece of bread.\nPlace the second slice of bread on top.\nCut and serve."},
                {"Oat Banana Bowl",
                        "Place the oats in a bowl.\nPeel and slice the banana.\nAdd the banana and milk to the oats.\nMix well and serve."}
        };

        db.beginTransaction();

        try {
            for (String[] recipe : recipes) {
                ContentValues values = new ContentValues();
                values.put("instructions", recipe[1]);

                db.update(
                        "recipes",
                        values,
                        "name = ?",
                        new String[]{recipe[0]}
                );
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public ArrayList<String> getMatchingRecipes() {
            ArrayList<String> matchingRecipes = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();

        String recipeQuery = "SELECT id, name FROM recipes";

        try (Cursor recipesCursor = db.rawQuery(recipeQuery, null)) {
            while (recipesCursor.moveToNext()) {
                int recipeId = recipesCursor.getInt(0);
                String recipeName = recipesCursor.getString(1);
                boolean allIngredientsAvailable = true;

                String ingredientQuery =
                        "SELECT ingredient_name, required_quantity, unit " +
                                "FROM recipe_ingredients WHERE recipe_id = ?";

                try (Cursor ingredientsCursor = db.rawQuery(
                        ingredientQuery,
                        new String[]{String.valueOf(recipeId)})) {

                    while (ingredientsCursor.moveToNext()) {
                        String requiredName = ingredientsCursor.getString(0);
                        double requiredQuantity = ingredientsCursor.getDouble(1);
                        String requiredUnit = ingredientsCursor.getString(2);

                        double availableQuantity = 0;

                        try (Cursor pantryCursor = db.rawQuery(
                                "SELECT name, quantity, unit FROM pantry_items",
                                null)) {

                            while (pantryCursor.moveToNext()) {
                                String pantryName = pantryCursor.getString(0);
                                double pantryQuantity = pantryCursor.getDouble(1);
                                String pantryUnit = pantryCursor.getString(2);

                                if (normaliseIngredient(pantryName).equals(
                                        normaliseIngredient(requiredName))
                                        && normaliseUnit(pantryUnit).equals(
                                        normaliseUnit(requiredUnit))) {
                                    availableQuantity += pantryQuantity;
                                }
                            }
                        }

                        if (availableQuantity < requiredQuantity) {
                            allIngredientsAvailable = false;
                            break;
                        }
                    }
                }

                if (allIngredientsAvailable) {
                    matchingRecipes.add(recipeName);
                }
            }
        }

        return matchingRecipes;
    }
    public Cursor getRecipeById(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, name, instructions FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }
    public Cursor getRecipeIngredients(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }
    private String normaliseIngredient(String name) {
        String value = name.trim().toLowerCase(Locale.ROOT);

        if (value.endsWith("ies") && value.length() > 3) {
            return value.substring(0, value.length() - 3) + "y";
        }

        if (value.endsWith("s") && !value.endsWith("ss")
                && value.length() > 1) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
    private String normaliseUnit(String unit) {
        String value = unit.trim().toLowerCase(Locale.ROOT);

        if (value.endsWith("s") && value.length() > 1) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
    public int getRecipeIdByName(String recipeName) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id FROM recipes WHERE name = ?",
                new String[]{recipeName})) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return -1;
    }
}
