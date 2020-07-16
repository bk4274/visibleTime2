package com.example.visibletime.Ui.Fragement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.visibletime.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private final String tag = "로그";

    View root;
    Context context;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private StatisticsDayFragment statisticsDayFragment;
    private StatisticsWeekFragment statisticsWeekFragment;
    private StatisticsMonthFragment statisticsMonthFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(tag,"StatisticsFragment - onCreateView() |  ");
        root = inflater.inflate(R.layout.fragment_statistics, container, false);        // Inflate the layout for this fragment
        this.context = container.getContext();
        initView();
        return root;
    }

    private void initView() {
        viewPager = root.findViewById(R.id.viewPager_Statistics);
        tabLayout = root.findViewById(R.id.tabs_Statistics);

        statisticsDayFragment = new StatisticsDayFragment();
        statisticsWeekFragment = new StatisticsWeekFragment();
        statisticsMonthFragment = new StatisticsMonthFragment();


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(statisticsDayFragment, "하루 통계");
        viewPagerAdapter.addFragment(statisticsWeekFragment, "주간 통계");
        viewPagerAdapter.addFragment(statisticsMonthFragment, "월간 통계");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_playlist);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_bookmark);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_heart);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.d(tag,"ViewPagerAdapter - getItem() |  ");
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            Log.d(tag,"ViewPagerAdapter - getCount() |  ");
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(tag,"ViewPagerAdapter - getPageTitle() |  ");
            return fragmentTitle.get(position);
        }
    }
}