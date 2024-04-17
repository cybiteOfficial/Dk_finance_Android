package drawable;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class LoanFormActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Spinner fragmentSpinner;
    private Button prevButton, nextButton;
    private LoanFormPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_form);

        viewPager = findViewById(R.id.viewPager);
        fragmentSpinner = findViewById(R.id.fragmentSpinner);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        // Initialize ViewPager adapter
        pagerAdapter = new LoanFormPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new Fragment1(), "Fragment 1");
        pagerAdapter.addFragment(new Fragment2(), "Fragment 2");
        viewPager.setAdapter(pagerAdapter);

        // Initialize Spinner with fragment names
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.add("Fragment 1");
        spinnerAdapter.add("Fragment 2");
        fragmentSpinner.setAdapter(spinnerAdapter);

        // Set spinner item selection listener
        fragmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position); // Set ViewPager to the selected fragment
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set ViewPager page change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Not needed
            }

            @Override
            public void onPageSelected(int position) {
                fragmentSpinner.setSelection(position); // Update the spinner selection based on ViewPager position
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Not needed
            }
        });

        // Set Previous button click listener
        prevButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            } else {
                Toast.makeText(LoanFormActivity.this, "Already at the first fragment", Toast.LENGTH_SHORT).show();
            }
        });

        // Set Next button click listener
        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pagerAdapter.getCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                Toast.makeText(LoanFormActivity.this, "Already at the last fragment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class LoanFormPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public LoanFormPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
