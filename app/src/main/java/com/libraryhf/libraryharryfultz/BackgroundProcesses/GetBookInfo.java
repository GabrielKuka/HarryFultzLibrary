package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.activity.BookActivity;
import com.libraryhf.libraryharryfultz.activity.Fragments.NewsFeed;
import com.libraryhf.libraryharryfultz.activity.UserProfile;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetBookInfo extends AsyncTask<Void, Void, Void> {

    private String bookTitle, bookAuthor, bookDescription, requestedBook, bookUrl, bookId, bookLanguage;
    private int copies;
    private AppCompatActivity a;
    private Dialog dialog;
    private FragmentActivity f;
    private Context c;

    public GetBookInfo(AppCompatActivity a, String requestedBook, Dialog d) {
        this.a = a;
        this.requestedBook = requestedBook;
        this.dialog = d;
    }

    public GetBookInfo(AppCompatActivity a, String requestedBook) {
        this.a = a;
        this.requestedBook = requestedBook;
    }

    public GetBookInfo(FragmentActivity f, String requestedBook, Dialog d) {
        this.f = f;
        this.requestedBook = requestedBook;
        this.dialog = d;
    }

    public GetBookInfo(Context c, String requestedBook) {
        this.requestedBook = requestedBook;
        this.c = c;
    }

    @Override
    protected void onPreExecute() {

        if (dialog != null)
            dialog.show();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        fetchBookInfo();
        return null;
    }

    private void fetchBookInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_BOOKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("title").equals(requestedBook)) {
                            bookId = "" + jsonObject.getInt("id");
                            bookTitle = jsonObject.getString("title");
                            bookAuthor = jsonObject.getString("author");
                            bookLanguage = jsonObject.getString("language");
                            bookDescription = jsonObject.getString("description");
                            copies = jsonObject.getInt("quantity");
                            setBookUrl(AppConfig.BASE_URL + "/files/books/" + jsonObject.getString("cover"));
                            Intent intent;
                            if (c == null) {
                                try {
                                    intent = new Intent(a, BookActivity.class);
                                    putExtrasIntoIntent(intent);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        NewsFeed.hideLoadingDialogFromBookActivity(dialog);
                                        UserProfile.hideLoadingDialogFromBookActivity(dialog);
                                    }
                                    if (a instanceof BookActivity)
                                        a.finish();

                                    a.startActivity(intent);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    intent = new Intent(f, BookActivity.class);
                                    putExtrasIntoIntent(intent);
                                    NewsFeed.hideLoadingDialogFromBookActivity(dialog);
                                    UserProfile.hideLoadingDialogFromBookActivity(dialog);

                                    f.startActivity(intent);
                                }
                            } else {

                                intent = new Intent(c, BookActivity.class);
                                putExtrasIntoIntent(intent);
                                NewsFeed.hideLoadingDialogFromBookActivity(dialog);
                                UserProfile.hideLoadingDialogFromBookActivity(dialog);

                                c.startActivity(intent);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Error: ", "" + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.errorMessage(AppController.getInstance(), "Volley Error!");
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void putExtrasIntoIntent(Intent intent) {
        intent.putExtra("title", getBookTitle());
        intent.putExtra("author", getBookAuthor());
        intent.putExtra("bookId", getBookId());
        intent.putExtra("description", getBookDescription());
        intent.putExtra("copies", getCopies());
        intent.putExtra("language", getBookLanguage());
        intent.putExtra("description", getBookDescription());
        intent.putExtra("bookUrl", getBookUrl());
    }

    private String getBookTitle() {
        return bookTitle;
    }

    private String getBookAuthor() {
        return bookAuthor;
    }

    private String getBookDescription() {
        return bookDescription;
    }

    private String getBookId() {
        return bookId;
    }

    private int getCopies() {
        return copies;
    }

    private void setBookUrl(String url) {
        bookUrl = url;
    }

    private String getBookUrl() {
        return bookUrl;
    }

    private String getBookLanguage() {
        return bookLanguage;
    }

}