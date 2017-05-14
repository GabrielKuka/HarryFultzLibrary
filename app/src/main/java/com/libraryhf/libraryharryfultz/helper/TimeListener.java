package com.libraryhf.libraryharryfultz.helper;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppController;

public class TimeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tag = "TIME_LISTENER";
        String userId = intent.getExtras().getString("userId");
        String userName = intent.getExtras().getString("userName");
        String bookName = intent.getExtras().getString("bookName");
        boolean deadline = intent.getExtras().getBoolean("deadline");

        if (userId != null && userName != null && deadline) {


            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setColor(Color.parseColor("#23487c"));
            mBuilder.setSmallIcon(R.drawable.hflogo);
            mBuilder.setContentTitle("Afati për huazimin e librit mbaroi");
            mBuilder.setContentText(userName + ", sot ke për të dorëzuar librin.");

            new GetBookInfo(context, bookName);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        } else if (userId == null && userName == null) {
            MyDynamicToast.informationMessage(AppController.getInstance(), "No notifications");
        }

    }
}
