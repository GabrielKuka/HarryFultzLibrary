package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.activity.BookActivity;
import com.libraryhf.libraryharryfultz.activity.Dashboard;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchBooks extends AsyncTask<Void, Void, String[]> {

    private String[] books, authors, bookCovers;
    private int[] bookIds;
    private Dashboard d;
    private BookActivity b;

    public FetchBooks(AppCompatActivity a) {
        if (a instanceof Dashboard) {
            d = (Dashboard) a;
        } else if (a instanceof BookActivity) {
            b = (BookActivity) a;
        }

    }

    @Override
    protected String[] doInBackground(Void... params) {

        getBooksFromDB();

        return books;
    }

    private void getBooksFromDB() {

        StringRequest requestBooks = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_BOOKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String bookTitlesArray[] = new String[jsonArray.length()];
                    String bookAuthorsArray[] = new String[jsonArray.length()];
                    String bookCoversArray[] = new String[jsonArray.length()];

                    int bookIdsArray[] = new int[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        bookTitlesArray[i] = jsonObject.getString("title");
                        bookAuthorsArray[i] = jsonObject.getString("author");
                        bookCoversArray[i] = jsonObject.getString("cover");
                        bookIdsArray[i] = jsonObject.getInt("id");
                    }
                    setBooksList(bookTitlesArray, bookAuthorsArray, bookIdsArray, bookCoversArray);
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

    private void setBooksList(String[] bookTitlesArray, String[] au, int[] ids, String[] covers) {
        books = new String[bookTitlesArray.length];
        bookCovers = new String[covers.length];
        authors = new String[au.length];
        bookIds = new int[ids.length];

        System.arraycopy(bookTitlesArray, 0, books, 0, bookTitlesArray.length);
        System.arraycopy(au, 0, authors, 0, au.length);
        System.arraycopy(ids, 0, bookIds, 0, ids.length);
        System.arraycopy(covers, 0, bookCovers, 0, covers.length);

        if (d != null) {
            d.initializeSearchView();
        } else if (b != null) {
            b.initializeSearchView();
        }

    }

    public String[] getBookTitles() {
        return books;
    }

   public String[] getBookAuthors() {
        return authors;
    }

   public String[] getBookCovers() {
        return bookCovers;
    }

    public int[] getBookIds() {
        return bookIds;
    }

}