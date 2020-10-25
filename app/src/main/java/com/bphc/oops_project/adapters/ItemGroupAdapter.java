package com.bphc.oops_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.models.ItemGroup;

import java.util.ArrayList;


public class ItemGroupAdapter extends RecyclerView.Adapter<ItemGroupAdapter.ItemGroupViewHolder> {

    private ArrayList<ItemGroup> itemGroups;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public ItemGroupAdapter(ArrayList<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }

    public static class ItemGroupViewHolder extends RecyclerView.ViewHolder{

        TextView itemGroup;
        RecyclerView childRecycler;

        public ItemGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            itemGroup = itemView.findViewById(R.id.item_group_text);
            childRecycler = itemView.findViewById(R.id.child_recycler);

        }
    }

    @NonNull
    @Override
    public ItemGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item_group, parent, false);
        return new ItemGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemGroupViewHolder holder, int position) {
        ItemGroup itemGroup = itemGroups.get(position);
        holder.itemGroup.setText(itemGroup.getGroup());
        LinearLayoutManager childLayoutManager = new LinearLayoutManager(
                holder.childRecycler.getContext(),
                RecyclerView.HORIZONTAL,
                false);
        ItemAdapter adapter = new ItemAdapter(itemGroup.getItems());

        childLayoutManager.setInitialPrefetchItemCount(itemGroup.getItems().size());

        holder.childRecycler.setLayoutManager(childLayoutManager);
        holder.childRecycler.setAdapter(adapter);
        holder.childRecycler.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return itemGroups.size();
    }

}
