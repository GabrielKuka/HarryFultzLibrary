package com.libraryhf.libraryharryfultz.app;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.libraryhf.libraryharryfultz.R;

public class ChangeStatusBarColor {

    public static void changeColor(AppCompatActivity a){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = a.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(a.getResources().getColor(R.color.colorAccent));
        }
    }

    public static void changeColor(Activity a){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = a.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(a.getResources().getColor(R.color.colorAccent));
        }
    }

}
