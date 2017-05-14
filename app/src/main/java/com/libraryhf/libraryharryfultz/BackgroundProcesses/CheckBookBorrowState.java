package com.libraryhf.libraryharryfultz.BackgroundProcesses;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.libraryhf.libraryharryfultz.activity.BookActivity;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CheckBookBorrowState extends AsyncTask<Void, Void, Void> {

    private AppCompatActivity a;
    private String userId, timeLeft = "", daysLeft = "";
    private boolean isBorrowed;

    public CheckBookBorrowState(AppCompatActivity ac, String userId) {
        this.a = ac;
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        isBorrowed = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        checkBorrowed();

        return null;
    }


    private void checkBorrowed() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_USER_URL + "/" + userId + "/borrows", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String idLookingFor = "" + jsonObject.getInt("book_id");
                            if (a.getIntent().getStringExtra("bookId").equals(idLookingFor)) {
                                timeLeft = jsonObject.getString("deadline");
                                Calendar calendar = Calendar.getInstance();
                                int monthDeadline = Integer.valueOf(timeLeft.substring(5, 7));
                                int leftDays = 0;
                                Log.d("Month", monthDeadline + "");
                                Log.d("MonthDifference", monthDeadline - (calendar.get(Calendar.MONTH) + 1) + "");

                                if (monthDeadline == (calendar.get(Calendar.MONTH) + 1)) {
                                    leftDays = Integer.valueOf(timeLeft.substring(timeLeft.lastIndexOf("-") + 1)) - calendar.get(Calendar.DAY_OF_MONTH);
                                } else if (monthDeadline - (calendar.get(Calendar.MONTH) + 1) == 1) {
                                    Calendar c = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                                    int daysFromMonthLeft = c.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
                                    leftDays = Integer.valueOf(timeLeft.substring(timeLeft.lastIndexOf("-") + 1)) + daysFromMonthLeft;
                                }

                                if (leftDays < 0) {
                                    daysLeft = "Afati i dorëzimit ka kaluar!";
                                } else if (leftDays == 0) {
                                    daysLeft = "Sot duhet të dorëzoni librin.";
                                } else {
                                    daysLeft = leftDays + " ditë të mbetura";
                                }

                                ((BookActivity) a).changeTextButton("Libri është i huazuar");
                                ((BookActivity) a).changeTimeLeftText("Afati i dorëzimit: " + timeLeft, daysLeft);

                                isBorrowed = true;
                                break;
                            } else {
                                isBorrowed = false;
                            }
                        }
                    }

                    checkRequests();

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

    private void checkRequests() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_USER_URL + "/" + userId + "/requests", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() >= 1) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String idLookingFor = "" + jsonObject.getInt("book_id");

                            int status = jsonObject.getInt("status");

                            if (!isBorrowed) {

                                ((BookActivity) a).changeTimeLeftText("", "");

                                if (!a.getIntent().getStringExtra("bookId").equals(idLookingFor)) {
                                    ((BookActivity) a).changeTextButton("Huazo këtë libër");
                                    Log.d("BookStatus", "Nothing");
                                }

                                if (a.getIntent().getStringExtra("bookId").equals(idLookingFor) && status == 0) {

                                    ((BookActivity) a).changeTextButton("Anullo kërkesën");
                                    break;
                                } else if (a.getIntent().getStringExtra("bookId").equals(idLookingFor) && status == 1) {

                                    ((BookActivity) a).changeTextButton("Kërkesa nuk është pranuar");
                                    break;
                                } else if (a.getIntent().getStringExtra("bookId").equals(idLookingFor) && status == 2) {

                                    ((BookActivity) a).changeTextButton("Kërkesa është aprovuar");
                                    break;
                                }
                            }
                        }

                    } else {
                        // nuk ka kerkesa
                        ((BookActivity) a).changeTextButton("Huazo këtë libër");
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
    }

}
