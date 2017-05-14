package com.libraryhf.libraryharryfultz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.helper.ConnectivityState;
import com.libraryhf.libraryharryfultz.helper.SQLiteHandler;
import com.libraryhf.libraryharryfultz.helper.SessionManager;
import com.tapadoo.alerter.Alerter;
import com.vlad1m1r.lemniscate.BernoullisProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {

    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager session;
    private SQLiteHandler db;
    private Activity a;
    private BernoullisProgressView bP;
    private Button btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        a = this;

        ChangeStatusBarColor.changeColor(this);

        Button recoverButton = (Button) findViewById(R.id.recoverButtonActivity);
        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RecoverPassword.class));
            }
        });

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Set the email in email field if it already exists in phone memory
        SharedPreferences sharedPreferences = getSharedPreferences("UserEmail", Context.MODE_PRIVATE);
        inputEmail.setText(sharedPreferences.getString("email", ""));

        // ProgressBar
        bP = (BernoullisProgressView) findViewById(R.id.progressBar);
        bP.setVisibility(View.INVISIBLE);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (new ConnectivityState(a).isConnected()) {
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

                    // Check for empty data in the form
                    if (!email.isEmpty() && !password.isEmpty()) {
                        // login user
                        new LogInUser().execute(email, password);
                    } else {
                        // Prompt user to enter credentials
                        Alerter.create(a)
                                .enableIconPulse(true)
                                .setText("Ju lutem plotësoni fushat!")
                                .setDuration(1000)
                                .setBackgroundColor(R.color.colorAccent)
                                .show();
                    }

                } else {
                    MyDynamicToast.warningMessage(a, "Nuk jeni lidhur me internet.");
                }


            }

        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if (bP != null) {
            bP.setVisibility(View.GONE);
            bP.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bP != null) {
            bP.setVisibility(View.GONE);
            bP.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        }
    }

    private class LogInUser extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            // Show progess bar and hide some views
            inputEmail.setVisibility(View.INVISIBLE);
            inputPassword.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            bP.setVisibility(View.VISIBLE);
            bP.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        }

        @Override
        protected Void doInBackground(final String... p) {

            StringRequest strReq = new StringRequest(Method.POST,
                    AppConfig.URL_LOGIN, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Login Response: " + response);

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {
                            session.setLogin(true); // Create login session
                            try {
                                Log.d("ProfileImage",  jObj.getString("image"));
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            // Now store the user in SQLite
                            int userId = jObj.getInt("id");
                            String fullName = jObj.getString("name") + " " + jObj.getString("surname");
                            String email = jObj.getString("email");
                            String birthday = jObj.getString("birthday");
                            String studentClass = jObj.getString("class");
                            String prfImage = AppConfig.PROFILE_IMAGE_URL + jObj.getString("image");

                            String gender;
                            if (jObj.getString("gender").equals("M")) {
                                gender = "Mashkull";
                            } else {
                                gender = "Femer";
                            }


                            // Store email in phone memory
                            SharedPreferences sharedPreferences = getSharedPreferences("UserEmail", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", email);
                            editor.apply();


                            db.addUser(fullName, email, userId, gender, birthday, studentClass, prfImage);  // Inserting row in users table
                            Intent intent = new Intent(LoginActivity.this,
                                    Dashboard.class);  // Launch dashboard activity
                            startActivity(intent);    // Trigger dashboard activity
                            finish();                   // Finish this activity
                        } else {
                            // Error in login. Get the error message
                            MyDynamicToast.errorMessage(AppController.getInstance(), "Të dhënat e futura nuk janë të sakta!");
                            bP.setVisibility(View.INVISIBLE);
                            bP.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                            inputEmail.setVisibility(View.VISIBLE);
                            inputPassword.setVisibility(View.VISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("email", p[0]);
                    params.put("password", p[1]);

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq);

            return null;
        }
    }
}