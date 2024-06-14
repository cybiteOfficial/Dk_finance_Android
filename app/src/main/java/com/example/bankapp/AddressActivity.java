package com.example.bankapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        // Find views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        submitBtn = findViewById(R.id.submit_button);
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

        // Setup submit button
        setupSubmitButton();

        // Get the co-applicant names passed from CreateCustomer
        List<String> coApplicantNames = getIntent().getStringArrayListExtra("coApplicantNames");
        if (coApplicantNames != null) {
            // Log the received co-applicant names
            for (String name : coApplicantNames) {
                Log.d(TAG, "Received Co-Applicant Name: " + name);
            }
        } else {
            Log.d(TAG, "No co-applicant names received.");
        }
    }

    private void setupHomeButton() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to DashboardActivity
                Intent mainIntent = new Intent(AddressActivity.this, DashboardActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private void setupSubmitButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something when submit button is clicked
                // For example, save data or proceed to the next activity
                // Here, we are passing data back to AddCustomerActivity
                String customerName = getIntent().getStringExtra("customerName");
                if (customerName == null) customerName = "Default Name";  // handle null

                ArrayList<String> coApplicantNames = getIntent().getStringArrayListExtra("coApplicantNames");
                if (coApplicantNames == null) coApplicantNames = new ArrayList<>();  // handle null

                Intent intent = new Intent(AddressActivity.this, AddCustomerActivity.class);
                intent.putExtra("customerName", customerName);
                intent.putStringArrayListExtra("coApplicantNames", coApplicantNames);
                startActivity(intent);
                finish();
            }
        });
    }

    // Implementing DataTransferListener interface method
    @Override
    public void onDataTransfer(CurrentAddressData data) {
        if (permanentFragment != null) {
            permanentFragment.updateData(data);
        }
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
}
