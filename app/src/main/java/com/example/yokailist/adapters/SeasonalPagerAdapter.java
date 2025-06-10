package com.example.yokailist.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.yokailist.fragments.ThisSeasonFragment;
import com.example.yokailist.fragments.UpcomingSeasonFragment;

public class SeasonalPagerAdapter extends FragmentStateAdapter {
    public SeasonalPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ThisSeasonFragment();
        } else {
            return new UpcomingSeasonFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
