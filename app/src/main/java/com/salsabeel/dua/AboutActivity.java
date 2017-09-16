package com.salsabeel.dua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.salsabeel.dua.utils.ContextWrapper;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AboutActivity";

    SharedPreferences sharedPreferences;
    private String currentLang;

    private ImageView mFacebook;
    private ImageView mTwitter;
    private ImageView mYoutube;
    private Intent intent;
    private TextView mCopyRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getResources().getString(R.string.about));

        mCopyRight = (TextView) findViewById(R.id.txt_copy_name);
        mFacebook = (ImageView) findViewById(R.id.image_facebook);
        mTwitter = (ImageView) findViewById(R.id.image_twitter);
        mYoutube = (ImageView)findViewById(R.id.image_youtube);

        mCopyRight.setOnClickListener(this);
        mFacebook.setOnClickListener(this);
        mTwitter.setOnClickListener(this);
        mYoutube.setOnClickListener(this);

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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_facebook:
                this.intent = new Intent("android.intent.action.VIEW");
                this.intent.setData(Uri.parse("https://www.facebook.com/Darul-Huda-Publication-134202593299656/"));
                startActivity(this.intent);
                break;
            case R.id.image_twitter:
                this.intent = new Intent("android.intent.action.VIEW");
                this.intent.setData(Uri.parse("https://twitter.com/darulhudaonline?lang=en"));
                startActivity(this.intent);
                break;
            case R.id.image_youtube:
                this.intent = new Intent("android.intent.action.VIEW");
                this.intent.setData(Uri.parse("https://www.youtube.com/darulhudanet"));
                startActivity(this.intent);
                break;
            case R.id.txt_copy_name:
                this.intent = new Intent("android.intent.action.VIEW");
                this.intent.setData(Uri.parse("http://www.darulhuda.net/"));
                startActivity(this.intent);
                break;
            default:
        }
    }
}
