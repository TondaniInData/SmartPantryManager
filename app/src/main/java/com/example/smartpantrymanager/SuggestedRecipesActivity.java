package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private ListView lvRecipes;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private List<Recipe> matchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        lvRecipes = findViewById(R.id.lvSuggestedRecipes);
        tvEmpty = findViewById(R.id.tvEmptyMessage);
        dbHelper = new DatabaseHelper(this);

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        matchingRecipes = dbHelper.getMatchingRecipes();
        List<String> displayList = new ArrayList<>();

        if (matchingRecipes.isEmpty()) {
            if (tvEmpty != null) {
                tvEmpty.setText("No recipes match your pantry yet - add more ingredients!");
            }
        } else {
            if (tvEmpty != null) {
                tvEmpty.setText("Recipes You Can Make Now:");
            }
            for (Recipe recipe : matchingRecipes) {
                displayList.add(recipe.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        lvRecipes.setAdapter(adapter);

        lvRecipes.setOnItemClickListener((parent, view, position, id) -> {
            if (!matchingRecipes.isEmpty()) {
                Recipe selectedRecipe = matchingRecipes.get(position);
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("RECIPE_ID", selectedRecipe.getId());
                startActivity(intent);
            }
        });
    }
}
