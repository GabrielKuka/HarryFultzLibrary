package com.libraryhf.libraryharryfultz.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.helper.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends Activity {

    private EditText actualPw, newPw, newPwAgain;
    private TextView label;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);
        ChangeStatusBarColor.changeColor(this);
        initializeViews();
    }

    private void initializeViews() {

        label = (TextView) findViewById(R.id.changePasswordLabelId);
        label.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf"));

        actualPw = (EditText) findViewById(R.id.oldPwId);
        newPw = (EditText) findViewById(R.id.newPwId);
        newPwAgain = (EditText) findViewById(R.id.newPwAgainId);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);
        dismissDialog();
    }

    public void validatePassword(View view) {

        String oldPass = actualPw.getText().toString();
        String newPass = newPw.getText().toString();
        String newPassAgain = newPwAgain.getText().toString();

        if (oldPass.length() == 0 || newPass.length() == 0 || newPassAgain.length() == 0) {
            MyDynamicToast.warningMessage(AppController.getInstance(), "Fushat nuk janë plotësuar të gjitha.");
        } else if (!newPass.equals(newPassAgain)) {
            MyDynamicToast.warningMessage(AppController.getInstance(), "Fjalëkalimi i ri nuk përputhet!");
        } else {
            new ChangePw(oldPass, newPass, newPassAgain).execute();
        }
    }

    // Loading dialog methods

    private void showDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void dismissDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    private class ChangePw extends AsyncTask<Void, Void, Void> {

        private String oldPass, newPass, newPassAgain, userId;

        ChangePw(String old, String newP, String newPagain) {
            this.oldPass = old;
            this.newPass = newP;
            this.newPassAgain = newPagain;
            userId = new UserData(ChangePassword.this).getUserId();
            showDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CHANGE_PASSWORD, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean oldPassStatus = jsonObject.getBoolean("oldPassStatus");
                        if (!oldPassStatus) {
                            MyDynamicToast.errorMessage(AppController.getInstance(), "Fjalëkalimi i vjetër nuk është i saktë");
                        } else {
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {
                                MyDynamicToast.successMessage(AppController.getInstance(), "Fjalëkalimi u ndryshua me sukses.");
                                finish();
                            } else if (!status) {
                                MyDynamicToast.errorMessage(AppController.getInstance(), "Të dhënat nuk janë të sakta.");
                            }
                        }

                        dismissDialog();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dismissDialog();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("oldPassword", oldPass);
                    params.put("newPassword", newPass);
                    params.put("user_id", userId);

                    return params;
                }

            };


            AppController.getInstance().addToRequestQueue(stringRequest);
            return null;
        }

    }

}
