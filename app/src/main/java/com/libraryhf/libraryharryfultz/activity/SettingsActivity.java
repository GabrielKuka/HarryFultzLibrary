package com.libraryhf.libraryharryfultz.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.helper.UserData;

import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {

    public Activity getActivity() {
        return this;
    }

    public static UserData userData;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.d("String value: ", stringValue);
            if (preference.getKey().equals("ageKey")) {
                preference.setSummary(userData.getBirthday());
            } else if (preference.getKey().equals("fullNameKey")) {
                preference.setSummary(userData.getName());
            } else if (preference.getKey().equals("emailKey")) {
                preference.setSummary(userData.getEmail());
            } else if (preference.getKey().equals("classKey")) {
                preference.setSummary(userData.getUserClass());
            }
            //preference.setSummary(stringValue);
            return true;
        }
    };


    public static UserData getUserData() {
        return userData;
    }


    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);


        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        userData = new UserData(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }


    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || EndFragment.class.getName().equals(fragmentName);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private static Preference fullnamePref, emailPref, passwordPref, classPref, agePref;
        private static UserData userData;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            initPrefs();


        }

        @Override
        public void onResume() {
            super.onResume();

        }

        private void initPrefs() {

            userData = getUserData();

            // Initialize preferences
            fullnamePref = findPreference("fullnameKey");
            emailPref = findPreference("emailKey");
            passwordPref = findPreference("passwordKey");
            classPref = findPreference("classKey");
            agePref = findPreference("ageKey");

            fullnamePref.setSummary(userData.getName());
            emailPref.setSummary(userData.getEmail());

            // Set event handlers to the preferences
            passwordPref.setOnPreferenceChangeListener(this);
            classPref.setOnPreferenceChangeListener(this);

            passwordPref.setOnPreferenceClickListener(this);

            // Bind the summaries to each preference
            bindPreferenceSummaryToValue(findPreference("fullnameKey"));
            bindPreferenceSummaryToValue(findPreference("emailKey"));
            bindPreferenceSummaryToValue(findPreference("classKey"));
            bindPreferenceSummaryToValue(findPreference("ageKey"));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference == passwordPref) {
                //Cfarë do ndodhi kur të ndryshohet fjalëkalimi i llogarisë

                // Notify user about the state
                MyDynamicToast.successMessage(AppController.getInstance(), "Fjalëkalimi u ndryshua.");
            } else if (preference == classPref) {

                // Send POST request to website and change the class at the local DB

                // Notify user about the state
                bindPreferenceSummaryToValue(findPreference("classKey"));
                MyDynamicToast.successMessage(AppController.getInstance(), "Klasa u ndryshua.");
            }
            return true;
        }

        public static Preference getEmailPref() {
            return emailPref;
        }

        public static Preference getUserName() {
            return fullnamePref;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.equals(passwordPref)) {
                startActivity(new Intent(this.getActivity(), ChangePassword.class));
            }
            return true;
        }
    }

    public static class EndFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private Preference logOutPref, deleteAccountPref;
        private UserData userData;

        @Override
        public void onCreate(Bundle e) {
            super.onCreate(e);
            addPreferencesFromResource(R.xml.pref_others);
            setHasOptionsMenu(true);
            userData = new UserData(getActivity());
            initPrefs();
        }

        private void initPrefs() {

            // Initialize preferences
            logOutPref = findPreference("logoutKey");
            deleteAccountPref = findPreference("deleteAccountKey");

            // Set event handlers for the preferences
            logOutPref.setOnPreferenceClickListener(this);
            deleteAccountPref.setOnPreferenceClickListener(this);

        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            if (preference == logOutPref) {
                // Logout user
                userData.logoutUser(getActivity());

            } else if (preference == deleteAccountPref) {
                // Delete user account
                userData.deleteAccount(getActivity());
            }

            return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }
}

