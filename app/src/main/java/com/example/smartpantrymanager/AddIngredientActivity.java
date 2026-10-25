package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText editIngredientName;
    private EditText editIngredientQuantity;
    private EditText editIngredientUnit;
    private EditText editIngredientExpiry;
    private int ingredientId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        editIngredientName = findViewById(R.id.editIngredientName);
        editIngredientQuantity = findViewById(R.id.editIngredientQuantity);
        editIngredientUnit = findViewById(R.id.editIngredientUnit);
        editIngredientExpiry = findViewById(R.id.editIngredientExpiry);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);
        Button btnDelete = findViewById(R.id.btnDeleteIngredient);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            setTitle("Edit Ingredient");
            btnSave.setText(R.string.update_ingredient);
            btnDelete.setVisibility(View.VISIBLE);
            loadIngredientDetails();
        }else{
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnCancel.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> confirmDeleteIngredient());
    }
    private void loadIngredientDetails() {
        PantryDatabaseHelper helper = new PantryDatabaseHelper(this);

        try {
            try (Cursor cursor = helper.getPantryItemById(ingredientId)) {
                if (cursor != null && cursor.moveToFirst()) {
                    editIngredientName.setText(
                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    PantryDatabaseHelper.COL_NAME)));

                    editIngredientQuantity.setText(
                            String.valueOf(cursor.getDouble(
                                    cursor.getColumnIndexOrThrow(
                                            PantryDatabaseHelper.COL_QUANTITY))));

                    editIngredientUnit.setText(
                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    PantryDatabaseHelper.COL_UNIT)));

                    String expiry = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    PantryDatabaseHelper.COL_EXPIRY));

                    editIngredientExpiry.setText(
                            expiry == null ? "" : expiry);
                }
            }
        } finally {
            helper.close();
        }
    }
    private void saveIngredient() {
        String name = editIngredientName.getText().toString().trim();
        String quantityText =
                editIngredientQuantity.getText().toString().trim();
        String unit = editIngredientUnit.getText().toString().trim();
        String expiry =
                editIngredientExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            editIngredientName.setError("Enter an ingredient name");
            editIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            editIngredientQuantity.setError("Enter a quantity");
            editIngredientQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            editIngredientUnit.setError("Enter a unit");
            editIngredientUnit.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editIngredientQuantity.setError("Enter a valid quantity");
            editIngredientQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            editIngredientQuantity.setError(
                    "Quantity must be greater than zero");
            editIngredientQuantity.requestFocus();
            return;
        }

        PantryDatabaseHelper databaseHelper =
                new PantryDatabaseHelper(this);

        long result;

        if (ingredientId == -1) {
            result = databaseHelper.addPantryItem(
                    name,
                    quantity,
                    unit,
                    expiry.isEmpty() ? null : expiry
            );
        } else {
            result = databaseHelper.updatePantryItem(
                    ingredientId,
                    name,
                    quantity,
                    unit,
                    expiry.isEmpty() ? null : expiry
            );
        }

        databaseHelper.close();

        if (result != -1 && result != 0) {
            Toast.makeText(
                    this,
                    ingredientId == -1
                            ? "Ingredient saved successfully!"
                            : "Ingredient updated successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(
                    this,
                    "No changes saved. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    private void confirmDeleteIngredient() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?"
                )
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteIngredient();
                })
                .show();
    }

    private void deleteIngredient() {
        PantryDatabaseHelper helper =
                new PantryDatabaseHelper(this);

        int result;

        try {
            result = helper.deletePantryItem(ingredientId);
        } finally {
            helper.close();
        }

        if (result > 0) {
            Toast.makeText(
                    this,
                    "Ingredient deleted successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(
                    this,
                    "Could not delete ingredient. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
