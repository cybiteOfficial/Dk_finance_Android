package com.example.bankapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private Button submitBtn;
    private static final String TAG = "AddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Button btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Set up ViewPager with adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);

        // Apply text appearance to the TabLayout
        tabLayout.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.black));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#7A306D"));


        submitBtn = findViewById(R.id.submit_button);

        submitBtn.setOnClickListener(v -> {
            // Get the customer name from the intent
            String customerName = getIntent().getStringExtra("customerName");
            // Get the list of co-applicant names passed from CreateCustomer
            ArrayList<String> coApplicantNames = getIntent().getStringArrayListExtra("coApplicantNames");

            // Create an Intent to pass back the customer name and the list of co-applicant names
            Intent intent = new Intent(AddressActivity.this, AddCustomerActivity.class);
            intent.putExtra("customerName", customerName);
            intent.putStringArrayListExtra("coApplicantNames", coApplicantNames);
            startActivity(intent);
            finish();
        });



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
                    return new CurrentFragment();
                case 1:
                    return new PermanentFragment();
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

