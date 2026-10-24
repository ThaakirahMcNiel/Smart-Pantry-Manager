package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editProfileName;
    private Spinner spinnerDietaryPreference;
    private SharedPreferences preferences;
    private String[] dietaryOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dietaryOptions = getResources().getStringArray(R.array.dietary_options);

        editProfileName = findViewById(R.id.editProfileName);
        spinnerDietaryPreference = findViewById(R.id.spinnerDietaryPreference);
        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);
        Button btnBackSettings = findViewById(R.id.btnBackSettings);

        preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dietaryOptions
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerDietaryPreference.setAdapter(adapter);

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnBackSettings.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        String savedName = preferences.getString("profile_name", "");
        String savedDiet = preferences.getString(
                "dietary_preference",
                dietaryOptions[0]
        );

        editProfileName.setText(savedName);

        for (int i = 0; i < dietaryOptions.length; i++) {
            if (dietaryOptions[i].equals(savedDiet)) {
                spinnerDietaryPreference.setSelection(i);
                break;
            }
        }
    }

    private void saveSettings() {
        String name = editProfileName.getText().toString().trim();
        String preference = spinnerDietaryPreference
                .getSelectedItem().toString();

        if (name.isEmpty()) {
            editProfileName.setError(getString(R.string.profile_name_required));
            editProfileName.requestFocus();
            return;
        }

        preferences.edit()
                .putString("profile_name", name)
                .putString("dietary_preference", preference)
                .apply();

        Toast.makeText(
                this,
                R.string.settings_saved,
                Toast.LENGTH_SHORT
        ).show();
    }
}
