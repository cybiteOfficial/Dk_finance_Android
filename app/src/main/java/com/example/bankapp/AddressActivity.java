package com.example.bankapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements DataTransferListener {

    private Button submitBtn;
    private ImageView homeButton;
    private static final String TAG = "AddressActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private CurrentFragment currentFragment;
    private PermanentFragment permanentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // get application id from intent
        Intent i = getIntent();
        String application_id = i.getStringExtra("application_id");

        // pass this to the fragments
        Bundle bundle = new Bundle();
        bundle.putString("application_id", application_id);

        // Find views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        homeButton = findViewById(R.id.homeButton);

        // Set up ViewPager with adapter
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Apply text appearance to the TabLayout
        tabLayout.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.black));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#7A306D"));

        // Setup home button
        setupHomeButton();

    }

    private void setupHomeButton() {
        homeButton.setOnClickListener(v -> {
            // Move to DashboardActivity
            Intent mainIntent = new Intent(AddressActivity.this, DashboardActivity.class);
            startActivity(mainIntent);
            finish();
        });
    }

    // Implementing DataTransferListener interface method
    @Override
    public void onDataTransfer(CurrentAddressData currentAddressData) {
        permanentFragment.updateData(currentAddressData);
    }

    @Override
    public void switchFragment() {
        // move to permanent fragment
        viewPager.setCurrentItem(1);
    }

    // Adapter for ViewPager
    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final String[] titles = {"Current", "Permanent"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    currentFragment = new CurrentFragment();
                    // Set the listener for data transfer
                    currentFragment.setDataTransferListener(AddressActivity.this);
                    return currentFragment;
                case 1:
                    permanentFragment = new PermanentFragment();
                    return permanentFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public void switchToPermanentTab() {
        viewPager.setCurrentItem(1);
    }
}
