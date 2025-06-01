package com.joo.sisteminvestarisbarang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private Context context;
    private OnItemActionListener listener;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public interface OnItemActionListener {
        void onEdit(Item item);
        void onDelete(Item item);
        void onDetail(Item item);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.typeTextView.setText(item.getTypeName());
        holder.locationTextView.setText(item.getLocation());
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDetail(item);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView, typeTextView, locationTextView;
        ImageButton btnEdit, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName);
            quantityTextView = itemView.findViewById(R.id.textQuantity);
            typeTextView = itemView.findViewById(R.id.textType);
            locationTextView = itemView.findViewById(R.id.textLocation);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
