package com.bphc.oops_project.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.bphc.oops_project.models.Result;
import com.bphc.oops_project.models.User;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.PHONE_VERIFIED;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.PROFESSION_GIVEN;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.USER;

public class GoogleAuthFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private String idToken;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_auth, container, false);

        SignInButton signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        Button signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");
        if (!authToken.isEmpty()) {
            updateUI();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            signIn();
        } else {
            signOut();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI() {
        Type type = new TypeToken<User>(){}.getType();
        User user = gson.fromJson(SharedPrefs.getStringParams(getContext(), USER, ""), type);
        if (SharedPrefs.getBooleanParams(getContext(), PHONE_VERIFIED) && SharedPrefs.getBooleanParams(getContext(), PROFESSION_GIVEN)) {
            toDashboard(user);
        } else if (SharedPrefs.getBooleanParams(getContext(), PHONE_VERIFIED)) {
            toUserAuth(user);
        } else {
            toPhoneAuth(user);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            idToken = account.getIdToken();
            sendTokenToServer();
        }
    }

    private void sendTokenToServer() {

        Retrofit retrofit = APIClient.getRetrofitInstance();
        Webservices webservices = retrofit.create(Webservices.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("idToken", idToken);

        Call<ServerResponse> call = webservices.authWithGoogle(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse mServerResponse = response.body();
                if (mServerResponse != null) {
                    Result result = mServerResponse.getResult();
                    SharedPrefs.setStringParams(getContext(), JWTS_TOKEN, result.authToken);
                    if (result.phoneVerified && result.professionGiven) {
                        User user = new User(result.email, result.name, result.username, result.profession, result.phone);
                        toDashboard(user);
                    } else {
                        User user = new User(result.email, result.name, "", "", "");
                        toPhoneAuth(user);
                    }

                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toPhoneAuth(User user) {
        SharedPrefs.setStringParams(getContext(), USER, gson.toJson(user));

        final NavController navController = Navigation.findNavController(requireView());
        GoogleAuthFragmentDirections.ActionGoogleAuthFragmentToPhoneAuthFragment action
                = GoogleAuthFragmentDirections.actionGoogleAuthFragmentToPhoneAuthFragment();
        action.setUser(user);

        navController.navigate(action);
    }

    private void toUserAuth(User user) {
        SharedPrefs.setStringParams(getContext(), USER, gson.toJson(user));

        final NavController navController = Navigation.findNavController(requireView());
        GoogleAuthFragmentDirections.ActionGoogleAuthFragmentToUserDetailsFragment action
                = GoogleAuthFragmentDirections.actionGoogleAuthFragmentToUserDetailsFragment();
        action.setUser(user);

        navController.navigate(action);
    }

    private void toDashboard(User user) {
        SharedPrefs.setStringParams(getContext(), USER, gson.toJson(user));

        final NavController navController = Navigation.findNavController(requireView());
        GoogleAuthFragmentDirections.ActionGoogleAuthFragmentToDashboardFragment action
                = GoogleAuthFragmentDirections.actionGoogleAuthFragmentToDashboardFragment();
        action.setUser(user);

        navController.navigate(action);
    }



}
