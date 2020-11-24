package com.bphc.oops_project.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.bphc.oops_project.R;
import com.bphc.oops_project.fragments.dashboard.ToDoFragment;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.NotificationService;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;

public class AddTaskFragment extends Fragment implements View.OnClickListener {

    private TextInputLayout taskTitle, taskDescription;
    private Button pickDate, pickTime;
    private String inputTitle, inputDescription, authToken;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.toolbar_add_task);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        taskTitle = view.findViewById(R.id.add_task_title);
        taskDescription = view.findViewById(R.id.add_task_description);

        pickDate = view.findViewById(R.id.date_picker);
        pickDate.setOnClickListener(this);

        pickTime = view.findViewById(R.id.time_picker);
        pickTime.setOnClickListener(this);

        authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");

        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            String m, d;
            if (month < 9) {
                m = "0" + (month + 1);
            } else {
                m = "" + (month + 1);
            }
            if (dayOfMonth < 10) {
                d = "0" + dayOfMonth;
            } else {
                d = "" + dayOfMonth;
            }

            pickDate.setText(year + "-" + m + "-" + d);
        };

        mTimeSetListener = (view12, hourOfDay, minute) -> {
            String h, m;
            if (hourOfDay < 10) {
                h = "0" + hourOfDay;
            } else {
                h = "" + hourOfDay;
            }
            if (minute < 10) {
                m = "0" + minute;
            } else {
                m = "" + minute;
            }
            pickTime.setText(h + ":" + m + ":00");
        };

        return view;
    }

    @Override
    public void onClick(View v) {

        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        switch (v.getId()) {
            case R.id.date_picker:
                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.time_picker:
                TimePickerDialog dialog1 = new TimePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mTimeSetListener,
                        hour, minutes, true);
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.show();
                break;
        }
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
                toDos();
                return true;
            case R.id.update:
                if (!validateTitle() | !validateDescription())
                    return false;
                if (pickDate.getText().toString().equalsIgnoreCase("Pick A Date")) {
                    Toast.makeText(getContext(), "Add a suitable date", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (pickTime.getText().toString().equalsIgnoreCase("Pick Time")) {
                    Toast.makeText(getContext(), "Add time", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String tasktime = pickDate.getText().toString() + " " + pickTime.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                long millis;
                try {
                    Date date = sdf.parse(tasktime);
                    millis = date.getTime();
                    addTask(tasktime, millis);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return onOptionsItemSelected(item);
    }

    private void addTask(String tasktime, long millis) {

        HashMap<String, String> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("title", inputTitle);
        map.put("description", inputDescription);
        map.put("tasktime", tasktime);
        Retrofit retrofit = APIClient.getRetrofitInstance();
        Webservices webservices = retrofit.create(Webservices.class);

        Call<ServerResponse> call = webservices.createTask(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                long delay = millis - Calendar.getInstance().getTimeInMillis() - 20000;
                scheduleNotification(delay);
                toDos();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private boolean validateTitle() {
        inputTitle = taskTitle.getEditText().getText().toString().trim();
        if (inputTitle.isEmpty()) {
            taskTitle.setError("* Please give a title");
            return false;
        } else {
            taskTitle.setError(null);
            return true;
        }
    }

    private boolean validateDescription() {
        inputDescription = taskDescription.getEditText().getText().toString().trim();
        if (inputDescription.isEmpty()) {
            taskDescription.setError("* Please enter description");
            return false;
        } else {
            taskDescription.setError(null);
            return true;
        }
    }

    private void scheduleNotification(long delay) {
        Intent intent = new Intent(getContext(), NotificationService.class);
        intent.putExtra("title", inputTitle);
        intent.putExtra("description", inputDescription);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                (int) (Math.random()*100000), intent, 0);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void toDos() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ToDoFragment())
                    .commit();
        }
    }
}
