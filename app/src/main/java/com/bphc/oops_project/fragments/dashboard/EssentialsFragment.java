package com.bphc.oops_project.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.adapters.ItemGroupAdapter;
import com.bphc.oops_project.adapters.RunningOutAdapter;
import com.bphc.oops_project.app.Constants;
import com.bphc.oops_project.fragments.AddItemFragment;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.Item;
import com.bphc.oops_project.models.ItemGroup;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;

public class EssentialsFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    private ItemGroupAdapter adapter;
    private RunningOutAdapter runningOutAdapter;
    private ImageView listEmptyImage;
    private ArrayList<ItemGroup> itemGroups = new ArrayList<>();
    private ArrayList<Item> runningOutItems = new ArrayList<>();
    private AlertDialog alertDialog;
    private String inputCategory, inputQuantity, authToken;
    private TextInputLayout addCategory, updateQuantity;
    private Webservices webservices;
    private int position, posItem, posParent;
    private Button add_update;
    private ConstraintLayout runningOutLayout;
    private TextView textSufficient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");
        Retrofit retrofit = APIClient.getRetrofitInstance();
        webservices = retrofit.create(Webservices.class);
        getUserEssentials();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_essentials, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar_essentials);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ImageView addCategoryImage = view.findViewById(R.id.category_add_image);
        addCategoryImage.setOnClickListener(this);

        runningOutLayout = view.findViewById(R.id.running_out_view);
        textSufficient = view.findViewById(R.id.text_sufficient);

        RecyclerView runningOutRecycler = view.findViewById(R.id.running_out_recycler);
        RecyclerView.LayoutManager runningOutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        runningOutAdapter = new RunningOutAdapter(runningOutItems, getContext());
        runningOutRecycler.setLayoutManager(runningOutManager);
        runningOutRecycler.setAdapter(runningOutAdapter);

        RecyclerView parentRecycler = view.findViewById(R.id.parent_recycler);
        listEmptyImage = view.findViewById(R.id.empty_list_image);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new ItemGroupAdapter(itemGroups, getContext());

        parentRecycler.setLayoutManager(layoutManager);
        parentRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
        runningOutAdapter.setOnItemClickListener(this);

        return view;
    }

    private void getUserEssentials() {

        Call<ServerResponse> call = webservices.getUserDashboard(authToken);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse mServerResponse = response.body();
                if (mServerResponse != null) {
                    if (!mServerResponse.getError()) {
                        itemGroups.clear();
                        runningOutItems.clear();
                        itemGroups.addAll(mServerResponse.getResult().getCategories());
                        for (int pos = 0; pos < itemGroups.size(); pos++) {
                            SharedPrefs.setIntParams(getContext(), itemGroups.get(pos).getGroup(), pos);
                        }
                        adapter.notifyDataSetChanged();
                        if (itemGroups.isEmpty()) {
                            listEmptyImage.setVisibility(View.VISIBLE);
                            textSufficient.setVisibility(View.GONE);
                        }
                        else {
                            listEmptyImage.setVisibility(View.GONE);
                            textSufficient.setVisibility(View.VISIBLE);
                        }

                        if (mServerResponse.getResult().getRunningOutItems().isEmpty())
                            runningOutLayout.setVisibility(View.GONE);
                        else {
                            runningOutLayout.setVisibility(View.VISIBLE);
                            runningOutItems.addAll(mServerResponse.getResult().getRunningOutItems());
                            runningOutAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(int position, int id) {
        this.position = position;
        ItemGroup itemGroup = itemGroups.get(position);
        if (id == R.id.item_add_image) {
            int categoryId = itemGroup.getCategoryId();
            String category = itemGroup.getGroup();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, AddItemFragment.newInstance(categoryId, category))
                        .commit();
            }
        } else if (id == R.id.edit_category_image) {
            inflateCategoryDialog(itemGroup.getGroup());
        } else {
            deleteCategory(itemGroup.getCategoryId());
        }
    }

    @Override
    public void onItemClick(int positionItem, int positionParent, int id) {
        posItem = positionItem;
        posParent = positionParent;
        if (id == R.id.increase_quantity || id == R.id.decrease_quantity) {
            inflateUpdateQuantityDialog();
        }
        if (id == R.id.delete_image) {
            getUserEssentials();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.category_add_image) {
            inflateCategoryDialog("");
        } else if (id == R.id.button_add_category) {
            if (!validateCategory())
                return;
            if (add_update.getText().equals("Add")) addNewCategory();
            else editCategory(itemGroups.get(position).getCategoryId());
        } else if (id == R.id.button_cancel) {
            alertDialog.dismiss();
        } else if (id == R.id.image_update) {
            if (!validateQuantity())
                return;
            addNewQuantity();
        } else if (id == R.id.image_close) {
            alertDialog.dismiss();
        }

    }


    private void inflateCategoryDialog(String inputCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.dialog_add_category, null);
        builder.setView(view)
                .setTitle("Add/Edit a Category: ");

        addCategory = view.findViewById(R.id.layout_add_category);
        addCategory.getEditText().setText(inputCategory);
        add_update = view.findViewById(R.id.button_add_category);
        if (inputCategory.isEmpty()) add_update.setText("Add");
        else add_update.setText("Update");
        Button cancel = view.findViewById(R.id.button_cancel);

        alertDialog = builder.create();
        alertDialog.show();

        add_update.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void inflateUpdateQuantityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.dialog_change_quantity, null);
        builder.setView(view);

        TextView textCurrentQuantity = view.findViewById(R.id.text_current_quantity);
        String currentQuantity = "Current Quantity: " + itemGroups.get(posParent).getItems().get(posItem).getQuantity();
        textCurrentQuantity.setText(currentQuantity);
        updateQuantity = view.findViewById(R.id.layout_update_quantity);
        ImageView updateImage = view.findViewById(R.id.image_update);
        ImageView closeImage = view.findViewById(R.id.image_close);

        alertDialog = builder.create();
        alertDialog.show();

        updateImage.setOnClickListener(this);
        closeImage.setOnClickListener(this);

    }

    private void addNewCategory() {

        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("category", inputCategory);
        Call<ServerResponse> call = webservices.createCategory(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (!response.body().getError() && response.body().getMessage().equals("Created category")) {
                    alertDialog.dismiss();
                    getUserEssentials();
                    listEmptyImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void editCategory(int categoryId) {

        if (inputCategory.equalsIgnoreCase(itemGroups.get(position).getGroup())) {
            alertDialog.dismiss();
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("category", inputCategory);
        map.put("categoryId", Integer.toString(categoryId));
        Call<ServerResponse> call = webservices.editCategory(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (!response.body().getError() && response.body().getMessage().equals("Edited category name")) {
                    itemGroups.get(position).setGroup(inputCategory);
                    adapter.notifyItemChanged(position);
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void deleteCategory(int categoryId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("categoryId", Integer.toString(categoryId));

        Call<ServerResponse> call = webservices.deleteCategory(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (!response.body().getError() && response.body().getMessage().equals("Deleted category")) {
                    SharedPrefs.removeKey(getContext(), itemGroups.get(position).getGroup());
                    itemGroups.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (itemGroups.isEmpty()) listEmptyImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void addNewQuantity() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authToken", Constants.getAuthToken(getContext()));
        map.put("itemId", itemGroups.get(posParent).getItems().get(posItem).getItemId());
        map.put("quantity", Integer.parseInt(inputQuantity));
        Call<ServerResponse> call = webservices.updateItemQuantity(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body() != null) {
                    if (!response.body().getError()) {
                        alertDialog.dismiss();
                        itemGroups.clear();
                        getUserEssentials();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateCategory() {
        inputCategory = addCategory.getEditText().getText().toString().trim();
        if (inputCategory.isEmpty()) {
            addCategory.setError("* Please give a name");
            return false;
        } else {
            addCategory.setError(null);
            return true;
        }
    }

    private boolean validateQuantity() {
        inputQuantity = updateQuantity.getEditText().getText().toString().trim();
        if (inputQuantity.isEmpty()) {
            updateQuantity.setError("Please add a quantity");
            return false;
        } else {
            updateQuantity.setError(null);
            return true;
        }
    }

}
