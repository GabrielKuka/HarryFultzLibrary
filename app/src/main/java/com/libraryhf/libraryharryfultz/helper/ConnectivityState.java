package com.libraryhf.libraryharryfultz.helper;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityState {

    private Activity ac;

    public ConnectivityState(Activity a){
        this.ac = a;
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) ac.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return nInfo != null && nInfo.isConnected();
    }

}
