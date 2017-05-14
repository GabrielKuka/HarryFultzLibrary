package com.libraryhf.libraryharryfultz.helper;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.LoginActivity;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class UserData {

    private SQLiteHandler db;
    private SessionManager session;
    private HashMap<String, String> user;
    private ArrayList<String> recentTitles, recentAuthors, recentImageUrls;

    public UserData(Activity a) {

        // SqLite database handler
        db = new SQLiteHandler(a);

        // session manager
        session = new SessionManager(a);

        // checks if the user is logged in or not
        if (!session.isLoggedIn()) {
            logoutUser(a);
        }

        user = db.getUserDetails();
        recentTitles = db.getRecentTitles();
        recentAuthors = db.getRecentAuthors();
        recentImageUrls = db.getRecentImageUrls();
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    public void logoutUser(final Activity a) {

        new LovelyStandardDialog(a)
                .setButtonsColorRes(R.color.colorPrimary)
                .setMessage(R.string.logOutAccountDialog)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        session.setLogin(false);

                        db.deleteUsers();

                        // Launching the login activity
                        a.startActivity(new Intent(a, LoginActivity.class));
                        a.finishAffinity();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();

    }

    public void deleteAccount(final Activity a) {
        new LovelyStandardDialog(a)
                .setTopColorRes(R.color.colorAccent)
                .setButtonsColorRes(R.color.colorPrimary)
                .setIcon(R.drawable.warning_icon)
                .setMessage(R.string.deleteAccountMessageDialog)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DeleteAccount().execute();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public String getName() {
        return user.get("name");
    }

    public String getEmail() {
        return user.get("email");
    }

    public String getGender() {
        return user.get("gender");
    }

    public String getBirthday() {
        return user.get("birthday");
    }

    public String getUserClass() {
        return user.get("userClass");
    }

    public String getUserId() {
        return user.get("uid");
    }

    public String getUserProfileImage() {
        return user.get("prfImage");
    }

    public ArrayList<String> getRecentTitles() {
        return this.recentTitles;
    }

    public ArrayList<String> getRecentAuthors() {
        return this.recentAuthors;
    }

    public ArrayList<String> getRecentImageUrls() {
        return this.recentImageUrls;
    }

    private class DeleteAccount extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Sent a post requeset to delete user data and delete userdata from local db
            return null;
        }
    }

}
