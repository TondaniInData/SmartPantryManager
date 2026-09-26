package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Switch switchMetricUnits;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "SmartPantryPrefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    private static final String KEY_METRIC_UNITS = "metric_units";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        switchMetricUnits = findViewById(R.id.switchMetricUnits);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved preferences (default to true)
        switchExpiryAlerts.setChecked(sharedPreferences.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchMetricUnits.setChecked(sharedPreferences.getBoolean(KEY_METRIC_UNITS, true));

        // Save preferences on toggle
        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
        });

        switchMetricUnits.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_METRIC_UNITS, isChecked).apply();
        });
    }
}