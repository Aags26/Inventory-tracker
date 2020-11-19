package com.bphc.oops_project.fragments.auth;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bphc.oops_project.R;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.AppSignatureHashHelper;
import com.bphc.oops_project.helper.MySMSBroadcastReceiver;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.models.User;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.PHONE_VERIFIED;
import static com.bphc.oops_project.prefs.SharedPrefsConstants.USER;

public class PhoneAuthFragment extends Fragment implements View.OnClickListener, MySMSBroadcastReceiver.SmsListener {

    private TextInputLayout phoneNumber, smsOtp;
    private Button button_getOtp;
    private TextView resendOtp;
    private String inputPhone;
    private int RESOLVE_HINT = 7;
    private CredentialsClient mCredentialsClient;
    private String authToken;
    private Retrofit retrofit = null;
    private Webservices webservices;
    private MySMSBroadcastReceiver smsReceiver;
    private String hash;

    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = PhoneAuthFragmentArgs.fromBundle(getArguments()).getUser();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragent_phone_auth, container, false);

        retrofit = APIClient.getRetrofitInstance();
        webservices = retrofit.create(Webservices.class);

        phoneNumber = view.findViewById(R.id.phone_number);
        smsOtp = view.findViewById(R.id.layout_text_otp);
        mCredentialsClient = Credentials.getClient(requireContext());

        button_getOtp = view.findViewById(R.id.button_getOtp);
        button_getOtp.setOnClickListener(this);

        resendOtp = view.findViewById(R.id.resend_otp);

        authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(getContext());
        hash = appSignatureHashHelper.getAppSignatures().get(0);
        Log.d("TAG", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));

        pickPhoneNumber();
        startSmsRetriever();

        return view;
    }

    private void pickPhoneNumber() {

        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent pendingIntent = mCredentialsClient.getHintPickerIntent(hintRequest);

        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String phone_number = credential.getId();
                phoneNumber.getEditText().setText(phone_number.substring(phone_number.length() - 10));
            }
        }
    }

    private void startSmsRetriever() {

        smsReceiver = new MySMSBroadcastReceiver();
        smsReceiver.setListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().registerReceiver(smsReceiver, intentFilter);

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Successfully started retrieving", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to start retrieving", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onOtpReceived(String otp) {

        smsOtp.setVisibility(View.VISIBLE);
        resendOtp.setVisibility(View.VISIBLE);
        smsOtp.getEditText().setText(otp);

        button_getOtp.setText("Verify");

        if (smsReceiver != null) {
            getActivity().unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("otp", otp);
        Call<ServerResponse> call = webservices.verifyOTP(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse mServerResponse = response.body();
                if (mServerResponse != null) {
                    if (!mServerResponse.error &&
                            mServerResponse.message.equals("Successfully verified phone number")) {

                        Gson gson = new Gson();

                        SharedPrefs.setBooleanParams(getContext(), PHONE_VERIFIED, true);
                        final NavController navController = Navigation.findNavController(getView());
                        user.setPhone(inputPhone);

                        SharedPrefs.setStringParams(getContext(), USER, gson.toJson(user));

                        PhoneAuthFragmentDirections.ActionPhoneAuthFragmentToUserDetailsFragment action
                                = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToUserDetailsFragment();
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

    private boolean validatePhoneNumber() {
        inputPhone = phoneNumber.getEditText().getText().toString().trim();
        if (inputPhone.isEmpty()) {
            phoneNumber.setError("* Please type your mobile number");
            return false;
        } else {
            phoneNumber.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_getOtp:
                if (!validatePhoneNumber())
                    return;
                requestOTP();
        }
    }

    private void requestOTP() {

        if (!authToken.isEmpty()) {
            Call<ServerResponse> call = webservices.requestOTP(authToken, inputPhone, hash);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {

                }
            });
        }

    }

}
