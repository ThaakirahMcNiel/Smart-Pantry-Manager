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

    public void clearRecipeCatalogue() {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            db.delete("recipe_ingredients", null, null);
            db.delete("recipes", null, null);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
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

        try{
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

        } finally {
            db.endTransaction();
        }
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
                                    availableQuantity += convertToBaseQuantity(
                                            pantryQuantity, pantryUnit
                                    );
                                }
                            }
                        }

                        double requiredBaseQuantity =
                                convertToBaseQuantity(requiredQuantity, requiredUnit);

                        if (availableQuantity < requiredBaseQuantity) {
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
    private double convertToBaseQuantity(double quantity, String unit) {
        String value = unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;

            case "g":
            case "gm":
            case "gram":
            case "grams":
                return quantity;

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity * 1000;

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return quantity;

            default:
                return quantity;
        }
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

        switch (value) {
            case "kilogram":
            case "kilograms":
            case "kgs":
            case "kg":
            case "gram":
            case "grams":
            case "gm":
            case "g":
                return "weight";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "volume";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tbsp";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "tsp";

            case "cup":
            case "cups":
                return "cup";

            case "item":
            case "items":
                return "item";

            case "clove":
            case "cloves":
                return "clove";

            case "slice":
            case "slices":
                return "slice";

            default:
                return value;
        }
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

    public void replaceWithCapeMalayRecipes() {
        SQLiteDatabase db = getWritableDatabase();

        String[][] recipes = {
                {"Cape Malay Bobotie",
                        "Soak the bread in milk and squeeze out excess milk.\nMix the bread with minced beef, curry powder, turmeric, chutney and seasoning.\nCook the mixture in a pan until the meat is browned.\nPlace in an ovenproof dish. Beat the eggs with the remaining milk and pour over the meat.\nBake at 180°C until the topping is set and golden."},

                {"Chicken Breyani",
                        "Rinse and soak the rice briefly, then drain.\nCook the chicken with onion, garlic, ginger, curry powder and turmeric.\nAdd the tomatoes and cook until the chicken is nearly tender.\nLayer the chicken and rice in a pot with the potatoes.\nAdd water, cover and cook on low heat until the rice and chicken are cooked."},

                {"Cape Malay Chicken Curry",
                        "Heat oil and cook the onion until soft.\nAdd garlic, ginger, curry powder and turmeric.\nAdd chicken and stir to coat with the spices.\nAdd tomatoes and potatoes with a little water.\nCover and simmer until the chicken and potatoes are fully cooked."},

                {"Denningvleis",
                        "Brown the beef in a pot with a little oil.\nAdd onion, garlic, ginger, cloves and cinnamon.\nStir in tamarind paste and brown sugar with water.\nCover and simmer gently until the beef is tender and the sauce has thickened.\nTaste and adjust the sweet-and-sour balance."},

                {"Cape Malay Tomato Bredie",
                        "Heat oil and brown the lamb pieces.\nAdd chopped onion and cook until softened.\nAdd tomatoes, cinnamon and black pepper.\nCover and simmer gently until the lamb is tender.\nStir occasionally and add a little water if needed."},

                {"Cape Malay Frikkadels",
                        "Combine minced beef, grated onion, egg, breadcrumbs, coriander and cumin.\nShape the mixture into small meatballs.\nHeat oil in a pan and brown the meatballs on all sides.\nAdd a little water, cover and cook until the meatballs are cooked through."},

                {"Cape Malay Sosaties",
                        "Mix the chicken pieces with curry powder, turmeric, garlic, apricot jam and vinegar.\nMarinate in the refrigerator for at least 30 minutes.\nThread the chicken and onion pieces onto skewers.\nGrill or bake, turning regularly, until the chicken is fully cooked."},

                {"Cape Malay Pickled Fish",
                        "Season the fish with salt and pepper, then coat lightly in flour.\nFry the fish in oil until cooked and golden. Set aside.\nCook sliced onion with curry powder, turmeric, vinegar, sugar and water.\nSimmer until the onion softens and the sauce is tangy.\nPour the sauce over the fish and refrigerate before serving."},

                {"Spinach Daltjies",
                        "Mix chopped spinach, flour, baking powder, curry powder and salt.\nAdd water gradually to make a thick batter.\nHeat oil in a deep pan.\nDrop spoonfuls of batter into the hot oil and fry until golden and cooked through.\nDrain on paper towel before serving."},

                {"Potato Samoosas",
                        "Boil the potatoes until tender and mash them roughly.\nCook onion with curry powder and add the potatoes and peas.\nAllow the filling to cool.\nFill samoosa pastry and fold into triangles, sealing the edges with flour paste.\nFry until crisp and golden."},

                {"Chicken Samoosas",
                        "Cook minced chicken with onion, garlic, curry powder and coriander until fully cooked.\nAllow the filling to cool completely.\nPlace a spoonful of filling into samoosa pastry and fold into triangles.\nSeal the edges with flour paste.\nFry until crisp and golden."},

                {"Sugar Bean Bredie",
                        "Heat oil and soften the onion with garlic and curry powder.\nAdd chopped tomatoes and cook until soft.\nAdd cooked sugar beans and a little water.\nSimmer until the sauce thickens and the flavours combine.\nSeason and serve warm."},

                {"Yellow Rice with Raisins",
                        "Rinse the rice until the water runs mostly clear.\nAdd rice, water, turmeric, cinnamon, sugar, raisins and a little salt to a pot.\nBring to a boil, then reduce the heat and cover.\nCook until the rice is tender and the liquid is absorbed.\nFluff gently with a fork."},

                {"Cinnamon Pumpkin",
                        "Peel and cube the pumpkin.\nPlace in a saucepan with a little water, cinnamon and brown sugar.\nCover and simmer until the pumpkin is tender.\nRemove the lid and allow excess liquid to evaporate.\nServe warm as a side dish."},

                {"Cape Malay Pumpkin Fritters",
                        "Boil the pumpkin until soft and drain thoroughly.\nMash the pumpkin and mix with flour, egg, sugar, cinnamon and baking powder.\nHeat a little oil in a pan.\nSpoon in the mixture and fry on both sides until golden and cooked through.\nDust with cinnamon sugar if desired."},

                {"Cape Malay Koesisters",
                        "Mix flour, sugar, yeast, cinnamon, ginger, cardamom and aniseed.\nAdd warm water and knead into a soft dough. Leave to rise until doubled.\nShape into small portions and allow to rise again.\nFry until cooked and golden.\nDip the warm koesisters into cooled sugar syrup and coat with desiccated coconut."},

                {"Boeber",
                        "Melt butter in a saucepan and add vermicelli, cardamom, cinnamon and sultanas.\nStir gently until the vermicelli begins to soften.\nAdd milk and sugar.\nSimmer gently, stirring often, until the vermicelli is tender and the drink thickens.\nServe warm."},

                {"Cape Malay Milk Tart",
                        "Prepare and bake a pastry shell until lightly golden.\nHeat milk with cinnamon until warm.\nWhisk eggs, sugar, flour and cornflour in a bowl.\nGradually whisk in the warm milk, then return the mixture to the saucepan.\nStir over low heat until thick, pour into the shell and sprinkle with cinnamon. Chill until set."},

                {"Sweet Yellow Rice",
                        "Rinse the rice well.\nAdd rice, water, turmeric, cinnamon, sugar, raisins and a little salt to a saucepan.\nBring to a boil, then cover and cook over low heat until tender.\nStir in a little butter if desired.\nFluff gently and serve warm."},

                {"Cape Malay Sago Pudding",
                        "Soak the sago in water for about 30 minutes, then drain.\nHeat milk with cinnamon and add the sago.\nSimmer gently, stirring often, until the pearls become mostly translucent.\nStir in sugar and beaten egg gradually, keeping the heat low.\nCook until creamy and serve warm or chilled with cinnamon."}
        };

        String[][][] ingredients = {
                {{"minced beef","500","g"},{"bread","2","slices"},{"milk","1","cup"},{"egg","2","items"},{"onion","1","item"},{"curry powder","1","tbsp"},{"turmeric","1","tsp"},{"chutney","2","tbsp"}},
                {{"chicken","500","g"},{"rice","2","cups"},{"onion","1","item"},{"garlic","2","cloves"},{"ginger","1","tbsp"},{"curry powder","2","tbsp"},{"turmeric","1","tsp"},{"tomato","2","items"},{"potato","2","items"}},
                {{"chicken","500","g"},{"onion","1","item"},{"garlic","2","cloves"},{"ginger","1","tbsp"},{"curry powder","2","tbsp"},{"turmeric","1","tsp"},{"tomato","2","items"},{"potato","2","items"},{"oil","1","tbsp"}},
                {{"beef","500","g"},{"onion","1","item"},{"garlic","2","cloves"},{"ginger","1","tbsp"},{"tamarind paste","2","tbsp"},{"brown sugar","2","tbsp"},{"cloves","4","items"},{"cinnamon","1","stick"}},
                {{"lamb","500","g"},{"tomato","4","items"},{"onion","1","item"},{"cinnamon","1","stick"},{"oil","1","tbsp"}},
                {{"minced beef","500","g"},{"onion","1","item"},{"egg","1","item"},{"breadcrumbs","0.5","cup"},{"coriander","1","tsp"},{"cumin","1","tsp"},{"oil","1","tbsp"}},
                {{"chicken","500","g"},{"onion","1","item"},{"curry powder","1","tbsp"},{"turmeric","1","tsp"},{"garlic","2","cloves"},{"apricot jam","2","tbsp"},{"vinegar","2","tbsp"}},
                {{"fish","500","g"},{"onion","2","items"},{"flour","0.5","cup"},{"curry powder","2","tbsp"},{"turmeric","1","tsp"},{"vinegar","0.5","cup"},{"sugar","0.25","cup"},{"oil","2","tbsp"}},
                {{"spinach","2","cups"},{"flour","1","cup"},{"baking powder","1","tsp"},{"curry powder","1","tsp"},{"oil","2","tbsp"}},
                {{"potato","3","items"},{"peas","0.5","cup"},{"onion","1","item"},{"curry powder","1","tsp"},{"samoosa pastry","10","sheets"},{"flour","2","tbsp"},{"oil","2","tbsp"}},
                {{"chicken","300","g"},{"onion","1","item"},{"garlic","2","cloves"},{"curry powder","1","tbsp"},{"coriander","1","tsp"},{"samoosa pastry","10","sheets"},{"flour","2","tbsp"},{"oil","2","tbsp"}},
                {{"sugar beans","2","cups"},{"onion","1","item"},{"tomato","2","items"},{"garlic","2","cloves"},{"curry powder","1","tbsp"},{"oil","1","tbsp"}},
                {{"rice","1","cup"},{"raisins","0.25","cup"},{"turmeric","0.5","tsp"},{"cinnamon","1","stick"},{"sugar","1","tbsp"}},
                {{"pumpkin","500","g"},{"cinnamon","1","tsp"},{"brown sugar","2","tbsp"}},
                {{"pumpkin","2","cups"},{"flour","1","cup"},{"egg","1","item"},{"sugar","2","tbsp"},{"cinnamon","1","tsp"},{"baking powder","1","tsp"},{"oil","2","tbsp"}},
                {{"flour","3","cups"},{"sugar","0.5","cup"},{"yeast","1","tsp"},{"cinnamon","1","tsp"},{"ginger","1","tsp"},{"cardamom","0.5","tsp"},{"aniseed","1","tsp"},{"desiccated coconut","0.5","cup"},{"oil","2","cups"}},
                {{"milk","4","cups"},{"vermicelli","0.5","cup"},{"butter","2","tbsp"},{"cardamom","0.5","tsp"},{"cinnamon","1","stick"},{"sultanas","0.25","cup"},{"sugar","0.25","cup"}},
                {{"milk","4","cups"},{"egg","3","items"},{"sugar","0.75","cup"},{"flour","0.25","cup"},{"cornflour","0.25","cup"},{"pastry","1","shell"},{"cinnamon","1","tsp"}},
                {{"rice","1","cup"},{"raisins","0.25","cup"},{"turmeric","0.5","tsp"},{"cinnamon","1","stick"},{"sugar","2","tbsp"},{"butter","1","tbsp"}},
                {{"sago","0.5","cup"},{"milk","4","cups"},{"sugar","0.25","cup"},{"egg","1","item"},{"cinnamon","1","stick"}}
        };

        db.beginTransaction();
        try {
            db.delete("recipe_ingredients", null, null);
            db.delete("recipes", null, null);

            for (int i = 0; i < recipes.length; i++) {
                ContentValues recipeValues = new ContentValues();
                recipeValues.put("name", recipes[i][0]);
                recipeValues.put("instructions", recipes[i][1]);

                long recipeId = db.insert("recipes", null, recipeValues);

                if (recipeId == -1) {
                    throw new android.database.SQLException(
                            "Could not insert recipe: " + recipes[i][0]);
                }

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
}
