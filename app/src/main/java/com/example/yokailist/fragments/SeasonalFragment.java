package com.example.yokailist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yokailist.R;
import com.example.yokailist.adapters.SeasonalPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SeasonalFragment extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seasonal, container, false);

        viewPager2 = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        SeasonalPagerAdapter adapter = new SeasonalPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if (position == 0) tab.setText("This Season");
                    else tab.setText("Next");
                }).attach();

        return view;
    }
}
