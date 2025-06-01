package com.joo.sisteminvestarisbarang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<String> categories;
    private OnCategoryDeleteListener listener;
    private boolean allowDelete = true;

    public interface OnCategoryDeleteListener {
        void onDelete(String name);
    }
    public void setOnCategoryDeleteListener(OnCategoryDeleteListener listener) {
        this.listener = listener;
    }
    public void setAllowDelete(boolean allow) {
        this.allowDelete = allow;
        notifyDataSetChanged();
    }
    public CategoryAdapter(List<String> categories) {
        this.categories = categories;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = categories.get(position);
        holder.textCategoryName.setText(name);
        holder.btnDeleteCategory.setVisibility(allowDelete ? View.VISIBLE : View.GONE);
        holder.btnDeleteCategory.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(name);
        });
    }
    @Override
    public int getItemCount() {
        return categories.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCategoryName;
        ImageButton btnDeleteCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoryName = itemView.findViewById(R.id.textCategoryName);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
