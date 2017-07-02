package com.salsabeel.dua;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.salsabeel.dua.utils.ContextWrapper;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    SharedPreferences sharedPreferences;
    private String currentLang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getResources().getString(R.string.about));


        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        currentLang = sharedPreferences.getString("pref_lang_key","ta");
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        //Log.d(TAG,"resumed dua detail currentLang "+currentLang);
        String oldLanguage = currentLang;
        currentLang = sharedPreferences.getString("pref_lang_key","ta");
        if (!oldLanguage.equals(currentLang)){
            recreate();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(newBase);
        String langPref = sharedPreferences.getString("pref_lang_key","ta");
        Locale newLocale;
   /*     Log.d(TAG,"langPref: "+langPref);*/
        newLocale = new Locale(langPref);
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }
}
