package com.bphc.oops_project.fragments.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bphc.oops_project.MainActivity;
import com.bphc.oops_project.R;
import com.bphc.oops_project.models.User;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.USER;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.USER_PHOTO;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        CircleImageView profileImage = view.findViewById(R.id.profile_image);
        Uri profileUri = Uri.parse(SharedPrefs.getStringParams(getContext(), USER_PHOTO, ""));
        Glide.with(getContext()).load(profileUri).into(profileImage);

        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();
        User user = gson.fromJson(SharedPrefs.getStringParams(getContext(), USER, ""), type);

        TextView name = view.findViewById(R.id.profile_name);
        name.setText(user.getName());
        TextView email = view.findViewById(R.id.profile_email);
        email.setText(user.getEmail());
        TextView username = view.findViewById(R.id.profile_username);
        username.setText(user.getUsername());
        TextView profession = view.findViewById(R.id.profile_profession);
        profession.setText(user.getProfession());
        TextView phone = view.findViewById(R.id.profile_phone);
        phone.setText(user.getPhone());

        Button buttonSignOut = view.findViewById(R.id.button_sign_out);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return view;
    }

    private void signOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPrefs.removeKey(getContext(), JWTS_TOKEN);
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }

}
