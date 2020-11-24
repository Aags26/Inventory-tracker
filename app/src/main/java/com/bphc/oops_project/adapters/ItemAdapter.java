package com.bphc.oops_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.models.Item;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private ArrayList<Item> items;
    private OnItemClickListener listener;
    private Context context;

    public ItemAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView itemImage, deleteImage, increaseImage, decreaseImage;
        TextView itemName, itemQuantity;
        OnItemClickListener listener;
        ArrayList<Item> items;
        Context context;

        public ItemViewHolder(@NonNull View itemView, OnItemClickListener listener, ArrayList<Item> items, Context context) {
            super(itemView);

            this.listener = listener;
            this.items = items;
            this.context = context;

            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            deleteImage = itemView.findViewById(R.id.delete_image);
            increaseImage = itemView.findViewById(R.id.increase_quantity);
            decreaseImage = itemView.findViewById(R.id.decrease_quantity);

            deleteImage.setOnClickListener(this);
            increaseImage.setOnClickListener(this);
            decreaseImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, items.get(position).getPositionParent(context, items.get(position).getCategory()), v.getId());
                }
            }
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, parent, false);
        return new ItemViewHolder(view, listener, items, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        if (item.getImage() == null) holder.itemImage.setImageResource(R.mipmap.ic_launcher);
        else Glide.with(context).load(item.getImage()).into(holder.itemImage);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
