package com.libraryhf.libraryharryfultz.BackgroundProcesses;


import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;

public class GetAuthors extends AsyncTask<Void, Void, Void> {
    private String[] authors;
    private int[] authorIds;

    @Override
    protected Void doInBackground(Void... voids) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_AUTHORS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    String[] a = new String[jsonArray.length()];
                    int[] id = new int[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        a[i] = jsonArray.getJSONObject(i).getString("name");
                        id[i] = jsonArray.getJSONObject(i).getInt("id");
                    }
                    setBookAuthors(a);
                    setAuthorIds(id);
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

    private void setAuthorIds(int[] id) {
        authorIds = new int[id.length];
        System.arraycopy(id, 0, authorIds, 0, id.length);
    }

    private void setBookAuthors(String[] name) {
        authors = new String[name.length];
        System.arraycopy(name, 0, authors, 0, name.length);
    }

    public String[] getBookAuthors() {
        return authors;
    }

    public int[] getAuthorIds() {
        return authorIds;
    }

}
