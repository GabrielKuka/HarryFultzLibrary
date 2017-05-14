package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.UserProfile;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.helper.UserData;

import org.json.JSONArray;
import org.json.JSONException;

public class GetUserBooks extends AsyncTask<Void, Void, Void> {

    private AppCompatActivity activity;
    private String userId, bookTypes;
    private String[] titles, authors, imageUrls;
    private int[] bookId, ids;
    private AppCompatTextView t;

    public GetUserBooks(AppCompatActivity a, UserData userData, int[] i, String[] t, String[] auth, String[] url, String bookTypes) {
        this.activity = a;
        this.userId = userData.getUserId();
        this.ids = i;
        this.titles = t;
        this.authors = auth;
        this.imageUrls = url;
        this.bookTypes = bookTypes;
    }


    @Override
    protected void onPreExecute() {
        t = (AppCompatTextView) activity.findViewById(R.id.noRequestsLabelId);
        ((UserProfile) activity).showDialog();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        switch (bookTypes) {
            case "requested":
                requestedBooks();
                break;
            case "borrowed":
                borrowedBooks();
                break;
            case "denied":
                deniedBooks();
                break;
            default:
                MyDynamicToast.warningMessage(AppController.getInstance(), "No book types found");
                break;
        }

        return null;
    }

    private void deniedBooks() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_USER_URL + "/" + userId + "/requests/denied", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        t.setVisibility(View.INVISIBLE);

                        int[] id = new int[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            id[i] = jsonArray.getJSONObject(i).getInt("book_id");
                        }

                        setBookIds(id);

                        for (int a = 0; a < ids.length; a++) {

                            for (int i = 0; i < getBookIds().length; i++) {
                                if (ids[a] == getBookIds()[i]) {
                                    String imageName = AppConfig.IMAGE_BASE_URL + imageUrls[a];
                                    ((UserProfile) activity).setItem(titles[a], authors[a], imageName);
                                    break;

                                }
                            }

                        }

                    } else {
                        t.setVisibility(View.VISIBLE);
                        t.setGravity(Gravity.CENTER_HORIZONTAL);
                        t.setText("Nuk ka kërkesa të mohuara");
                    }

                    ((UserProfile) activity).hideDialog();
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
    }

    private void requestedBooks() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_USER_URL + "/" + userId + "/requests/pending", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    boolean hasRequestedBooks = false;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getInt("status") == 0 || jsonArray.getJSONObject(i).getInt("status") == 2) {
                            hasRequestedBooks = true;
                            break;
                        }
                    }

                    if (hasRequestedBooks) {


                        t.setVisibility(View.INVISIBLE);

                        int[] id = new int[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            id[i] = jsonArray.getJSONObject(i).getInt("book_id");
                        }

                        setBookIds(id);


                        for (int a = 0; a < ids.length; a++) {

                            for (int i = 0; i < getBookIds().length; i++) {

                                if (ids[a] == getBookIds()[i]) {
                                    String imageName = AppConfig.IMAGE_BASE_URL + imageUrls[a];
                                    ((UserProfile) activity).setItem(titles[a], authors[a], imageName);
                                }
                            }

                        }

                    } else {
                        t.setVisibility(View.VISIBLE);
                        t.setGravity(Gravity.CENTER_HORIZONTAL);
                        t.setText("Nuk ka kërkesa");
                    }
                    ((UserProfile) activity).hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.errorMessage(AppController.getInstance(), "Volley did not respond!");
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void borrowedBooks() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_USER_URL + "/" + userId + "/borrows", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {

                        t.setVisibility(View.INVISIBLE);

                        int[] id = new int[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            id[i] = jsonArray.getJSONObject(i).getInt("book_id");
                        }

                        setBookIds(id);

                        for (int a = 0; a < ids.length; a++) {

                            for (int i = 0; i < getBookIds().length; i++) {
                                if (ids[a] == getBookIds()[i]) {
                                    String imageName = AppConfig.IMAGE_BASE_URL + imageUrls[a];
                                    ((UserProfile) activity).setItem(titles[a], authors[a], imageName);
                                    break;

                                }
                            }

                        }

                    } else {
                        t.setVisibility(View.VISIBLE);
                        t.setGravity(Gravity.CENTER_HORIZONTAL);
                        t.setText("Nuk ka huazime");
                    }

                    ((UserProfile) activity).hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSONArray Error:", "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.errorMessage(AppController.getInstance(), "Volley did not respond!");
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void setBookIds(int[] id) {
        bookId = new int[id.length];
        System.arraycopy(id, 0, bookId, 0, id.length);
        bubbleSortIds(bookId);
    }

    private int[] getBookIds() {
        return bookId;
    }

    private void bubbleSortIds(int[] arr) {

        int n = arr.length;
        int temp;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (arr[j - 1] > arr[j]) {

                    // swap elements
                    temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                }

            }
        }

    }
}