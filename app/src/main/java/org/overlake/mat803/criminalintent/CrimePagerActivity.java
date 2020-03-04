package org.overlake.mat803.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "org.overlake.mat803.criminalintent.crime_id";

    public static Intent newIntent(Context context, UUID crimeId){
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        ViewPager vp = findViewById(R.id.crime_view_pager);
        FragmentManager fm = getSupportFragmentManager();

        CrimeLab crimeLab = CrimeLab.get(CrimePagerActivity.this);
        final List<Crime> crimes = crimeLab.getCrimes();

        vp.setAdapter(new FragmentStatePagerAdapter(fm) {

            @Override
            public Fragment getItem(int position) {
                UUID crimeId = crimes.get(position).getId();
                return CrimeFragment.newInstance(crimeId);
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

        for(int i = 0; i < crimes.size(); i++){
            if(crimes.get(i).getId().equals(crimeId)){
                vp.setCurrentItem(i);
                break;
            }
        }

    }
}
