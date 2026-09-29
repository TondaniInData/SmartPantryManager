package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Switch switchMetricUnits;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;

    public static final String PREFS_NAME = "SmartPantryPrefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    public static final String KEY_METRIC_UNITS = "metric_units";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        switchMetricUnits = findViewById(R.id.switchMetricUnits);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved preferences (default to true)
        boolean isExpiryAlertEnabled = sharedPreferences.getBoolean(KEY_EXPIRY_ALERTS, true);
        boolean isMetricEnabled = sharedPreferences.getBoolean(KEY_METRIC_UNITS, true);

        switchExpiryAlerts.setChecked(isExpiryAlertEnabled);
        switchMetricUnits.setChecked(isMetricEnabled);

        // Save preferences on toggle
        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            if (isChecked) {
                checkAndShowExpiryAlerts();
            } else {
                Toast.makeText(this, "Expiry alerts disabled", Toast.LENGTH_SHORT).show();
            }
        });

        switchMetricUnits.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_METRIC_UNITS, isChecked).apply();
            String mode = isChecked ? "Metric units enabled" : "Imperial units enabled";
            Toast.makeText(this, mode, Toast.LENGTH_SHORT).show();
        });

        // Trigger alert check on open if enabled
        if (isExpiryAlertEnabled) {
            checkAndShowExpiryAlerts();
        }
    }

    /**
     * Queries database for items expiring within 3 days and shows an Alert Dialog.
     */
    private void checkAndShowExpiryAlerts() {
        Cursor cursor = databaseHelper.getAllPantryItems();
        List<String> expiringItems = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_NAME));
                String expiryStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_EXPIRY));

                if (expiryStr != null && !expiryStr.isEmpty()) {
                    try {
                        Date expiryDate = sdf.parse(expiryStr);
                        if (expiryDate != null) {
                            long diffInMillies = expiryDate.getTime() - today.getTime();
                            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

                            // Items expiring within 3 days or already expired
                            if (diffInDays <= 3) {
                                expiringItems.add("• " + name + " (Expires: " + expiryStr + ")");
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (!expiringItems.isEmpty()) {
            StringBuilder message = new StringBuilder("The following items are expiring soon or expired:\n\n");
            for (String item : expiringItems) {
                message.append(item).append("\n");
            }

            new AlertDialog.Builder(this)
                    .setTitle("⚠️ Expiry Alert")
                    .setMessage(message.toString())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}