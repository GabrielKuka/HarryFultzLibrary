package com.libraryhf.libraryharryfultz.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;

import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetUserBooks;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper.ProgramModel;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.app.ProfileNewsFeedAdapter;
import com.libraryhf.libraryharryfultz.helper.UserData;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private UserData userData;
    private ProfileNewsFeedAdapter adapter;
    private ArrayList<ProgramModel> list;
    private static String bookTypes;
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChangeStatusBarColor.changeColor(this);

        userData = new UserData(this);

        list = new ArrayList<>();
        adapter = new ProfileNewsFeedAdapter(getActivity(), list);

        initializeViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ProfileActivity", "Resumed");
        try {
            checkTypeBooks(getBookTypes());
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d("BookTypes", " " + e.getMessage());
            checkTypeBooks("requested");
        }
    }

    private void initializeViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(userData.getName());

        RecyclerView profileNewsFeed = (RecyclerView) findViewById(R.id.profileNewsFeed);
        profileNewsFeed.setLayoutManager(new LinearLayoutManager(this));
        profileNewsFeed.setItemAnimator(new DefaultItemAnimator());
        profileNewsFeed.setHasFixedSize(true);
        profileNewsFeed.setAdapter(adapter);

        list.add(new ProgramModel()); // <- This is for the header

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setContentView(R.layout.loading_layout);

    }

    public static void hideLoadingDialogFromBookActivity(Dialog d) {
        d.dismiss();
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    public void setItem(String title, String author, String url) {
        list.add(new ProgramModel(title, author, url));
        updateList();
    }

    public void changePanel() {
        list.clear();
        list.add(new ProgramModel());
        updateList();
    }

    private void updateList() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void checkTypeBooks(String type) {
        changePanel();
        switch (type) {
            case "requested":
                GetUserBooks getRequestedBooks = new GetUserBooks(this, userData, getIntent().getIntArrayExtra("Ids"), getIntent().getStringArrayExtra("Titles"), getIntent().getStringArrayExtra("Authors"), getIntent().getStringArrayExtra("ImageUrls"), "requested");
                getRequestedBooks.execute();
                break;
            case "borrowed":
                GetUserBooks getBorrowedBooks = new GetUserBooks(this, userData, getIntent().getIntArrayExtra("Ids"), getIntent().getStringArrayExtra("Titles"), getIntent().getStringArrayExtra("Authors"), getIntent().getStringArrayExtra("ImageUrls"), "borrowed");
                getBorrowedBooks.execute();
                break;
            case "denied":
                GetUserBooks getDeniedBooks = new GetUserBooks(this, userData, getIntent().getIntArrayExtra("Ids"), getIntent().getStringArrayExtra("Titles"), getIntent().getStringArrayExtra("Authors"), getIntent().getStringArrayExtra("ImageUrls"), "denied");
                getDeniedBooks.execute();
                break;
        }
    }

    public static void setBookTypes(String type) {
        bookTypes = type;
    }

    public static String getBookTypes() {
        return bookTypes;
    }

    public void showDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void hideDialog() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

}
