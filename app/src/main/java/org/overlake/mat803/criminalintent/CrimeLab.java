package org.overlake.mat803.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import org.overlake.mat803.criminalintent.database.CrimeBaseHelper;
import org.overlake.mat803.criminalintent.database.CrimeCursorWrapper;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }

        return  sCrimeLab;
    }
    private CrimeLab(Context context){
        mContext = context;
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes =  new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ? ",
                new String[] { id.toString() }
        );

        try {
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();

        } finally {
            cursor.close();
        }

    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime){
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,
                values,
                CrimeTable.Cols.UUID + " = ? ",
                new String[] { crime.getId().toString() });
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved());
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }

    public File getCrimePhotoFile(Crime crime){
        File crimePhotosDir = new File(mContext.getFilesDir(), "crime_photos");
        if(!crimePhotosDir.exists()) {
            crimePhotosDir.mkdir();
        }
        return new File(crimePhotosDir, "IMG_" + crime.getId() + ".jpg");
    }
}
