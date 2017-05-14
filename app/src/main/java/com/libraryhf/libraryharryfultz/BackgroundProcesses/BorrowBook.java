package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import java.util.HashMap;
import java.util.Map;

public class BorrowBook extends AsyncTask<Void, Void, Void> {

    private String book_Id, user_Id;
    private AppCompatActivity a;

    public BorrowBook(String bookId, String userId, AppCompatActivity a) {
        this.book_Id = bookId;
        this.user_Id = userId;
        this.a = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.BASE_URL_POST + "/book/" + book_Id + "/" + user_Id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyDynamicToast.successMessage(AppController.getInstance(), "Statusi u rifreskua.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.errorMessage(AppController.getInstance(), "Kërkesa dështoi. Provoni përsëri.");
                Log.d("Kerkesa Error:", "" + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_Id);
                params.put("book_id", book_Id);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest);

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        a.recreate();
    }
}