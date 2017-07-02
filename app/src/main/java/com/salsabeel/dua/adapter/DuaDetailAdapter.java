package com.salsabeel.dua.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsButton;
import com.salsabeel.dua.BookMarksActivity;
import com.salsabeel.dua.R;
import com.salsabeel.dua.database.*;
import com.salsabeel.dua.model.Dua;
import com.salsabeel.dua.utils.FetchPrefData;
import com.salsabeel.dua.utils.ShareBroadcastReceiver;
import com.salsabeel.dua.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.data;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by MOHAMED SULAIMAN on 30-03-2017. DuaDetailAdapter
 */

public class DuaDetailAdapter extends BaseAdapter {

    private static final String TAG = "DuaDetailAdapter";
    private final float prefArabicFontSize;
    private final float prefOtherFontSize;
    private ArrayList<Dua> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private ExternalDbOpenHelper mDbHelper;
    private Boolean viewArabicAndTamil;
    private Boolean viewArabicOnly;
    private Boolean viewTamilOnly;
    private FetchPrefData fetchPrefData;

    public DuaDetailAdapter(Context context, ArrayList<Dua> list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;

        fetchPrefData = new FetchPrefData(mContext);

        viewArabicAndTamil = fetchPrefData.isArabicAndTamilView();
        viewArabicOnly = fetchPrefData.isArabicViewOnly();
        viewTamilOnly = fetchPrefData.isTamilViewOnly();

        prefArabicFontSize = fetchPrefData.getArabicFontSize();
        prefOtherFontSize = fetchPrefData.getTranslationAndRefFontSize();

       /* Log.d(TAG,"viewArabicAndTamil " +viewArabicAndTamil +" viewArabicOnly " +viewArabicOnly +
                  " viewTamilOnly " +viewTamilOnly);
        Log.d(TAG,"prefArabicFontSize " +prefArabicFontSize +" prefOtherFontSize " +prefOtherFontSize );*/

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Dua getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder mHolder;
        final Dua p = getItem(position);

        if (convertView == null) {

            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.dua_detail_item_card, parent, false);

            mHolder.tvDuaNumber = (TextView) convertView.findViewById(R.id.txtDuaNumber);

            mHolder.tvDuaArabic = (TextView) convertView.findViewById(R.id.txtDuaArabic);
            mHolder.tvDuaArabic.setTextSize(prefArabicFontSize);

            mHolder.tvDuaArabicReference = (TextView) convertView.findViewById(R.id.txtDuaArabicRef);
            mHolder.tvDuaArabicReference.setTextSize(prefArabicFontSize - Utilities.ARABIC_DUA_REF_FONT_SIZE);

            mHolder.tvDuaTranslation = (TextView) convertView.findViewById(R.id.txtDuaTranslation);
            mHolder.tvDuaTranslation.setTextSize(prefOtherFontSize);

            mHolder.tvDuaTranslationReference = (TextView) convertView.findViewById(R.id.txtDuaTranslationReference);
            mHolder.tvDuaTranslationReference.setTextSize(prefOtherFontSize - Utilities.TRANSLATION_DUA_REF_FONT_SIZE);

            mHolder.shareButton = (IconicsButton) convertView.findViewById(R.id.share_btn);
            mHolder.favButton = (IconicsButton) convertView.findViewById(R.id.fav_btn);

            mHolder.layoutArabic = (LinearLayout) convertView.findViewById(R.id.arabic_layout);
            mHolder.layoutTranslation = (LinearLayout) convertView.findViewById(R.id.translation_layout);

            mHolder.layoutDivider = (View) convertView.findViewById(R.id.layout_divider);

            convertView.setTag(mHolder);
        }
        else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View convertView) {
                Utilities.buildSharingText(mContext,mHolder.tvDuaArabic,mHolder.tvDuaArabicReference,mHolder.tvDuaTranslation,
                                mHolder.tvDuaTranslationReference,viewArabicAndTamil, viewArabicOnly ,viewTamilOnly);
            }
        });

        final View finalConvertView = convertView;
        mHolder.favButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View ConvertView) {

                Log.d(TAG,"position "+position);
                boolean isFav = !p.isFav();

                mDbHelper = new ExternalDbOpenHelper(finalConvertView.getContext().getApplicationContext());

                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                // New value for one column
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.FAV, isFav);

                // Which row to update, based on the ID
                String selection = TaskContract.TaskEntry.DUA_ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(p.getDuaId())};

                int count = db.update(
                        TaskContract.TaskEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                if (count == 1) {
                    if (isFav) {
                        mHolder.favButton.setText("{faw-star}");
                    } else {
                        mHolder.favButton.setText("{faw-star-o}");
                    }
                    p.setFav(isFav);
                }
            }
        });

        if (p != null) {
            mHolder.tvDuaNumber.setText(String.valueOf(p.getDuaId()));
            mHolder.tvDuaArabic.setText(p.getArabicDua());
            mHolder.tvDuaArabicReference.setText(p.getArabicReference());
            mHolder.tvDuaTranslation.setText(p.getTamilTranslation());
            mHolder.tvDuaTranslationReference.setText(p.getTamilReference());

            if (p.isFav()) {
                mHolder.favButton.setText("{faw-star}");
            } else {
                mHolder.favButton.setText("{faw-star-o}");
            }

            if(viewArabicAndTamil){
                mHolder.layoutArabic.setVisibility(View.VISIBLE);
                mHolder.layoutTranslation.setVisibility(View.VISIBLE);
                mHolder.layoutDivider.setVisibility(View.VISIBLE);
            }
            else if(viewArabicOnly){
                mHolder.layoutArabic.setVisibility(View.VISIBLE);
                mHolder.layoutTranslation.setVisibility(View.GONE);
                mHolder.layoutDivider.setVisibility(View.GONE);
            }
            else if(viewTamilOnly){
                mHolder.layoutArabic.setVisibility(View.GONE);
                mHolder.layoutDivider.setVisibility(View.GONE);
                mHolder.layoutTranslation.setVisibility(View.VISIBLE);
            }

           /* Log.d(TAG, p.getDuaId() + " " +p.getArabicDua()+ " " +p.getEnglishTranslation()+ " " +p.getEnglishReference()+ " " +
                    p.getTamilTranslation()+ " " +p.getTamilReference());*/
            //Log.d(TAG, p.getTamilTranslation()+ " " +p.getEnglishReference());

        }
        return convertView;
    }

    /*public void doSocialShare(String title, String text){
       // Log.d(TAG,"doSocialShare called");
        // First search for compatible apps with sharing (Intent.ACTION_SEND)
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        // Set title and text to share when the user selects an option.
        shareIntent.putExtra(Intent.EXTRA_TITLE, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                targetedShare.setType("text/plain"); // put here your mime type
                targetedShare.setPackage(info.activityInfo.packageName.toLowerCase());
                targetedShareIntents.add(targetedShare);
            }
            // Then show the ACTION_PICK_ACTIVITY to let the user select it
            Intent intentPick = new Intent();
            intentPick.setAction(Intent.ACTION_PICK_ACTIVITY);
            // Set the title of the dialog
            intentPick.putExtra(Intent.EXTRA_TITLE, title);
            intentPick.putExtra(Intent.EXTRA_INTENT, shareIntent);
          //  intentPick.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray());
            // Call StartActivityForResult so we can get the app name selected by the user
            ((Activity) mContext).startActivityForResult(intentPick, REQUEST_CODE_MY_PICK);
        }
    }*/


    public static class ViewHolder {
        TextView tvDuaNumber;
        TextView tvDuaArabic;
        TextView tvDuaArabicReference;

        TextView tvDuaTranslation;
        TextView tvDuaTranslationReference;

        LinearLayout layoutArabic;
        LinearLayout layoutTranslation;

        View layoutDivider;

        IconicsButton shareButton;
        IconicsButton favButton;
    }
}

