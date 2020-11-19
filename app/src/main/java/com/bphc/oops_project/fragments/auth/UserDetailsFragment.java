package com.bphc.oops_project.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bphc.oops_project.R;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.models.User;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.PROFESSION_GIVEN;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.USER;

public class UserDetailsFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout username;
    private String inputUsername, profession;
    private RadioGroup radioGroup;
    private Button buttonSubmit;

    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = UserDetailsFragmentArgs.fromBundle(getArguments()).getUser();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        username = view.findViewById(R.id.username);
        radioGroup = view.findViewById(R.id.radioGroup);
        buttonSubmit = view.findViewById(R.id.button_submit);

        buttonSubmit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = getView().findViewById(radioId);
        profession = radioButton.getText().toString();
        if (!validateUsername())
            return;
        if (radioId == -1) {
            Toast.makeText(getContext(), "Select your profession", Toast.LENGTH_SHORT).show();
            return;
        }
        addUserDetails();

    }

    private boolean validateUsername() {
        inputUsername = username.getEditText().getText().toString();
        if (inputUsername.isEmpty()) {
            username.setError("* Cannot be empty.");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    private void addUserDetails() {
        String authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");
        if (!authToken.isEmpty()) {

            Retrofit retrofit = APIClient.getRetrofitInstance();
            Webservices webservices = retrofit.create(Webservices.class);

            HashMap<String, String> map = new HashMap<>();
            map.put("authToken", authToken);
            map.put("username", inputUsername);
            map.put("profession", profession);

            Call<ServerResponse> call = webservices.addUserDetails(map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ServerResponse mServerResponse = response.body();
                    if (mServerResponse != null) {
                        if (!mServerResponse.error && mServerResponse.message.equals("Updated details")) {

                            SharedPrefs.setBooleanParams(getContext(), PROFESSION_GIVEN, true);
                            Gson gson = new Gson();
                            user.setUsername(inputUsername);
                            user.setProfession(profession);

                            SharedPrefs.setStringParams(getContext(), USER, gson.toJson(user));

                            final NavController navController = Navigation.findNavController(getView());
                            UserDetailsFragmentDirections.ActionUserDetailsFragmentToDashboardFragment action
                                    = UserDetailsFragmentDirections.actionUserDetailsFragmentToDashboardFragment();
                            action.setUser(user);
                            navController.navigate(action);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {

                }
            });

        }
    }
}
