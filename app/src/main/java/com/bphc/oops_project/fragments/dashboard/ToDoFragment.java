package com.bphc.oops_project.fragments.dashboard;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bphc.oops_project.R;
import com.bphc.oops_project.adapters.ToDoAdapter;
import com.bphc.oops_project.fragments.AddItemFragment;
import com.bphc.oops_project.fragments.AddTaskFragment;
import com.bphc.oops_project.helper.APIClient;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.helper.Webservices;
import com.bphc.oops_project.models.Result;
import com.bphc.oops_project.models.ServerResponse;
import com.bphc.oops_project.models.ToDo;
import com.bphc.oops_project.prefs.SharedPrefs;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.bphc.oops_project.prefs.SharedPrefsConstants.JWTS_TOKEN;

public class ToDoFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    private ToDoAdapter adapter;
    private String authToken;
    private ArrayList<ToDo> toDos = new ArrayList<>();
    private Webservices webservices;
    private ImageView emptyTodoImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Retrofit retrofit = APIClient.getRetrofitInstance();
        webservices = retrofit.create(Webservices.class);
        authToken = SharedPrefs.getStringParams(getContext(), JWTS_TOKEN, "");
        getUserToDos();
    }

    private void getUserToDos() {
        Call<ServerResponse> call = webservices.getUserToDos(authToken);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse mServerResponse = response.body();
                if (mServerResponse != null) {
                    if (!mServerResponse.getError()) {
                        if (!mServerResponse.getResult().getToDos().isEmpty()) {
                            emptyTodoImage.setVisibility(View.GONE);
                            toDos.addAll(mServerResponse.getResult().getToDos());
                            adapter.notifyDataSetChanged();
                        } else {
                            emptyTodoImage.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_todo);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ImageView addTaskImage = view.findViewById(R.id.task_add_image);
        addTaskImage.setOnClickListener(this);

        emptyTodoImage = view.findViewById(R.id.empty_todo_image);

        RecyclerView recyclerView = view.findViewById(R.id.parent_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new ToDoAdapter(toDos);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_add_image:
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new AddTaskFragment())
                            .commit();
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position, int id) {
        switch (id) {
            case R.id.delete_task_image:
                deleteTask(position);
        }
    }

    @Override
    public void onItemClick(int positionItem, int positionParent, int id) {

    }

    private void deleteTask(int position) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("taskId", toDos.get(position).getTaskId());
        Call<ServerResponse> call = webservices.deleteTask(map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (!response.body().getError()) {
                    toDos.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (toDos.isEmpty()) emptyTodoImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }
}
