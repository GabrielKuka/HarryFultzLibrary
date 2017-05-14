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

import java.util.ArrayList;
import java.util.Random;

public class GetRandomBooks extends AsyncTask<Void, Void, Void> {

    private ArrayList<String> titles, authors, imageUrls;

    public GetRandomBooks() {
        titles = new ArrayList<>();
        authors = new ArrayList<>();
        imageUrls = new ArrayList<>();

    }

    @Override
    protected Void doInBackground(Void... voids) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_BOOKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    final Random randomBookNumber = new Random();

                    for (int i = 0; i < 10; i++) {
                        int randomBookId = randomBookNumber.nextInt(jsonArray.length());
                        if (!checkIfExists(jsonArray.getJSONObject(randomBookId).getString("title"))) {
                            titles.add(jsonArray.getJSONObject(randomBookId).getString("title"));
                            authors.add(jsonArray.getJSONObject(randomBookId).getString("author"));
                            imageUrls.add(AppConfig.IMAGE_BASE_URL + jsonArray.getJSONObject(randomBookId).getString("cover"));
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

    private boolean checkIfExists(String title) {
        boolean present = false;
        for (int x = 0; x < titles.size(); x++) {
            if (title.equals(titles.get(x))) {
                present = true;
                break;
            } else {
                present = false;
            }
        }

        return present;
    }


    public ArrayList<String> getRandomTitlesList() {
        return titles;
    }

    public ArrayList<String> getRandomImageUrlsList() {
        return imageUrls;
    }

    public ArrayList<String> getRandomAuthorsList() {
        return authors;
    }

}
