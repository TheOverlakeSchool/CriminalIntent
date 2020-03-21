package org.overlake.mat803.criminalintent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.crime_list_title);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}
