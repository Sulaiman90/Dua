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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsButton;
import com.salsabeel.dua.R;
import com.salsabeel.dua.adapter.DuaDetailAdapter.ViewHolder;
import com.salsabeel.dua.database.ExternalDbOpenHelper;
import com.salsabeel.dua.database.TaskContract;
import com.salsabeel.dua.model.Dua;
import com.salsabeel.dua.utils.FetchPrefData;
import com.salsabeel.dua.utils.ShareBroadcastReceiver;
import com.salsabeel.dua.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
/**
 * Created by MOHAMED SULAIMAN on 30-03-2017.
 */

public class BookmarksDuaAdapter extends RecyclerView.Adapter<BookmarksDuaAdapter.ViewHolder> {

    private static final String TAG = "BookmarksDuaAdapter";
    private final float prefArabicFontSize;
    private final float prefOtherFontSize;
    private List<Dua> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private ExternalDbOpenHelper mDbHelper;
    private ViewHolder mHolder;
    private Boolean viewArabicAndTamil;
    private Boolean viewArabicOnly;
    private Boolean viewTamilOnly;
    private FetchPrefData fetchPrefData;

    public BookmarksDuaAdapter(Context context, List<Dua> list) {

        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
       // Log.d(TAG,"BookmarksDuaAdapter:constructor "+getItemCount());
       // Log.d(TAG,"BookmarksDuaAdapter:mList "+mList);

        fetchPrefData = new FetchPrefData(mContext);

        viewArabicAndTamil = fetchPrefData.isArabicAndTamilView();
        viewArabicOnly = fetchPrefData.isArabicViewOnly();
        viewTamilOnly = fetchPrefData.isTamilViewOnly();

        prefArabicFontSize = fetchPrefData.getArabicFontSize();
        prefOtherFontSize = fetchPrefData.getTranslationAndRefFontSize();
    }

     @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         Log.d(TAG,"onCreateViewHolder ");
         View itemLayoutView = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.dua_detail_item_card, parent,false);

         // create ViewHolder
         mHolder = new ViewHolder(itemLayoutView);
         return mHolder;
    }

    private void deleteRow(int position){
        mList.remove(position); // this will remove row of data
        notifyItemRemoved(position); // this will do the animation of removal
        notifyItemRangeChanged(position,mList.size()); // From GreenTech email
    }

    @Override
    public void onBindViewHolder(ViewHolder mHolder, int position) {

        final int finalPosition = position;
        final Dua p = mList.get(position);

      //  Log.d(TAG,"onBindViewHolder "+position);

        mHolder.tvDuaNumber.setText(String.valueOf(p.getDuaId()));
        mHolder.tvDuaArabic.setText(p.getArabicDua());
        mHolder.tvDuaArabicReference.setText(p.getArabicReference());
        mHolder.tvDuaTranslation.setText(p.getTamilTranslation());
        mHolder.tvDuaTranslationReference.setText(p.getTamilReference());

       // Log.d(TAG,"dua id "+p.getDuaSerialNo() +" "+p.isFav());
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
            mHolder.layoutTranslation.setVisibility(View.VISIBLE);
            mHolder.layoutDivider.setVisibility(View.GONE);
        }

        /* Log.d(TAG, p.getDuaId() + " " +p.getArabicDua()+ " " +p.getEnglishTranslation()+ " " +p.getEnglishReference()+ " " +
                    p.getTamilTranslation()+ " " +p.getTamilReference());*/
        //Log.d(TAG, p.getTamilTranslation()+ " " +p.getEnglishReference());

        final ViewHolder finalmHolder = mHolder;

        mHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View convertView) {
                Utilities.buildSharingText(mContext,
                        finalmHolder.tvDuaArabic,finalmHolder.tvDuaArabicReference,
                        finalmHolder.tvDuaTranslation, finalmHolder.tvDuaTranslationReference,
                        viewArabicAndTamil, viewArabicOnly ,viewTamilOnly);
            }
        });

        finalmHolder.favButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View ConvertView) {
                boolean isFav =  false;

                int sql_position = Integer.parseInt(finalmHolder.tvDuaNumber.getText().toString());

                deleteRow(finalPosition);
                mDbHelper = new ExternalDbOpenHelper(ConvertView.getContext().getApplicationContext());

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
                        finalmHolder.favButton.setText("{faw-star}");
                    } else {
                        finalmHolder.favButton.setText("{faw-star-o}");
                    }
                }
            }
        });
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    //Holder class
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDuaNumber;
        TextView tvDuaArabic;
        TextView tvDuaArabicReference;
        TextView tvDuaTranslation;
        TextView tvDuaTranslationReference;
        IconicsButton shareButton;
        IconicsButton favButton;

        LinearLayout layoutArabic;
        LinearLayout layoutTranslation;
        View layoutDivider;


        private ViewHolder(View convertView) {
            super(convertView);

            tvDuaNumber = (TextView) convertView.findViewById(R.id.txtDuaNumber);

            tvDuaArabic = (TextView) convertView.findViewById(R.id.txtDuaArabic);
            tvDuaArabic.setTextSize(prefArabicFontSize);

            tvDuaArabicReference = (TextView) convertView.findViewById(R.id.txtDuaArabicRef);
            tvDuaArabicReference.setTextSize(prefArabicFontSize);

            tvDuaTranslation = (TextView) convertView.findViewById(R.id.txtDuaTranslation);
            tvDuaTranslation.setTextSize(prefOtherFontSize);

            tvDuaTranslationReference = (TextView) convertView.findViewById(R.id.txtDuaTranslationReference);
            tvDuaTranslationReference.setTextSize(prefOtherFontSize);

            shareButton = (IconicsButton) convertView.findViewById(R.id.share_btn);
            favButton = (IconicsButton) convertView.findViewById(R.id.fav_btn);

            layoutArabic = (LinearLayout) convertView.findViewById(R.id.arabic_layout);
            layoutTranslation = (LinearLayout) convertView.findViewById(R.id.translation_layout);

            layoutDivider = (View) convertView.findViewById(R.id.layout_divider);
        }
    }
}

