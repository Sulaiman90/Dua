package com.salsabeel.dua;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.salsabeel.dua.database.TaskContract.TaskEntry;
import com.salsabeel.dua.model.Dua;
import com.salsabeel.dua.adapter.*;
import com.salsabeel.dua.database.*;
import com.salsabeel.dua.utils.ContextWrapper;
import com.salsabeel.dua.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DuaDetailActivity extends AppCompatActivity {

    private static final String TAG = "DuaDetailActivity";
    SharedPreferences sharedPreferences;
    private int categoryNo;
    private ArrayList<Dua> results = new ArrayList<>();
    private String currentLang;

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dua_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.duaDetailListView);

        final ExternalDbOpenHelper helper = ExternalDbOpenHelper.getInstance(this);

        Bundle extras = getIntent().getExtras();
        categoryNo = extras.getInt("categoryNo");

       // Log.d(TAG,"categoryNo "+categoryNo);
        if(categoryNo==0){
            setTitle(getResources().getString(R.string.quran_dua));
        }
        else if(categoryNo==1){
            setTitle(getResources().getString(R.string.hadith_dua));
        }
        retrieveTasks(categoryNo);

        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        currentLang = sharedPreferences.getString("pref_lang_key","ta");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = listView.onSaveInstanceState();
        outState.putParcelable(LIST_STATE, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListState = savedInstanceState.getParcelable(LIST_STATE);
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
     //   Log.d(TAG,"resumed dua detail "+categoryNo +"currentLang "+currentLang);
        String oldLanguage = currentLang;
        currentLang = sharedPreferences.getString("pref_lang_key","ta");
        if (!oldLanguage.equals(currentLang)){
            recreate();
        }
        retrieveTasks(categoryNo);
        if (mListState != null)
            listView.onRestoreInstanceState(mListState);
        mListState = null;
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
        getMenuInflater().inflate(R.menu.menu_bookmarks,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      //  Log.d(TAG,"categoryNo "+categoryNo);
        if(item.getItemId()==R.id.action_favourite){
            Intent intent = new Intent(this, BookMarksActivity.class);
            intent.putExtra("dua_category_no",categoryNo);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Log.d(TAG,"onActivityResult");
        if(requestCode == Utilities.REQUEST_CODE_MY_PICK) {
            Utilities.shareDua(data);
        }
    }

    public void retrieveTasks(int category) {
        Cursor cursor;
        ExternalDbOpenHelper myDbHelper = new ExternalDbOpenHelper(DuaDetailActivity.this);
        myDbHelper.createDataBase();
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
       // Toast.makeText(DuaDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
        cursor = myDbHelper.query(category);

        results.clear();

       // Log.d(TAG,"totalDuas "+cursor.getCount());
        int duaNo = 0;

        if(cursor!=null){
            if(cursor.getCount()!=0 && cursor.moveToFirst()){
                do{
                    duaNo++;
                    int id = cursor.getInt(cursor.getColumnIndex(TaskEntry.DUA_ID));
                    String arabicDua = cursor.getString(cursor.getColumnIndex(TaskEntry.ARABIC_DUA));
                    String arabicRef =  cursor.getString(cursor.getColumnIndex(TaskEntry.ARABIC_REFERENCE));
                    String tamilTranslation =  cursor.getString(cursor.getColumnIndex(TaskEntry.TAMIL_TRANSLATION));
                    String tamilRef =  cursor.getString(cursor.getColumnIndex(TaskEntry.TAMIL_REFERENCE));
                    boolean fav = cursor.getInt(cursor.getColumnIndex(TaskEntry.FAV)) == 1;
                    results.add(new Dua(id, duaNo, arabicDua, arabicRef, tamilTranslation, tamilRef, fav));
                   // Log.d(TAG,id + arabicDua + englishTranslation+ tamilTranslation+ englishRef+ tamilRef+ fav);
                    //Log.d(TAG,englishTranslation+ tamilTranslation);
                }
                while (cursor.moveToNext());
            }
        }
        cursor.close();
        setDuaAdapter(results);
    }

    private void setDuaAdapter(ArrayList<Dua> results){
        DuaDetailAdapter adapter = new DuaDetailAdapter(DuaDetailActivity.this,results);
        listView.setAdapter(adapter);
    }
}