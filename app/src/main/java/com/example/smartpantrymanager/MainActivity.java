package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPantryItems;
    private PantryAdapter adapter;
    private List<PantryItem> pantryList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        rvPantryItems = findViewById(R.id.rvPantryItems);
        rvPantryItems.setLayoutManager(new LinearLayoutManager(this));

        pantryList = new ArrayList<>();

        // Fix: itemId is passed directly as a long primitive
        adapter = new PantryAdapter(this, pantryList, itemId -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            intent.putExtra("ITEM_ID", itemId);
            startActivity(intent);
        });

        rvPantryItems.setAdapter(adapter);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        if (pantryList != null && adapter != null && databaseHelper != null) {
            pantryList.clear();

            Cursor cursor = databaseHelper.getAllPantryItems();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"));
                    String unit = cursor.getString(cursor.getColumnIndexOrThrow("unit"));
                    String expiry = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));

                    pantryList.add(new PantryItem(id, name, quantity, unit, expiry));
                }
                cursor.close();
            }
            adapter.notifyDataSetChanged();
        }
    }
}