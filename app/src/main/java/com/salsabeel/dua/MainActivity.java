package com.salsabeel.dua;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.salsabeel.dua.utils.ContextWrapper;
import com.salsabeel.dua.utils.FetchPrefData;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static String[] categories = { "Quran Dua", "Hadith Dua","Settings"};
    public Boolean toViewBoth;
    public Boolean toViewArabic;
    public Boolean toViewTamil;
    ListView listView;
    private Button tvQuran;
    private Button tvHadith;

    SharedPreferences sharedPreferences;
    private String currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.app_name));

        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        currentLang = sharedPreferences.getString("pref_lang_key","ta");

        tvQuran = (Button) findViewById(R.id.quranDua);
        tvQuran.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemClick(0);
            }
        });

        tvHadith = (Button) findViewById(R.id.hadithDua);
        tvHadith.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemClick(1);
            }
        });

        checkPrefToShowRadioGroup();

    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        Log.d(TAG,"resumed dua detail currentLang "+currentLang);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if(item.getItemId()==R.id.action_settings){
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public void listItemClick(int itemSelected) {

        Intent intent = new Intent(this, DuaDetailActivity.class);
        Bundle bundle = new Bundle();
        if (itemSelected == 0) {
            bundle.putInt("categoryNo",0);
        } else if (itemSelected == 1) {
            bundle.putInt("categoryNo",1);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void checkPrefToShowRadioGroup() {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE_SETTINGS), Context.MODE_PRIVATE);

        RadioButton rbBoth = (RadioButton) findViewById(R.id.rbArabicAndTamil);
        RadioButton rbArabic = (RadioButton) findViewById(R.id.rbArabic);
        RadioButton rbTamil = (RadioButton) findViewById(R.id.rbTamil);

        Boolean b1 = sharedPreferences.getBoolean(getString(R.string.PREF_toViewBoth), true);
        Boolean b2 = sharedPreferences.getBoolean(getString(R.string.PREF_toViewArabic), false);
        Boolean b3 = sharedPreferences.getBoolean(getString(R.string.PREF_toViewTamil), false);

        rbBoth.setChecked(b1);
        rbArabic.setChecked(b2);
        rbTamil.setChecked(b3);
    }

    public void changeDuaView(View view) {

        switch (view.getId()) {
            case R.id.rbArabicAndTamil:
                //Toast.makeText(getBaseContext(), "Both Arabic and Tamil", Toast.LENGTH_SHORT).show();
                this.toViewBoth = true;
                this.toViewArabic = false;
                this.toViewTamil = false;
                break;
            case R.id.rbArabic:
               // Toast.makeText(getBaseContext(), "Only Arabic ", Toast.LENGTH_SHORT).show();
                this.toViewBoth = false;
                this.toViewArabic = true;
                this.toViewTamil = false;
                break;
            case R.id.rbTamil:
               // Toast.makeText(getBaseContext(), "Only Tamil", Toast.LENGTH_SHORT).show();
                this.toViewBoth = false;
                this.toViewArabic = false;
                this.toViewTamil = true;
                break;
        }
        saveRadioToShow();
    }

    public void saveRadioToShow() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE_SETTINGS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(getString(R.string.PREF_toViewBoth), this.toViewBoth);
        editor.putBoolean(getString(R.string.PREF_toViewArabic), this.toViewArabic);
        editor.putBoolean(getString(R.string.PREF_toViewTamil), this.toViewTamil);

       // editor.commit();
        editor.apply();
        Log.d(TAG, "saved");
    }
}
