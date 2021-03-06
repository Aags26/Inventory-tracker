package com.bphc.oops_project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bphc.oops_project.R;
import com.bphc.oops_project.fragments.dashboard.EssentialsFragment;
import com.bphc.oops_project.fragments.dashboard.ProfileFragment;
import com.bphc.oops_project.fragments.dashboard.ToDoFragment;
import com.bphc.oops_project.helper.OnItemClickListener;
import com.bphc.oops_project.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static User user = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = DashboardFragmentArgs.fromBundle(getArguments()).getUser();
        }
        loadFragment(new EssentialsFragment());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);

        return view;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedFragment = null;

        switch (item.getItemId()) {

            case R.id.essentials:
                selectedFragment = new EssentialsFragment();
                break;
            case R.id.todo_list:
                selectedFragment = new ToDoFragment();
                break;
            case R.id.profile:
                selectedFragment = new ProfileFragment();
        }

        loadFragment(selectedFragment);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
