package com.libraryhf.libraryharryfultz.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rreth nesh");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ChangeStatusBarColor.changeColor(this);
    }

    public void launchSchoolWebsite(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.harryfultz.edu.al")));
    }

    public void callGabriel(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: +355698567597")));
    }

}
