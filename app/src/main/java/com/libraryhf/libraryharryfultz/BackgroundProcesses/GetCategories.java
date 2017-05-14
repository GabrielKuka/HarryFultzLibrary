package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper.NewsFeedAdapter;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCategories extends AsyncTask<Void, Void, Void> {

    private NewsFeedAdapter.ViewHeader newsFeedAdapter;

    public GetCategories(NewsFeedAdapter.ViewHeader n) {
        this.newsFeedAdapter = n;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        printCategories();
        return null;
    }

    private void printCategories() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        newsFeedAdapter.addCardView(jsonObject.getInt("id"), jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyDynamicToast.errorMessage(AppController.getInstance(), "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.errorMessage(AppController.getInstance(), "JSON Error!");
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onPostExecute(Void v) {

    }

}
