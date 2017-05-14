package com.libraryhf.libraryharryfultz.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.FetchBooks;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetAuthors;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.helper.ConnectivityState;
import com.libraryhf.libraryharryfultz.helper.TimeListener;
import com.libraryhf.libraryharryfultz.helper.UserData;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import br.com.mauker.materialsearchview.MaterialSearchView;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, MaterialSearchView.OnQueryTextListener {

    private DrawerLayout drawer;

    private UserData userData;

    private MaterialSearchView materialSearchView;

    private FetchBooks fetchBooks;

    private GetAuthors getAuthors;

    private Dialog dialog;

    private Toolbar toolbar;
    /*
        Activity's methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ChangeStatusBarColor.changeColor(this);

        userData = new UserData(this);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        fetchBooks = new FetchBooks(this);
        fetchBooks.execute();

        getAuthors = new GetAuthors();
        getAuthors.execute();

        new FetchBorrowedBooks().execute();

        initializeViews();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dismissDialog();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dismissDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dismissDialog();
        }

        if (fetchBooks.isCancelled()) {
            fetchBooks.execute();
        }
    }

    @Override
    public void onBackPressed() {

        if (materialSearchView != null && !materialSearchView.isOpen())
            this.finishAffinity();
        else if (materialSearchView != null)
            materialSearchView.closeSearch();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.profileIconId:
                Intent intent = new Intent(this, UserProfile.class);
                intent.putExtra("Ids", fetchBooks.getBookIds());
                intent.putExtra("Titles", fetchBooks.getBookTitles());
                intent.putExtra("Authors", fetchBooks.getBookAuthors());
                intent.putExtra("ImageUrls", fetchBooks.getBookCovers());
                startActivity(intent);
                break;
            case R.id.websiteIconId:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://ec2-52-39-232-168.us-west-2.compute.amazonaws.com/"));
                startActivity(i);
                break;
            case R.id.settingsIconId:
                startActivity(new Intent(Dashboard.this, SettingsActivity.class));
                break;
            case R.id.logoutIconId:
                userData.logoutUser(this);
                break;
            case R.id.aboutUsId:
                startActivity(new Intent(Dashboard.this, AboutUs.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:

                if (materialSearchView != null) {
                    Log.d("SearchView:", "Not null");
                    materialSearchView.openSearch();
                } else {
                    initializeSearchView();
                    materialSearchView.openSearch();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public AppCompatActivity getActivity() {
        return this;
    }



    /*
        Other methods
     */

    private void initializeViews() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.inflateHeaderView(R.layout.dashboard_nav_header_navigation);
        TextView navTitle = (TextView) headerView.findViewById(R.id.navHeaderTitleId);
        TextView navSubtitle = (TextView) headerView.findViewById(R.id.navHeaderSubtitleId);
        navTitle.setText(userData.getName());
        navSubtitle.setText(userData.getEmail());

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);

    }

    public void initializeSearchView() {
        materialSearchView = (MaterialSearchView) findViewById(R.id.sv);
        materialSearchView.setOnItemClickListener(this);
        materialSearchView.setOnQueryTextListener(this);
        materialSearchView.setShouldKeepHistory(false);
        materialSearchView.clearSuggestions();
        materialSearchView.adjustTintAlpha((float) 0.9);
        if (fetchBooks == null) {
            Log.d("FetchBooks", "Reinitialized");
            fetchBooks = new FetchBooks(this);
            fetchBooks.execute();
            materialSearchView.addSuggestions(fetchBooks.getBookTitles());
        } else {
            try {
                materialSearchView.addSuggestions(fetchBooks.getBookTitles());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Log.d("SearchView:", "Set up");
    }

    /*
        MaterialSearchView event Handlers
     */

    @Override
    public boolean onQueryTextSubmit(String s) {


        materialSearchView.closeSearch();

        if (new ConnectivityState(this).isConnected()) {


            int bookNumbers = fetchBooks.getBookTitles().length;
            int authorNumbers = getAuthors.getBookAuthors().length;

            boolean emptyBookArray = true;
            boolean emptyAuthorArray = true;

            ArrayList<String> resultBookArray = new ArrayList<>();
            ArrayList<String> resultAuthorArray = new ArrayList<>();

            Intent resultIntent = new Intent(this, SearchResults.class);


            for (int i = 0; i < bookNumbers; i++) {
                if (fetchBooks.getBookTitles()[i].contains(s)) {
                    emptyBookArray = false;
                    resultBookArray.add(fetchBooks.getBookTitles()[i]);
                }
            }

            for (int i = 0; i < authorNumbers; i++) {
                if (getAuthors.getBookAuthors()[i].contains(s)) {
                    emptyAuthorArray = false;
                    resultAuthorArray.add(getAuthors.getBookAuthors()[i]);
                }
            }

            if (!emptyBookArray || !emptyAuthorArray) {

                Bundle b = new Bundle();

                b.putStringArrayList("booksResults", resultBookArray);
                b.putStringArrayList("authorsResults", resultAuthorArray);

                resultIntent.putExtras(b);
                startActivity(resultIntent);


            } else if (emptyAuthorArray && emptyBookArray) {
                Alerter.create(this)
                        .setBackgroundColor(R.color.colorAccent)
                        .setText("Nuk ka rezultate")
                        .enableIconPulse(true)
                        .setDuration(1000)
                        .show();
            }
        } else {
            MyDynamicToast.errorMessage(AppController.getInstance(), "Nuk jeni i lidhur me internet");
        }


        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        materialSearchView.closeSearch();

        if (new ConnectivityState(this).isConnected()) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                GetBookInfo getBookInfo = new GetBookInfo(getActivity(), materialSearchView.getSuggestionAtPosition(i), dialog);
                getBookInfo.execute();
            } else {
                GetBookInfo getBookInfo = new GetBookInfo(getActivity(), materialSearchView.getSuggestionAtPosition(i));
                getBookInfo.execute();
            }
        } else {
            MyDynamicToast.errorMessage(AppController.getInstance(), "Nuk jeni i lidhur me internet");
        }


    }


    /*
        ProgessDialog methods
     */


    private void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private class FetchBorrowedBooks extends AsyncTask<Void, Void, Void> {

        private Intent intent;

        @Override
        protected Void doInBackground(Void... voids) {

            intent = new Intent(Dashboard.this, TimeListener.class);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://ec2-52-39-232-168.us-west-2.compute.amazonaws.com/api/user/" + userData.getUserId() + "/borrows", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String deadline = jsonObject.getString("deadline");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.add(Calendar.DATE, 0);
                                String formattedCurrentDate = dateFormat.format(c.getTime());
                                if (formattedCurrentDate.equals(deadline) && jsonObject.getInt("status") == 0) {

                                    intent.putExtra("userId", userData.getUserId());
                                    intent.putExtra("userName", userData.getName());
                                    intent.putExtra("deadline", true);
                                    startBroadCast();
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            AppController.getInstance().addToRequestQueue(stringRequest);

            return null;
        }

        private void startBroadCast() {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 7);
            c.set(Calendar.MINUTE, 30);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(Dashboard.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }


    }
}