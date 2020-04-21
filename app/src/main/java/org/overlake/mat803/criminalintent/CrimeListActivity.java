package org.overlake.mat803.criminalintent;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.OnCrimeSelectedListener, CrimeFragment.OnCrimeUpdatedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.crime_list_title);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null){
            // We have a phone or other small device
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            // We have a tablet or other large device
            Fragment fragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if(fragment instanceof CrimeListFragment){
            ((CrimeListFragment) fragment).setCrimeSelectedListener(this);
        } else if (fragment instanceof CrimeFragment){
            ((CrimeFragment) fragment).setCrimeUpdatedListener(this);
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
    }
}
