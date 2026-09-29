package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private long itemId = -1; // -1 indicates new item, existing ID indicates edit item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        databaseHelper = new DatabaseHelper(this);

        // Fixed IDs matching standard XML definitions
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        btnSave = findViewById(R.id.btnSave);

        // Check if an existing item was passed for editing
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getLongExtra("ITEM_ID", -1);
            if (itemId != -1) {
                loadExistingItemData(itemId);
                btnSave.setText("Update Ingredient");
            }
        }

        btnSave.setOnClickListener(v -> saveOrUpdateIngredient());
    }

    private void loadExistingItemData(long id) {
        Cursor cursor = databaseHelper.getPantryItemById(id);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            etQuantity.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"))));
            etUnit.setText(cursor.getString(cursor.getColumnIndexOrThrow("unit")));
            etExpiry.setText(cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
            cursor.close();
        }
    }

    private void saveOrUpdateIngredient() {
        String name = etName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);

        if (itemId == -1) {
            databaseHelper.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient updated!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}