package com.example.visibletime.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.example.visibletime.R;
import com.example.visibletime.Ui.Fragement.StatisticsDayFragment;
import com.example.visibletime.Ui.Fragement.StatisticsFragment;
import com.example.visibletime.Ui.Fragement.StatisticsMonthFragment;
import com.example.visibletime.Ui.Fragement.StatisticsWeekFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private final String tag = "로그";
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private StatisticsDayFragment statisticsDayFragment;
    private StatisticsWeekFragment statisticsWeekFragment;
    private StatisticsMonthFragment statisticsMonthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager_Statistics);
        tabLayout = findViewById(R.id.tabs_Statistics);

        statisticsDayFragment = new StatisticsDayFragment();
        statisticsWeekFragment = new StatisticsWeekFragment();
        statisticsMonthFragment = new StatisticsMonthFragment();


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
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
