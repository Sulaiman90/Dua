package com.salsabeel.dua.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.salsabeel.dua.R;

import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class FetchPrefData {

    private Context mContext;
    private SharedPreferences sharedPreferences;

    private SharedPreferences sharedPrefs;

    public FetchPrefData(Context context){
        mContext = context;
        sharedPreferences = getDefaultSharedPreferences(context);
        sharedPrefs = mContext.getSharedPreferences(context.getResources().getString(R.string.PREF_FILE_SETTINGS),
                Context.MODE_PRIVATE);

    }

    public Boolean isArabicAndTamilView() {
        return sharedPrefs.getBoolean(mContext.getResources().getString(R.string.PREF_toViewBoth), true);
    }

    public Boolean isArabicViewOnly() {
        return sharedPrefs.getBoolean(mContext.getResources().getString(R.string.PREF_toViewArabic), false);
    }

    public Boolean isTamilViewOnly() {
        return sharedPrefs.getBoolean(mContext.getResources().getString(R.string.PREF_toViewTamil), false);
    }

    public int getArabicFontSize() {
        return sharedPreferences.getInt(
                mContext.getResources().getString(R.string.pref_font_arabic_size_key),
                mContext.getResources().getInteger(R.integer.pref_font_arabic_size_default));
    }

    public int getTranslationAndRefFontSize() {
        return sharedPreferences.getInt(
                mContext.getResources().getString(R.string.pref_font_other_size_key),
                mContext.getResources().getInteger(R.integer.pref_font_other_size_default));
    }

}
