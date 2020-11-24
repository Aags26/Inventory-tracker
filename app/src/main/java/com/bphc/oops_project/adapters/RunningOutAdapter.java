package com.bphc.oops_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.models.Item;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RunningOutAdapter extends RecyclerView.Adapter<RunningOutAdapter.RunningOutViewHolder> {

    private ArrayList<Item> runningOutItems;
    private OnItemClickListener listener;
    private Context context;

    public RunningOutAdapter(ArrayList<Item> runningOutItems, Context context) {
        this.runningOutItems = runningOutItems;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class RunningOutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView itemImage, increaseImage, decreaseImage;;
        OnItemClickListener listener;
        TextView itemName, itemQuantity, scheduleTask, itemCategory;

        public RunningOutViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.listener = listener;

            itemImage = itemView.findViewById(R.id.running_out_item_image);
            increaseImage = itemView.findViewById(R.id.running_out_increase_quantity);
            decreaseImage = itemView.findViewById(R.id.running_out_decrease_quantity);
            itemName = itemView.findViewById(R.id.running_out_item_name);
            itemQuantity = itemView.findViewById(R.id.running_out_item_quantity);
            itemCategory = itemView.findViewById(R.id.running_out_item_category);

            scheduleTask = itemView.findViewById(R.id.running_out_schedule_task);
            scheduleTask.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, v.getId());
                }
            }
        }
    }

    @NonNull
    @Override
    public RunningOutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_running_out, parent, false);
        return new RunningOutAdapter.RunningOutViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RunningOutViewHolder holder, int position) {
        Item runningOutItem = runningOutItems.get(position);
        if (runningOutItem.getImage() == null) holder.itemImage.setImageResource(R.mipmap.ic_launcher);
        else Glide.with(context).load(runningOutItem.getImage()).into(holder.itemImage);
        holder.itemName.setText(runningOutItem.getName());
        holder.itemQuantity.setText(runningOutItem.getQuantity());
        holder.itemCategory.setText(runningOutItem.getCategory());
    }

    @Override
    public int getItemCount() {
        return runningOutItems.size();
    }
}
