package com.example.yokailist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yokailist.R;
import com.example.yokailist.adapters.MyListAdapter;
import com.example.yokailist.data.db.AnimeStatusHelper;
import com.example.yokailist.data.db.MappingHelper;
import com.example.yokailist.data.models.AnimeStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyListFragment extends Fragment {
    private MyListAdapter adapter;
    private AnimeStatusHelper animeStatusHelper;
    private List<AnimeStatus> allAnimeList = new ArrayList<>();
    private List<AnimeStatus> filteredAnimeList = new ArrayList<>();
    private RecyclerView rvMyList;
    private TextView tvNoResult;
    private ProgressBar progressBar;
    private LinearLayout llNoResult;
    private ImageButton ibFilter;
    private int currentFilter = 0;

    public MyListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);

        rvMyList = view.findViewById(R.id.rv_anime);
        progressBar = view.findViewById(R.id.progressBar);
        ibFilter = view.findViewById(R.id.ib_filter);
        llNoResult = view.findViewById(R.id.ll_noResult);
        tvNoResult = view.findViewById(R.id.tv_noResult);

        filteredAnimeList = new ArrayList<>();
        adapter = new MyListAdapter(getContext(), filteredAnimeList);
        rvMyList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyList.setAdapter(adapter);

        ibFilter.setOnClickListener(v -> showFilterMenu());

        loadAnimeData();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAnimeData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAnimeData();
    }

    private void loadAnimeData() {
        progressBar.setVisibility(View.VISIBLE);
        rvMyList.setVisibility(View.GONE);
        llNoResult.setVisibility(View.GONE);

        if (animeStatusHelper == null) {
            animeStatusHelper = AnimeStatusHelper.getInstance(getContext());
        }
        animeStatusHelper.open();

        allAnimeList = MappingHelper.mapCursorToArrayList(animeStatusHelper.queryAll());

        if (allAnimeList != null && !allAnimeList.isEmpty()) {
            applyFilter(currentFilter);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvMyList.setVisibility(View.GONE);
            llNoResult.setVisibility(View.VISIBLE);

            tvNoResult.setText("No anime in your list yet");

            filteredAnimeList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    private void applyFilter(int statusFilter) {
        currentFilter = statusFilter;

        if (statusFilter == 0) {
            filteredAnimeList = new ArrayList<>(allAnimeList);
        } else {
            filteredAnimeList = allAnimeList.stream()
                    .filter(anime -> anime.getStatus() == statusFilter)
                    .collect(Collectors.toList());
        }

        adapter = new MyListAdapter(getContext(), filteredAnimeList);
        rvMyList.setAdapter(adapter);

        if (filteredAnimeList.isEmpty()) {
            rvMyList.setVisibility(View.GONE);
            llNoResult.setVisibility(View.VISIBLE);

            tvNoResult.setText("No Result");

        } else {
            rvMyList.setVisibility(View.VISIBLE);
            llNoResult.setVisibility(View.GONE);
        }
    }

    private void showFilterMenu() {
        PopupMenu popup = new PopupMenu(requireContext(), ibFilter);
        popup.getMenuInflater().inflate(R.menu.status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int statusFilter = 0;

            if (item.getItemId() == R.id.all) {
                statusFilter = 0;
            } else if (item.getItemId() == R.id.sort_by_watching) {
                statusFilter = 1;
            } else if (item.getItemId() == R.id.sort_by_Completed) {
                statusFilter = 2;
            } else if (item.getItemId() == R.id.sort_by_OnHold) {
                statusFilter = 3;
            } else if (item.getItemId() == R.id.sort_by_Dropped) {
                statusFilter = 4;
            } else if (item.getItemId() == R.id.sort_by_PlanToWatch) {
                statusFilter = 5;
            }

            applyFilter(statusFilter);
            return true;
        });
        popup.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (animeStatusHelper != null) {
            animeStatusHelper.close();
        }
    }
}