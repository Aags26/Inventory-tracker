package com.bphc.oops_project.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.adapters.ItemGroupAdapter;
import com.bphc.oops_project.models.Item;
import com.bphc.oops_project.models.ItemGroup;

import java.util.ArrayList;
import java.util.List;

public class EssentialsFragment extends Fragment {

    private RecyclerView parentRecycler;
    private ItemGroupAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_essentials, container, false);

        ArrayList<ItemGroup> itemGroups = new ArrayList<>();
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item(R.mipmap.ic_launcher, "Abc", "5"));
        items.add(new Item(R.mipmap.ic_launcher, "Abc", "5"));
        items.add(new Item(R.mipmap.ic_launcher, "Abc", "5"));

        itemGroups.add(new ItemGroup("Groceries", items));
        itemGroups.add(new ItemGroup("Stationery", items));

        parentRecycler = view.findViewById(R.id.parent_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new ItemGroupAdapter(itemGroups);

        parentRecycler.setLayoutManager(layoutManager);
        parentRecycler.setAdapter(adapter);

        return view;
    }
}
