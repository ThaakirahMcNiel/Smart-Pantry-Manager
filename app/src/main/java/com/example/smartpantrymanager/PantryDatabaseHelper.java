
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

        try {
            String[][] recipes = {
                    {"Vegetable Fried Rice", "Cook rice. Stir-fry vegetables, add rice and soy sauce."},
                    {"Tomato Pasta", "Boil pasta. Cook tomatoes with garlic and mix with pasta."},
                    {"Pancakes", "Mix flour, milk and egg. Cook portions in a lightly oiled pan."},
                    {"Omelette", "Beat eggs and cook in a pan. Add cheese if available."},
                    {"Banana Smoothie", "Blend banana and milk until smooth."},
                    {"Peanut Butter Toast", "Toast bread and spread peanut butter."},
                    {"Garlic Rice", "Cook rice and stir-fry with garlic and oil."},
                    {"Cheese Sandwich", "Place cheese between bread slices and toast."},
                    {"Tomato Egg Scramble", "Cook chopped tomatoes, add beaten eggs and stir."},
                    {"Simple Salad", "Chop cucumber and tomato, then mix together."},
                    {"French Toast", "Dip bread in beaten egg and milk, then fry."},
                    {"Milk Porridge", "Cook oats in milk until thick."},
                    {"Flour Flatbread", "Mix flour and water, shape dough and cook in a pan."},
                    {"Egg Fried Rice", "Stir-fry cooked rice with egg and vegetables."},
                    {"Banana Pancakes", "Mash banana, mix with egg and flour, then fry."},
                    {"Tomato Cheese Toast", "Add tomato and cheese to bread and toast."},
                    {"Peanut Banana Toast", "Spread peanut butter on bread and add banana slices."},
                    {"Garlic Pasta", "Boil pasta and toss with cooked garlic and oil."},
                    {"Cucumber Sandwich", "Place sliced cucumber between bread slices."},
                    {"Oat Banana Bowl", "Mix oats, banana and milk in a bowl."}
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
