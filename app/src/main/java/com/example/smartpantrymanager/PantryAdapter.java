package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    private Context context;
    private List<PantryItem> pantryList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(long id);
    }

    public PantryAdapter(Context context, List<PantryItem> pantryList, OnItemClickListener listener) {
        this.context = context;
        this.pantryList = pantryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = pantryList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(item.getQuantity() + " " + item.getUnit());
        holder.tvExpiry.setText("Expires: " + item.getExpiryDate());

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item.getId()));
    }

    @Override
    public int getItemCount() {
        return pantryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPantryName);
            tvQuantity = itemView.findViewById(R.id.tvPantryQuantity);
            tvExpiry = itemView.findViewById(R.id.tvPantryExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}