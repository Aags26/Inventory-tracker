package com.bphc.oops_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.app.Constants;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.ItemGroup;
import com.bphc.oops_project.models.ServerResponse;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ItemGroupAdapter extends RecyclerView.Adapter<ItemGroupAdapter.ItemGroupViewHolder> implements OnItemClickListener{

    private ArrayList<ItemGroup> itemGroups;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private OnItemClickListener listener;
    private ItemAdapter adapter;
    private Context context;

    public ItemGroupAdapter(ArrayList<ItemGroup> itemGroups, Context context) {
        this.itemGroups = itemGroups;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item_group, parent, false);
        return new ItemGroupViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemGroupViewHolder holder, int position) {
        ItemGroup itemGroup = itemGroups.get(position);
        holder.itemGroup.setText(itemGroup.getGroup());
        LinearLayoutManager childLayoutManager = new LinearLayoutManager(
                holder.childRecycler.getContext(),
                RecyclerView.HORIZONTAL,
                false);
        adapter = new ItemAdapter(itemGroup.getItems(), context);

        childLayoutManager.setInitialPrefetchItemCount(itemGroup.getItems().size());

        holder.childRecycler.setLayoutManager(childLayoutManager);
        holder.childRecycler.setAdapter(adapter);
        holder.childRecycler.setRecycledViewPool(viewPool);

        adapter.setOnItemClickListener(this);

    }

    @Override
    public int getItemCount() {
        return itemGroups.size();
    }

    @Override
    public void onItemClick(int position, int id) {

    }

    @Override
    public void onItemClick(int positionItem, int positionParent, int id) {
        if (id == R.id.delete_image) {
            int itemId = itemGroups.get(positionParent).getItems().get(positionItem).getItemId();
            removeItem(positionItem, positionParent, itemId, id);
        } else listener.onItemClick(positionItem, positionParent, id);

    }

    private void removeItem(int posItem, int posParent, int itemId, int viewId) {
        Retrofit retrofit = APIClient.getRetrofitInstance();
        Webservices webservices = retrofit.create(Webservices.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("authToken", Constants.getAuthToken(context));
        map.put("itemId", itemId);

        Call<ServerResponse> call = webservices.deleteItem(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body() != null) {
                    if (!response.body().getError()) {
                        itemGroups.get(posParent).getItems().remove(posItem);
                        adapter.notifyItemRemoved(posItem);
                        listener.onItemClick(posItem, posParent, viewId);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    public static class ItemGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemGroup;
        RecyclerView childRecycler;
        ImageView addItemImage, deleteCategoryImage, editCategoryImage;
        OnItemClickListener listener;

        public ItemGroupViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            itemGroup = itemView.findViewById(R.id.item_group_text);
            childRecycler = itemView.findViewById(R.id.child_recycler);
            addItemImage = itemView.findViewById(R.id.item_add_image);
            deleteCategoryImage = itemView.findViewById(R.id.delete_category_image);
            editCategoryImage = itemView.findViewById(R.id.edit_category_image);

            this.listener = listener;

            addItemImage.setOnClickListener(this);
            deleteCategoryImage.setOnClickListener(this);
            editCategoryImage.setOnClickListener(this);

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

}
