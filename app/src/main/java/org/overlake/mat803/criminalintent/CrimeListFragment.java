package org.overlake.mat803.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private CrimeLab mCrimeLab;
    private OnCrimeSelectedListener mCrimeSelectedListener;

    public void setCrimeSelectedListener(OnCrimeSelectedListener crimeSelectedListener) {
        mCrimeSelectedListener = crimeSelectedListener;
    }

    interface OnCrimeSelectedListener {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCrimeLab = CrimeLab.get(getActivity());
    }

    public void updateUI() {
        List<Crime> crimes = mCrimeLab.getCrimes();
        mAdapter.setCrimes(crimes);
        mAdapter.notifyDataSetChanged();
        int crimeCount = crimes.size();
        String subtitle = getString(R.string.crime_count, crimeCount);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                mCrimeLab.addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        
        List<Crime> crimes = mCrimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(View view) {
            super(view);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            mCrimeSelectedListener.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;
        private LayoutInflater mLayoutInflater;

        public CrimeAdapter(List<Crime> crimes){
            mLayoutInflater = LayoutInflater.from(getActivity());
            mCrimes = crimes;
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.list_item_crime, parent,false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
