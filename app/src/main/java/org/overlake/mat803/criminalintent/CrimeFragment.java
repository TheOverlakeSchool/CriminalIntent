package org.overlake.mat803.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment implements DatePickerFragment.OnDateUpdateListener {

    private static final String DIALOG_DATE = "dialog_date";
    private static final int REQUEST_CONTACT = 0;
    private static final int REQUEST_PHOTO = 1;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mSuspectButton;
    private CheckBox mSolvedCheckbox;
    private static final String ARG_CRIME_ID = "crime_id";
    private File mCrimePhotoFile;
    private ImageButton mCameraButton;
    private ImageView mCrimePhoto;
    private OnCrimeUpdatedListener mCrimeUpdatedListener;

    public void setCrimeUpdatedListener(OnCrimeUpdatedListener crimeUpdatedListener) {
        mCrimeUpdatedListener = crimeUpdatedListener;
    }

    private void updateCrime(){
        CrimeLab.get(this.getActivity()).updateCrime(mCrime);
        if(mCrimeUpdatedListener != null){
            mCrimeUpdatedListener.onCrimeUpdated(mCrime);
        }

    }

    interface OnCrimeUpdatedListener {
        void onCrimeUpdated(Crime crime);
    }
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        mCrime = crimeLab.getCrime(crimeId);
        mCrimePhotoFile = crimeLab.getCrimePhotoFile(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, 0);
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });


        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setText(mCrime.getSuspect());

        final Intent pickIntent = new Intent();
        pickIntent.setAction(Intent.ACTION_PICK);
        pickIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        PackageManager packageManager = getActivity().getPackageManager();

        if(packageManager.resolveActivity(pickIntent, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        } else {
            mSuspectButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(pickIntent, REQUEST_CONTACT);
                }
            });
        }

        v.findViewById(R.id.crime_report).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.crime_report_subject);
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        mCameraButton = v.findViewById(R.id.crime_camera);
        final Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        final List<ResolveInfo> cameraActivities = packageManager.queryIntentActivities(cameraIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if(mCrimePhotoFile == null || cameraActivities.size() == 0){
            mCameraButton.setEnabled(false);
        } else {
            mCameraButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = getUri();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    for(ResolveInfo activity : cameraActivities){
                        getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    startActivityForResult(cameraIntent, REQUEST_PHOTO);
                }
            });

        }

        mCrimePhoto = v.findViewById(R.id.crime_photo);
       // updatePhotoView();
        Glide.with(getActivity()).load(mCrimePhotoFile).into(mCrimePhoto);
        return v;
    }

    private Uri getUri() {
        return FileProvider.getUriForFile(getActivity(),
                                "org.overlake.mat803.criminalintent.fileprovider", mCrimePhotoFile);
    }

    private void updatePhotoView() {
        mCrimePhoto.setImageBitmap(PictureUtils.getScaledBitmap(mCrimePhotoFile.getPath(), getActivity(), 4));
    }

    @Override
    public void onDateUpdate(Date date) {
        mCrime.setDate(date);
        mDateButton.setText(mCrime.getDate().toString());
        updateCrime();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport(){
        String solved = getString(mCrime.isSolved() ?
                R.string.crime_report_solved : R.string.crime_report_unsolved);

        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report,
                mCrime.getTitle(),
                mCrime.getDate(),
                solved,
                suspect
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] fields = {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(
                    contactUri,
                    fields,
                    null,
                    null,
                    null
            );

            try {
                if(c.getCount() > 0){
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                    updateCrime();
                }
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
            Uri uri = getUri();
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }
}
