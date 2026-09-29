package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(long itemId);
    }

    private final Context context;
    private final List<PantryItem> pantryList;
    private final OnItemClickListener listener;
    private final DatabaseHelper databaseHelper;

    public PantryAdapter(Context context, List<PantryItem> pantryList, OnItemClickListener listener) {
        this.context = context;
        this.pantryList = pantryList;
        this.listener = listener;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(item.getQuantity() + " " + item.getUnit());
        holder.tvExpiry.setText("Expires: " + item.getExpiryDate());

        // 1. UPDATE: Click row item to open edit/update mode
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditIngredientActivity.class);
            intent.putExtra("ITEM_ID", (long) item.getId());
            context.startActivity(intent);
        });

        // 2. DELETE: Click dustbin icon to delete item immediately from SQLite database
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                boolean isDeleted = databaseHelper.deletePantryItem((long) item.getId());
                if (isDeleted) {
                    pantryList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, pantryList.size());
                    Toast.makeText(context, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Could not delete item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantryList != null ? pantryList.size() : 0;
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry;
        ImageButton btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPantryName);
            tvQuantity = itemView.findViewById(R.id.tvPantryQuantity);
            tvExpiry = itemView.findViewById(R.id.tvPantryExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}