package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.libraryhf.libraryharryfultz.activity.BookActivity;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

public class GetSimilarBooks extends AsyncTask<Void, Void, Void> {

    private ArrayList<String> titles, authors, imageUrls;
    private int categoryId, currentBookId;
    private AppCompatActivity a;

    public GetSimilarBooks(AppCompatActivity a, String categoryId, String currentBookId) {
        titles = new ArrayList<>();
        authors = new ArrayList<>();
        imageUrls = new ArrayList<>();
        this.categoryId = Integer.valueOf(categoryId);
        this.currentBookId = Integer.valueOf(currentBookId);
        this.a = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_URL_GET + "/category/" + categoryId + "/books", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    final Random randomBookNumber = new Random();
                    for (int i = 0; i < 5; i++) {
                        int randomBookId = randomBookNumber.nextInt(jsonArray.length());
                        if (!checkIfExists(jsonArray.getJSONObject(randomBookId).getString("title")) && jsonArray.getJSONObject(randomBookId).getInt("id") != currentBookId) {
                            titles.add(jsonArray.getJSONObject(randomBookId).getString("title"));
                            authors.add(jsonArray.getJSONObject(randomBookId).getString("author"));
                            imageUrls.add(AppConfig.IMAGE_BASE_URL + jsonArray.getJSONObject(randomBookId).getString("cover"));
                        }
                    }

                    if (titles.size() >= 1) {
                        try {
                            ((BookActivity) a).addSimilarBooksSection(getSimilarTitles(), getSimilarAuthors(), getSimilarImageUrls());
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
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

    private ArrayList<String> getSimilarTitles() {
        return this.titles;
    }

    private ArrayList<String> getSimilarAuthors() {
        return this.authors;
    }

    private ArrayList<String> getSimilarImageUrls() {
        return this.imageUrls;
    }

}
