package com.salsabeel.dua.database;

import android.provider.BaseColumns;


public class TaskContract {

    public static final String DATABASE_NAME = "quranhadithdua.sqlite";
    public static final int DATABASE_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "dua";

        public static final String DUA_ID = "_id";
        public static final String ARABIC_DUA = "ar_dua";
        public static final String ENGLISH_TRANSLATION = "en_translation";
        public static final String TAMIL_TRANSLATION = "ta_translation";
        public static final String ENGLISH_REFERENCE = "en_reference";
        public static final String TAMIL_REFERENCE = "ta_reference";
        public static final String GROUP_ID ="group_id";
        public static final String FAV = "fav";
    }
}
