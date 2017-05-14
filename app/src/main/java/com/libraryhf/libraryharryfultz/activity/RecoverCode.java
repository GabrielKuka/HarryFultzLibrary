package com.libraryhf.libraryharryfultz.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;


public class RecoverCode extends AppCompatActivity implements View.OnClickListener {

    private EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recover_code_activity);
        ChangeStatusBarColor.changeColor(this);
        initializeViews();
    }

    private void initializeViews() {
        code = (EditText) findViewById(R.id.codeRecoverTextFieldId);
        Button recoverButton = (Button) findViewById(R.id.codeRecoverButtonId);
        recoverButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (new ConnectivityState(this).isConnected()) {
            String codeVal = code.getText().toString();

            if (codeVal.isEmpty()) {
                MyDynamicToast.warningMessage(AppController.getInstance(), "Plotësoni fushën e lënë bosh.");
            } else {
                checkRecoveryCode(codeVal);
            }

        } else {
            MyDynamicToast.warningMessage(AppController.getInstance(), "Nuk jeni i lidhur me internet. Provoni përsëri.");
        }
    }

    private void checkRecoveryCode(final String code) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (code.equals(jsonObject.getString("code"))) {
                        MyDynamicToast.successMessage(AppController.getInstance(), "Kodi është i saktë.");
                    } else {
                        MyDynamicToast.informationMessage(AppController.getInstance(), "Kodi nuk është i saktë.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyDynamicToast.warningMessage(AppController.getInstance(), "Rikuperimi i llogarisë dështoi. Provoni përsëri.");
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
