package com.salsabeel.dua.adapter;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsButton;
import com.salsabeel.dua.R;
import com.salsabeel.dua.database.*;
import com.salsabeel.dua.model.Dua;
import com.salsabeel.dua.utils.FetchPrefData;
import com.salsabeel.dua.utils.ShareBroadcastReceiver;

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

            mHolder.tvDuaTranslation = (TextView) convertView.findViewById(R.id.txtDuaTranslation);
            mHolder.tvDuaTranslation.setTextSize(prefOtherFontSize);

            mHolder.tvDuaReference = (TextView) convertView.findViewById(R.id.txtDuaReference);
            mHolder.tvDuaReference.setTextSize(prefOtherFontSize);

            mHolder.shareButton = (IconicsButton) convertView.findViewById(R.id.share_btn);
            mHolder.favButton = (IconicsButton) convertView.findViewById(R.id.fav_btn);

            convertView.setTag(mHolder);
        }
        else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View convertView) {
                String heading = mContext.getResources().getString(R.string.app_name);
                 /*   String textToShare = heading + "\n\n" +
                            mHolder.tvDuaArabic.getText() + "\n\n" +
                            mHolder.tvDuaTranslation.getText() + "\n\n" +
                            mHolder.tvDuaReference.getText() + "\n\n" +
                            convertView.getResources().getString(R.string.action_share_credit);*/

                String textToShare = heading + "\n\n";
                if(mHolder.tvDuaArabic.getVisibility()==View.VISIBLE){
                    textToShare = textToShare + mHolder.tvDuaArabic.getText() + "\n\n";
                }
                if(mHolder.tvDuaTranslation.getVisibility()==View.VISIBLE){
                    textToShare = textToShare + mHolder.tvDuaTranslation.getText() + "\n\n";
                }
                textToShare = textToShare + mHolder.tvDuaReference.getText() + "\n\n" +
                        convertView.getResources().getString(R.string.action_share_credit);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,textToShare);
                intent.setType("text/plain");
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP_MR1) {
                    Intent receiver = new Intent(convertView.getContext(), ShareBroadcastReceiver.class)
                            .putExtra("textToShare", textToShare);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(convertView.getContext(), 0,
                            receiver, PendingIntent.FLAG_UPDATE_CURRENT);
                    Intent chooser = Intent.createChooser(
                            intent,
                            convertView.getResources().getString(R.string.action_share_title),
                            pendingIntent.getIntentSender());
                    convertView.getContext().startActivity(chooser);
                }
                else{
                    convertView.getContext().startActivity(
                            Intent.createChooser(
                                    intent,
                                    convertView.getResources().getString(R.string.action_share_title)
                            )
                    );
                }
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
            mHolder.tvDuaNumber.setText(String.valueOf(p.getDuaSerialNo()));
            mHolder.tvDuaArabic.setText(p.getArabicDua());
            mHolder.tvDuaTranslation.setText(p.getTamilTranslation());
            mHolder.tvDuaReference.setText(p.getTamilReference());
            if (p.isFav()) {
                mHolder.favButton.setText("{faw-star}");
            } else {
                mHolder.favButton.setText("{faw-star-o}");
            }

            if(viewArabicAndTamil){
                mHolder.tvDuaArabic.setVisibility(View.VISIBLE);
                mHolder.tvDuaTranslation.setVisibility(View.VISIBLE);
            }
            else if(viewArabicOnly){
                mHolder.tvDuaArabic.setVisibility(View.VISIBLE);
                mHolder.tvDuaTranslation.setVisibility(View.GONE);
            }
            else if(viewTamilOnly){
                mHolder.tvDuaArabic.setVisibility(View.GONE);
                mHolder.tvDuaTranslation.setVisibility(View.VISIBLE);
            }

           /* Log.d(TAG, p.getDuaId() + " " +p.getArabicDua()+ " " +p.getEnglishTranslation()+ " " +p.getEnglishReference()+ " " +
                    p.getTamilTranslation()+ " " +p.getTamilReference());*/
            //Log.d(TAG, p.getTamilTranslation()+ " " +p.getEnglishReference());

        }
        return convertView;
    }


    public static class ViewHolder {
        TextView tvDuaNumber;
        TextView tvDuaArabic;
        TextView tvDuaReference;
        TextView tvDuaTranslation;
        IconicsButton shareButton;
        IconicsButton favButton;
    }
}

