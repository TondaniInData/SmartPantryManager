package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private DatabaseHelper dbHelper;
    private long itemId = -1; // -1 means adding new item, valid ID means editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        Button btnSave = findViewById(R.id.btnSave);

        // Check if an item ID was passed to edit an existing ingredient
        itemId = getIntent().getLongExtra("PANTRY_ITEM_ID", -1);

        if (itemId != -1) {
            PantryItem item = dbHelper.getPantryItemById(itemId);
            if (item != null) {
                etName.setText(item.getName());
                etQuantity.setText(String.valueOf(item.getQuantity()));
                etUnit.setText(item.getUnit());
                etExpiry.setText(item.getExpiryDate());
                btnSave.setText("Update Ingredient");
            }
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Please enter name and quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (itemId == -1) {
            // Add new ingredient
            dbHelper.addPantryItem(name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing ingredient (e.g. correcting a typo from 'eggz' to 'egg')
            dbHelper.updatePantryItem(itemId, name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient updated!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}