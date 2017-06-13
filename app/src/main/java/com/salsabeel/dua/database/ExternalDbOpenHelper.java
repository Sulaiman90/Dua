package com.salsabeel.dua.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.salsabeel.dua.database.TaskContract.TaskEntry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "ExternalDbOpenHelper";
    //Database file name
    private static final String DB_NAME = TaskContract.DATABASE_NAME;
    private static final int DB_VERSION = TaskContract.DATABASE_VERSION;
    //Path to the device folder with databases
    private static String DB_PATH;
    private static ExternalDbOpenHelper sInstance;
    public Context context;
    private SQLiteDatabase database;

    private ExternalDbOpenHelper(Context context, String databaseName) {
        super(context, databaseName, null, 1);
        this.context = context;
        //Write a full path to the databases of your application
        String packageName = context.getPackageName();
        DB_PATH = String.format("//data//data//%s//databases//", packageName);
        Log.d(TAG,"openDataBase called");
        openDataBase();
    }

    public ExternalDbOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static ExternalDbOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ExternalDbOpenHelper(
                    context.getApplicationContext(), DB_NAME);
        }
        return sInstance;
    }

    public SQLiteDatabase getDb() {
        return database;
    }

    //This piece of code will create a com.salsabeel.dua.database if it’s not yet created
    public void createDataBase() {
        Log.d(TAG, "createDataBase called");
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.d(TAG, "Copying error");
                throw new Error("Error copying com.salsabeel.dua.database!");
            }
        } else {
            Log.d(TAG, "Database already exists");
        }
    }

    //Performing a com.salsabeel.dua.database existence check
    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = DB_PATH + DB_NAME;
            checkDb = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.d(this.getClass().toString(), "Error while checking db");
        }
        //Android doesn’t like resource leaks, everything should
        // be closed
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }

    //Method for copying the com.salsabeel.dua.database
    private void copyDataBase() throws IOException {
        //Open a stream for reading from our ready-made com.salsabeel.dua.database
        //The stream source is located in the assets
        InputStream externalDbStream = context.getAssets().open(DB_NAME);

        //Path to the created empty com.salsabeel.dua.database on your Android device
        String outFileName = DB_PATH + DB_NAME;

        //Now create a stream for writing the com.salsabeel.dua.database byte by byte
        OutputStream localDbStream = new FileOutputStream(outFileName);

        //Copying the com.salsabeel.dua.database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
        //Don’t forget to close the streams
        localDbStream.close();
        externalDbStream.close();
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public Cursor query(int category){
        String[] columns = new String[] { TaskEntry.DUA_ID, TaskEntry.ARABIC_DUA, TaskEntry.ENGLISH_TRANSLATION,
                TaskEntry.TAMIL_TRANSLATION,TaskEntry.ENGLISH_REFERENCE , TaskEntry.TAMIL_REFERENCE,
                TaskEntry.FAV };
         return database.query(TaskEntry.TABLE_NAME,
                      columns,
                      TaskEntry.GROUP_ID + "=" + category,
                null, null, null, null);
    }

    public Cursor queryBookmarkedDuas(int category){
        String[] columns = new String[] { TaskEntry.DUA_ID, TaskEntry.ARABIC_DUA, TaskEntry.ENGLISH_TRANSLATION,
                TaskEntry.TAMIL_TRANSLATION,TaskEntry.ENGLISH_REFERENCE , TaskEntry.TAMIL_REFERENCE,
                TaskEntry.FAV };
        return database.query(TaskEntry.TABLE_NAME,
                columns,
                TaskEntry.FAV + "= 1" + " AND "+  TaskEntry.GROUP_ID + "=" + category,
                null, null, null, null);
    }
}