package com.libraryhf.libraryharryfultz.activity.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.desai.vatsal.myrecylerviewlibrary.MyDynamicRecyclerView;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper.NewsFeedAdapter;
import com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper.ProgramModel;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.helper.ConnectivityState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NewsFeed extends Fragment implements MyDynamicRecyclerView.LoadMoreListener {

    MyDynamicRecyclerView myRecyclerview;
    NewsFeedAdapter testAdapter;
    ArrayList<ProgramModel> modelArrayList;
    LinearLayoutManager linearLayoutManager;
    Dialog dialog;
    int limit;


    public NewsFeed() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("NewsFeedFragment", "Destroyed");
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("NewsFeedFragment", "Paused");
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAllBooksByHeader();
        getActivity().setTitle("Të gjithë");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("NewsFeedFragment", "Created");

        View v = inflater.inflate(R.layout.news_feed_fragment_layout, container, false);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);

        myRecyclerview = (MyDynamicRecyclerView) v.findViewById(R.id.myRecyclerview);

        modelArrayList = new ArrayList<>();
        testAdapter = new NewsFeedAdapter(getActivity().getApplicationContext(), modelArrayList, (AppCompatActivity) getActivity(), this, false);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        myRecyclerview.setScrollContainer(false);
        myRecyclerview.setBackgroundColor("#FFFFFF");
        myRecyclerview.setSwipeRefresh(false);
        myRecyclerview.setColorSchemeColors(Color.RED, Color.YELLOW, Color.BLACK, Color.GREEN, Color.BLUE);
        myRecyclerview.setLoadMore(true, linearLayoutManager);
        myRecyclerview.setLoadMoreListener(this);
        myRecyclerview.setLayoutManager(linearLayoutManager);
        myRecyclerview.setItemAnimator(new DefaultItemAnimator());
        myRecyclerview.setSimpleDivider(false);
        myRecyclerview.setAdapter(testAdapter);

        limit = 0;
        if (checkInternetForNewsFeed()) {
            fetchAllBooksFromDB();
        } else {
            myRecyclerview.showInfoLayout();
        }

        return v;
    }

    public void fetchAllBooksByHeader() {
        limit = 0;
        if (modelArrayList.size() >= 2) {
            removeItems();
        }

        new FetchBooks(true).execute();
        testAdapter.notifyDataSetChanged();
    }

    public void fetchAllBooksFromDB() {
        if (modelArrayList.size() >= 2) {
            removeItems();
        }

        new FetchBooks(false).execute();
        testAdapter.notifyDataSetChanged();
    }

    public void addHeader() {
        if (modelArrayList.size() == 0)
            modelArrayList.add(new ProgramModel());
    }

    public void addItem(String title, String author, String imageName) {
        modelArrayList.add(new ProgramModel(title, author, imageName));
        testAdapter.notifyDataSetChanged();
    }

    public void removeItems() {
        modelArrayList.clear();
        addHeader();
    }

    private boolean checkInternetForNewsFeed() {
        if (new ConnectivityState(getActivity()).isConnected()) {
            myRecyclerview.hideInfoLayout();
            return true;
        } else {
            // Fonts for the title and the message of the content
            Typeface messageTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
            Typeface titleTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");

            // Setting up the content
            myRecyclerview.setInfoIcon(R.drawable.warning_icon, "#000000");
            myRecyclerview.setInfoTitle("Nuk jeni i lidhur me internet.", 18, Color.GRAY, titleTypeface);
            myRecyclerview.setInfoMessage("Rifreskoni edhe njëherë për të kontrolluar aksesin në internet.", 13, Color.GRAY, messageTypeface);
            return false;
        }
    }

    @Override
    public void OnLoadMore() {
        new FetchBooks(true).execute();
        MyDynamicToast.informationMessage(AppController.getInstance(), "...");
    }

    public static void hideLoadingDialogFromBookActivity(Dialog d) {
        d.dismiss();
    }

    private class FetchBooks extends AsyncTask<Void, Void, Void> {

        private boolean reload;

        FetchBooks(boolean reload) {
            this.reload = reload;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            getBooksFromDB();
            return null;
        }

        private void getBooksFromDB() {
            StringRequest requestBooks = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_BOOKS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = limit; i < jsonArray.length(); i++) {
                            if (i < (limit + 5)) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String imageName = AppConfig.IMAGE_BASE_URL + jsonObject.getString("cover");
                                modelArrayList.add(new ProgramModel(jsonObject.getString("title"), jsonObject.getString("author"), imageName));
                            } else {
                                break;
                            }
                        }
                        limit += 5;
                        if (reload) {
                            testAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MyDynamicToast.errorMessage(AppController.getInstance(), "" + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MyDynamicToast.errorMessage(AppController.getInstance(), "Request did not work!");
                }
            });

            AppController.getInstance().addToRequestQueue(requestBooks);

        }

    }

}
