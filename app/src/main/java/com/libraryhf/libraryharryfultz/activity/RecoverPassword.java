package com.libraryhf.libraryharryfultz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.helper.ConnectivityState;

import java.util.HashMap;
import java.util.Map;

public class RecoverPassword extends Activity implements View.OnClickListener {

    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recover_password_activity);
        initializeViews();
        ChangeStatusBarColor.changeColor(this);
    }

    private void initializeViews() {
        email = (EditText) findViewById(R.id.recoverTextFieldId);

        Button confirmButton = (Button) findViewById(R.id.recoverButtonId);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (new ConnectivityState(this).isConnected()) {
            String emailVal = email.getText().toString();

            if (emailVal.length() == 0) {
                MyDynamicToast.warningMessage(AppController.getInstance(), "Plotësoni fushën e lënë bosh");
            } else {
                sendRequest(emailVal);
            }
        } else {
            MyDynamicToast.warningMessage(AppController.getInstance(), "Nuk jeni i lidhur me internet. Provoni përsëri.");
        }

    }

    private void sendRequest(final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);

    }
}
