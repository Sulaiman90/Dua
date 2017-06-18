package com.salsabeel.dua;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.salsabeel.dua.adapter.BookmarksDuaAdapter;
import com.salsabeel.dua.adapter.DuaDetailAdapter;
import com.salsabeel.dua.database.ExternalDbOpenHelper;
import com.salsabeel.dua.database.TaskContract.TaskEntry;
import com.salsabeel.dua.model.Dua;
import com.salsabeel.dua.utils.ContextWrapper;
import com.salsabeel.dua.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.id.message;

public class BookMarksActivity extends AppCompatActivity {

    private static final String TAG = "BookMarksActivity";
    private RecyclerView recyclerView;
    private BookmarksDuaAdapter mAdapter;
    private ArrayList<Dua> results = new ArrayList<>();
    private int categoryNo = 0;

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        categoryNo = intent.getIntExtra("dua_category_no",0);
        if(categoryNo == 0){
            setTitle(getResources().getString(R.string.quran_dua_favourites));
        }
        else{
            setTitle(getResources().getString(R.string.hadith_dua_favourites));
        }

        Log.d(TAG,"categoryNo "+categoryNo);

        recyclerView = (RecyclerView) findViewById(R.id.rVFavourites);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        results = getBookmarkedDuas();

        setBookmarksDuaAdapter(results);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LIST_STATE, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListState = savedInstanceState.getParcelable(LIST_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }
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
       // getMenuInflater().inflate(R.menu.menu_bookmarks,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Utilities.REQUEST_CODE_MY_PICK) {
            Utilities.shareDua(data);
        }
    }

    private ArrayList<Dua> getBookmarkedDuas(){

      final ExternalDbOpenHelper mDbHelper  = ExternalDbOpenHelper.getInstance(this);

        results.clear();
        Cursor cursor = null;
        int duaNo = 0;
        try {
            cursor = mDbHelper.queryBookmarkedDuas(categoryNo);
            results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    duaNo++;
                    int id = cursor.getInt(cursor.getColumnIndex(TaskEntry.DUA_ID));
                    String arabicDua = cursor.getString(cursor.getColumnIndex(TaskEntry.ARABIC_DUA));
                    String arabicRef =  cursor.getString(cursor.getColumnIndex(TaskEntry.ARABIC_REFERENCE));
                    String tamilTranslation =  cursor.getString(cursor.getColumnIndex(TaskEntry.TAMIL_TRANSLATION));
                    String tamilRef =  cursor.getString(cursor.getColumnIndex(TaskEntry.TAMIL_REFERENCE));
                    boolean fav = cursor.getInt(cursor.getColumnIndex(TaskEntry.FAV)) == 1;
                    results.add(new Dua(id, duaNo, arabicDua, arabicRef, tamilTranslation,tamilRef,fav));
                   // Log.d(TAG,""+id);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return results;
    }

    private void setBookmarksDuaAdapter(List results){
        mAdapter = new BookmarksDuaAdapter(this,results);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }


}
