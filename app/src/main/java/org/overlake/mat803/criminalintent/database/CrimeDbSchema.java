package org.overlake.mat803.criminalintent.database;

public class CrimeDbSchema {

    private CrimeDbSchema(){}

    public static final String DATABASE_NAME = "crimes.db";
    public static final int VERSION = 1;

    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
