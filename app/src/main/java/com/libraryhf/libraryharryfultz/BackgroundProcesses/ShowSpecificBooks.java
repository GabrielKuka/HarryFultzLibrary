package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.activity.Fragments.NewsFeed;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowSpecificBooks extends AsyncTask<Void, Void, Void> {

    private int categoryId;
    private Fragment fragment;

    public ShowSpecificBooks(String id, Fragment f) {
        this.categoryId = Integer.valueOf(id);
        this.fragment = f;

    }

    @Override
    protected Void doInBackground(Void... voids) {
        fetchTheBooks();
        return null;
    }

    private void fetchTheBooks() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_URL_GET + "/category/" + categoryId + "/books", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    ((NewsFeed) fragment).removeItems();
                    if(jsonArray.length() == 0){
                        MyDynamicToast.informationMessage(AppController.getInstance(), "Nuk ka libra në këtë kategori.");
                    }else{
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imageName = AppConfig.IMAGE_BASE_URL + jsonObject.getString("cover");

                        ((NewsFeed) fragment).addItem(jsonObject.getString("title"), jsonObject.getString("author"), imageName);
                    }}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.errorMessage(AppController.getInstance(), "Possibly internet issue");
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
