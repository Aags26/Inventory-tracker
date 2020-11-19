package com.bphc.oops_project.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bphc.oops_project.R;
import com.bphc.oops_project.app.Constants;
import com.bphc.oops_project.fragments.dashboard.EssentialsFragment;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.DocumentHelper;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.ImageResponse;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.HashMap;

import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;

public class AddItemFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private String category;
    private int categoryId;
    private TextInputLayout addItem, addQuantity;
    private String inputItemName, inputQuantity;

    private ImageView itemImage;
    public final static int FILE_PICK = 1001;
    private File chosenFile;

    public static AddItemFragment newInstance(int categoryId, String category) {
        
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, categoryId);
        args.putString(ARG_PARAM2, category);
        AddItemFragment fragment = new AddItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_PARAM1, -1);
            category = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tick, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.update:
                if (!validateItemName() | !validateQuantity()) {
                    return false;
                }
                createUpload(chosenFile);
                //addNewItem();
                return true;
        }
        return onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_add_item);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        TextView textCategory = view.findViewById(R.id.item_category_text);
        textCategory.setText(category);

        itemImage = view.findViewById(R.id.add_item_image);

        TextView textItemAddImage = view.findViewById(R.id.text_add_item_image);
        textItemAddImage.setOnClickListener(this);

        addItem = view.findViewById(R.id.add_item_name);
        addQuantity = view.findViewById(R.id.add_item_quantity);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, FILE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FILE_PICK) return;
        if (resultCode != RESULT_OK) return;

        Uri returnUri = data.getData();
        Glide.with(getContext()).load(returnUri).into(itemImage);
        //String filePath = DocumentHelper.getPath(getContext(), returnUri);
        //if (filePath == null || filePath.isEmpty()) return;
        File file = new File(returnUri.getPath());
        Toast.makeText(getContext(), returnUri.getPath(), Toast.LENGTH_SHORT).show();
        final String[] split = file.getPath().split(":");
        chosenFile = new File(split[1]);
        Toast.makeText(getContext(), "Perf", Toast.LENGTH_SHORT).show();
    }

    private void createUpload(File image) {
        Retrofit retrofit = APIClient.getRetrofitInstance();
        Webservices webservices = retrofit.create(Webservices.class);
        Call<ImageResponse> call = webservices.postImage(Constants.getImgurClientId(),
                "title", "description", "", null,
                new TypedFile("image/*", image));
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    private void addNewItem() {

        String authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");
        Retrofit retrofit = APIClient.getRetrofitInstance();
        Webservices webservices = retrofit.create(Webservices.class);

        HashMap<String, Object> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("name", inputItemName);
        map.put("categoryId", categoryId);
        map.put("quantity", Integer.parseInt(inputQuantity));
        map.put("image", "");
        Call<ServerResponse> call = webservices.createItem(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (!response.body().getError() && response.body().getMessage().equals("Created item")) {
                    getActivity().onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateItemName() {
        inputItemName = addItem.getEditText().getText().toString().trim();
        if (inputItemName.isEmpty()) {
            addItem.setError("* Please give name");
            return false;
        } else {
            addItem.setError(null);
            return true;
        }
    }

    private boolean validateQuantity() {
        inputQuantity = addQuantity.getEditText().getText().toString().trim();
        if (inputQuantity.isEmpty()) {
            addQuantity.setError("* Please enter quantity");
            return false;
        } else {
            addQuantity.setError(null);
            return true;
        }
    }
}
