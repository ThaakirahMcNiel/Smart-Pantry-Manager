package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

public class PantryAdapter extends ArrayAdapter<PantryItem> {

    public PantryAdapter(Context context, List<PantryItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_pantry, parent, false);
        }

        PantryItem item = getItem(position);

        TextView txtName = convertView.findViewById(R.id.txtPantryItemName);
        TextView txtQuantity = convertView.findViewById(R.id.txtPantryItemQuantity);

        if (item != null) {
            txtName.setText(item.getName());

            String quantityText = String.format(
                    Locale.getDefault(),
                    "%.2f %s",
                    item.getQuantity(),
                    item.getUnit()
            );

            txtQuantity.setText(quantityText);
        }

        return convertView;
    }
}

