package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages SQLite database operations for the Smart Pantry Manager app.
 * Handles database creation, version upgrades, pantry item CRUD operations,
 * and recipe matching algorithms.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry Table Constants
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Recipes Table Constants
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe Ingredients Table Constants
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "required_quantity";
    public static final String COL_RI_UNIT = "unit";

    /**
     * Constructor to initialize the SQLite database helper.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * Sets up all tables and pre-seeds default recipes.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the pantry table
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT)";

        // SQL statement to create the recipes table
        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)";

        // SQL statement to create the recipe ingredients table with foreign key constraint
        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QUANTITY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))";

        // Execute table creation queries
        db.execSQL(createPantryTable);
        db.execSQL(createRecipeTable);
        db.execSQL(createRecipeIngredientsTable);

        // Pre-populate database with default recipes
        seedRecipes(db);
    }

    /**
     * Called when the database needs to be upgraded.
     * Drops existing tables and recreates them.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- PANTRY OPERATIONS ---

    /**
     * Adds a new item to the pantry inventory.
     */
    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, name.trim().toLowerCase());
        values.put(COL_PANTRY_QUANTITY, quantity);
        values.put(COL_PANTRY_UNIT, unit.trim().toLowerCase());
        values.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.insert(TABLE_PANTRY, null, values);
    }

    /**
     * Updates an existing pantry item by its ID.
     */
    public boolean updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, name.trim().toLowerCase());
        values.put(COL_PANTRY_QUANTITY, quantity);
        values.put(COL_PANTRY_UNIT, unit.trim().toLowerCase());
        values.put(COL_PANTRY_EXPIRY, expiryDate);
        return db.update(TABLE_PANTRY, values, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Retrieves all pantry items ordered alphabetically by name.
     */
    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COL_PANTRY_NAME + " ASC", null);
    }

    /**
     * Retrieves a specific pantry item by its unique ID.
     */
    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " WHERE " + COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME));
            double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY));
            String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT));
            String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY));
            cursor.close();
            return new PantryItem(id, name, qty, unit, expiry);
        }
        return null;
    }

    /**
     * Deletes a pantry item by its unique ID.
     */
    public boolean deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // --- RECIPE SEEDING & MATCHING OPERATIONS ---

    /**
     * Pre-seeds initial sample recipes and their respective ingredients into the database.
     */
    private void seedRecipes(SQLiteDatabase db) {
        addRecipeWithIngredients(db, "Scrambled Eggs", "1. Whisk eggs and milk.\n2. Melt butter in pan.\n3. Cook on low heat.",
                new String[][]{{"egg", "2", "pcs"}, {"milk", "50", "ml"}, {"butter", "10", "g"}});

        addRecipeWithIngredients(db, "Pancakes", "1. Mix flour, eggs, and milk.\n2. Pour batter on skillet.\n3. Flip when bubbly.",
                new String[][]{{"flour", "200", "g"}, {"egg", "2", "pcs"}, {"milk", "300", "ml"}, {"butter", "20", "g"}});

        addRecipeWithIngredients(db, "Grilled Cheese", "1. Butter bread slices.\n2. Place cheese between slices.\n3. Grill until golden.",
                new String[][]{{"bread", "2", "slices"}, {"cheese", "2", "slices"}, {"butter", "10", "g"}});

        addRecipeWithIngredients(db, "Tomato Soup", "1. Sauté onion and garlic.\n2. Add tomato and broth.\n3. Simmer and blend.",
                new String[][]{{"tomato", "4", "pcs"}, {"onion", "1", "pc"}, {"garlic", "2", "cloves"}});

        addRecipeWithIngredients(db, "Omelette", "1. Beat eggs with salt.\n2. Pour into skillet and add cheese.\n3. Fold and serve.",
                new String[][]{{"egg", "3", "pcs"}, {"cheese", "1", "slice"}, {"butter", "10", "g"}});

        addRecipeWithIngredients(db, "Pasta Marinara", "1. Boil pasta.\n2. Cook tomatoes with garlic.\n3. Combine and serve.",
                new String[][]{{"pasta", "200", "g"}, {"tomato", "3", "pcs"}, {"garlic", "2", "cloves"}});

        addRecipeWithIngredients(db, "Garlic Bread", "1. Mix butter and minced garlic.\n2. Spread on bread slice.\n3. Bake for 10 mins.",
                new String[][]{{"bread", "4", "slices"}, {"butter", "30", "g"}, {"garlic", "3", "cloves"}});

        addRecipeWithIngredients(db, "Fried Rice", "1. Stir fry garlic and onion.\n2. Add rice and egg.\n3. Season and mix well.",
                new String[][]{{"rice", "200", "g"}, {"egg", "1", "pc"}, {"garlic", "1", "clove"}, {"onion", "1", "pc"}});

        addRecipeWithIngredients(db, "French Toast", "1. Dip bread in egg and milk mix.\n2. Fry in butter until brown.",
                new String[][]{{"bread", "2", "slices"}, {"egg", "1", "pc"}, {"milk", "50", "ml"}, {"butter", "10", "g"}});

        addRecipeWithIngredients(db, "Mashed Potatoes", "1. Boil potatoes.\n2. Mash with butter and milk.",
                new String[][]{{"potato", "3", "pcs"}, {"butter", "20", "g"}, {"milk", "50", "ml"}});

        addRecipeWithIngredients(db, "Boiled Eggs", "1. Place eggs in boiling water for 8 mins.\n2. Cool in cold water and peel.",
                new String[][]{{"egg", "2", "pcs"}});

        addRecipeWithIngredients(db, "Potato Chips", "1. Slice potatoes thin.\n2. Fry in oil until crispy.",
                new String[][]{{"potato", "2", "pcs"}, {"oil", "100", "ml"}});

        addRecipeWithIngredients(db, "Simple Salad", "1. Chop tomatoes and cucumber.\n2. Mix with oil.",
                new String[][]{{"tomato", "2", "pcs"}, {"cucumber", "1", "pc"}, {"oil", "15", "ml"}});

        addRecipeWithIngredients(db, "Cheesy Pasta", "1. Boil pasta.\n2. Melt butter and cheese into hot pasta.",
                new String[][]{{"pasta", "150", "g"}, {"cheese", "2", "slices"}, {"butter", "15", "g"}});

        addRecipeWithIngredients(db, "Garlic Rice", "1. Sauté garlic in butter.\n2. Add cooked rice and stir.",
                new String[][]{{"rice", "200", "g"}, {"garlic", "2", "cloves"}, {"butter", "15", "g"}});
    }

    /**
     * Helper method to insert a recipe and its corresponding ingredients into the database.
     */
    private void addRecipeWithIngredients(SQLiteDatabase db, String name, String instructions, String[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (String[] ing : ingredients) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(COL_RI_RECIPE_ID, recipeId);
            ingValues.put(COL_RI_NAME, ing[0].trim().toLowerCase());
            ingValues.put(COL_RI_QUANTITY, Double.parseDouble(ing[1]));
            ingValues.put(COL_RI_UNIT, ing[2].trim().toLowerCase());
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }

    /**
     * Robust matching algorithm:
     * Uses LOWER() and RTRIM(..., 's') to trim plural 's' or 'es' endings
     * so "eggs" matches "egg" and "tomatoes" matches "tomato".
     * Returns a list of recipes whose required ingredients are all present in the user's pantry.
     */
    public List<Recipe> getMatchingRecipes() {
        List<Recipe> matchingRecipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor recipeCursor = db.rawQuery("SELECT " + COL_RECIPE_ID + " FROM " + TABLE_RECIPES, null);

        if (recipeCursor != null && recipeCursor.moveToFirst()) {
            do {
                long recipeId = recipeCursor.getLong(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_ID));

                String checkQuery = "SELECT COUNT(*) FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " +
                        COL_RI_RECIPE_ID + " = ? AND " +
                        "LOWER(RTRIM(RTRIM(" + COL_RI_NAME + ", 's'), 'e')) NOT IN (" +
                        "  SELECT LOWER(RTRIM(RTRIM(" + COL_PANTRY_NAME + ", 's'), 'e')) FROM " + TABLE_PANTRY +
                        ")";

                Cursor checkCursor = db.rawQuery(checkQuery, new String[]{String.valueOf(recipeId)});

                if (checkCursor != null && checkCursor.moveToFirst()) {
                    int missingCount = checkCursor.getInt(0);
                    checkCursor.close();

                    if (missingCount == 0) {
                        Recipe match = getRecipeById(recipeId);
                        if (match != null) {
                            matchingRecipes.add(match);
                        }
                    }
                }
            } while (recipeCursor.moveToNext());
            recipeCursor.close();
        }

        return matchingRecipes;
    }

    /**
     * Retrieves full recipe details (Name, Ingredients with formatted units/qty, and Instructions) by ID.
     */
    public Recipe getRecipeById(long recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)});

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));
            String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS));

            String ingQuery = "SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COL_RI_RECIPE_ID + " = ?";
            Cursor ingCursor = db.rawQuery(ingQuery, new String[]{String.valueOf(recipeId)});

            StringBuilder ingredientsBuilder = new StringBuilder();
            if (ingCursor != null && ingCursor.moveToFirst())  {
                do {
                    String ingName = ingCursor.getString(ingCursor.getColumnIndexOrThrow(COL_RI_NAME));
                    double qty = ingCursor.getDouble(ingCursor.getColumnIndexOrThrow(COL_RI_QUANTITY));
                    String unit = ingCursor.getString(ingCursor.getColumnIndexOrThrow(COL_RI_UNIT));

                    if (ingredientsBuilder.length() > 0) {
                        ingredientsBuilder.append("\n");
                    }
                    ingredientsBuilder.append("• ").append(ingName).append(" (").append(qty).append(" ").append(unit).append(")");
                } while (ingCursor.moveToNext());
                ingCursor.close();
            }
            cursor.close();

            Recipe recipe = new Recipe(recipeId, name, ingredientsBuilder.toString());
            recipe.setInstructions(instructions);
            return recipe;
        }
        return null;
    }
}
