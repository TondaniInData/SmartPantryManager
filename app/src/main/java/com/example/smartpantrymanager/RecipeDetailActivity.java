package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvInstructions = findViewById(R.id.tvDetailInstructions);

        long recipeId = getIntent().getLongExtra("RECIPE_ID", -1);

        if (recipeId != -1) {
            DatabaseHelper db = new DatabaseHelper(this);
            Recipe recipe = db.getRecipeById(recipeId);
            if (recipe != null) {
                tvTitle.setText(recipe.getName());
                tvIngredients.setText(recipe.getIngredients());
                tvInstructions.setText(recipe.getInstructions());
            }
        }
    }
}