package com.bphc.oops_project.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.Progress;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.ImageResponse;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;

public class AddItemFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private String category, encoded = "", link;
    private int categoryId;
    private TextInputLayout addItem, addQuantity, addRoQuantity, addUnit;
    private String inputItemName, inputQuantity;
    private Webservices webservices;

    private ImageView itemImage;
    public final static int IMAGE_PICK = 1001;
    public final static int IMAGE_CLICK = 1002;
    private ProgressDialog progressDialog;

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
        Retrofit retrofit = APIClient.getRetrofitInstance();
        webservices = retrofit.create(Webservices.class);
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
                if (getActivity() != null) getActivity().onBackPressed();
                return true;
            case R.id.update:
                if (!validateItemName() | !validateQuantity()) {
                    return false;
                }
                if (!encoded.isEmpty()) createUpload();
                else addNewItem();
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

        progressDialog = Progress.getProgressDialog(getContext());
        itemImage = view.findViewById(R.id.add_item_image);

        TextView galleryImage = view.findViewById(R.id.text_gallery);
        galleryImage.setOnClickListener(this);

        TextView cameraImage = view.findViewById(R.id.text_camera);
        cameraImage.setOnClickListener(this);

        addItem = view.findViewById(R.id.add_item_name);
        addQuantity = view.findViewById(R.id.add_item_quantity);
        addRoQuantity = view.findViewById(R.id.layout_ro_quantity);
        addUnit = view.findViewById(R.id.add_item_quantity_unit);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (((TextView) v).getText().toString()) {
            case "Gallery":
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICK);
                break;
            case "Camera":
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMAGE_CLICK);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case IMAGE_PICK:
                    if (data.getData() != null) {
                        Uri returnUri = data.getData();
                        try {
                            Glide.with(requireContext()).load(returnUri).into(itemImage);
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), returnUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case IMAGE_CLICK:
                    if (data.getExtras() != null) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        itemImage.setImageBitmap(bitmap);
                    }
                    break;
            }
            if (bitmap == null) return;
            encodeImage(bitmap);
        }

    }

    private void encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void createUpload() {

        Progress.showProgress(true, "Uploading...may take a while");
        Call<ImageResponse> call = webservices.postImage(Constants.getImgurClientId(), encoded);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                ImageResponse mImageResponse = response.body();
                if (mImageResponse != null) {
                    if (mImageResponse.isSuccess()) {
                        ImageResponse.UploadedImage data = mImageResponse.getData();
                        link = data.link;
                        addNewItem();
                        Progress.dismissProgress(progressDialog);
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    private void addNewItem() {

        Progress.showProgress(true, "Uploading...");
        String authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");

        HashMap<String, Object> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("name", inputItemName);
        map.put("categoryId", categoryId);
        map.put("quantity", Integer.parseInt(inputQuantity));
        map.put("unit", addUnit.getEditText().getText().toString().trim());
        if (!addRoQuantity.getEditText().getText().toString().trim().isEmpty())
            map.put("roQuantity", Integer.parseInt(addRoQuantity.getEditText().getText().toString().trim()));
        map.put("image", link);
        Call<ServerResponse> call = webservices.createItem(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (!response.body().getError() && response.body().getMessage().equals("Created item")) {
                    Progress.dismissProgress(progressDialog);
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
