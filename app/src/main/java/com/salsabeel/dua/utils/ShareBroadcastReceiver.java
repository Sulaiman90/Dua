package com.salsabeel.dua.utils;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by MOHAMED SULAIMAN on 11-06-2017.
 */

public class ShareBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ShareBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String selectedApp = intent.getExtras().get(Intent.EXTRA_CHOSEN_COMPONENT).toString();
        Log.d(TAG, "Received intent after selection: "+intent.getExtras().get(Intent.EXTRA_CHOSEN_COMPONENT));
        Bundle extras = intent.getExtras();
        String sharedText = extras.getString("textToShare");
       // Log.d(TAG,"sharedText "+ sharedText);

        if(selectedApp.equals("ComponentInfo{com.whatsapp/com.whatsapp.ContactPicker}")) {
            try {
                Uri uriUrl = Uri.parse("whatsapp://send?text=" + sharedText + "");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                launchBrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchBrowser);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Whatsapp Not Installed.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Log.d(TAG,"not matched to whatsapp");
        }
    }
}
