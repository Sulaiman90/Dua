package com.salsabeel.dua.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.salsabeel.dua.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOHAMED SULAIMAN on 17-06-2017.
 */

public class Utilities {

    private static Context mContext;
    public static final int REQUEST_CODE_MY_PICK = 1;
    public static final String WHATSAPP_COMPONENT_NAME = "com.whatsapp/.ContactPicker";

    private static final String TAG = "Utilities";

    private static final String PLAYSTORE_LINK = "https://play.google.com/store/apps/details?id=com.salsabeel.dua";

    public static void checkVersionAndShareDua(String textToShare){
        //Log.d(TAG,"Utilities");
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,textToShare);
            intent.setType("text/plain");

            Intent receiver = new Intent(mContext, ShareBroadcastReceiver.class)
                    .putExtra("textToShare", textToShare);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                    receiver, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent chooser = Intent.createChooser(
                    intent,
                    mContext.getResources().getString(R.string.action_share_title),
                    pendingIntent.getIntentSender());
            mContext.startActivity(chooser);
        }
        else{
            createShareIntent(mContext.getResources().getString(R.string.action_share_title), textToShare);
        }
    }
    
    // building necessary intents for versions less than 5.1

    public static void createShareIntent(String title, String text){
        // Log.d(TAG,"createShareIntent called");
        // First search for compatible apps with sharing (Intent.ACTION_SEND)
        List<Intent> targetedShareIntents = new ArrayList<>();
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
            intentPick.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray());
            // Call StartActivityForResult so we can get the app name selected by the user
            ((Activity) mContext).startActivityForResult(intentPick, REQUEST_CODE_MY_PICK);
        }
    }

    // sharing duas for versions less than 5.1

    public static void shareDua(Intent launchingIntent){
        if(launchingIntent != null && launchingIntent.getComponent() != null && !TextUtils.isEmpty(launchingIntent.getComponent().flattenToShortString()) ) {
            String appName = launchingIntent.getComponent().flattenToShortString();
            // Log.d(TAG,"selected app "+appName);
            Bundle extras = launchingIntent.getExtras();
            String sharedText = extras.getString(Intent.EXTRA_TEXT);
            // Log.d(TAG,"sharedText "+ sharedText);
            if(appName.equals(WHATSAPP_COMPONENT_NAME)) {
                try {
                    Uri uriUrl = Uri.parse("whatsapp://send?text=" + sharedText + "");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    launchBrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(launchBrowser);
                } catch (ActivityNotFoundException e) {
                    //Toast.makeText(mContext, "Whatsapp Not Installed.", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Whatsapp Not Installed.");
                }
            }
            else{
                //Log.d(TAG,"not matched to whatsapp");
                mContext.startActivity(launchingIntent);
            }
        }
    }

    public static void buildSharingText(Context context,TextView tvDuaArabic,TextView tvDuaArabicReference,TextView tvDuaTranslation,TextView tvDuaTranslationReference,
                                        Boolean showBoth,  Boolean showArabicOnly,Boolean showTranslationOnly){

        mContext = context;

        String heading = mContext.getResources().getString(R.string.app_name);
        String textToShare = heading + "\n\n";

        if(showBoth){
            textToShare = textToShare + tvDuaArabic.getText() + "\n\n" +  tvDuaArabicReference.getText() + "\n\n" +
                     tvDuaTranslation.getText() + "\n\n" +  tvDuaTranslationReference.getText() + "\n\n";
        }
        else if(showArabicOnly){
            textToShare = textToShare + tvDuaArabic.getText() + "\n\n" + tvDuaArabicReference.getText() + "\n\n" ;
        }
        else if(showTranslationOnly){
            textToShare = textToShare + tvDuaTranslation.getText() + "\n\n" + tvDuaTranslationReference.getText() + "\n\n";
        }

        textToShare = textToShare + mContext.getResources().getString(R.string.action_share_credit);

        textToShare = textToShare + "\n\n" +PLAYSTORE_LINK;

        checkVersionAndShareDua(textToShare);
    }
}
