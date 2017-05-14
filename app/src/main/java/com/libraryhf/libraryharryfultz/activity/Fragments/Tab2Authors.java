package com.libraryhf.libraryharryfultz.activity.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetAuthors;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.BookListFromAuthor;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Tab2Authors extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> adapter;
    private ListViewCompat listViewCompat;
    private Dialog dialog;
    private TextView noAuthorsText;
    private GetAuthors getAuthors;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.search_results_authors_tab, container, false);

        Bundle b = getArguments();

        getAuthors = new GetAuthors();
        getAuthors.execute();

        noAuthorsText = (TextView) v.findViewById(R.id.noAuthorsTextViewId);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, b.getStringArrayList("authors"));
        adapter.notifyDataSetChanged();

        listViewCompat = (ListViewCompat) v.findViewById(R.id.authorsResultId);
        try {
            listViewCompat.setAdapter(adapter);
            listViewCompat.setOnItemClickListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (b.getStringArrayList("authors").size() == 0) {
            noAuthorsText.setVisibility(View.VISIBLE);
        } else {
            noAuthorsText.setVisibility(View.GONE);
        }

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        getIdFromSpecificAuthor(listViewCompat.getItemAtPosition(i).toString());
    }

    private void getIdFromSpecificAuthor(final String name) {
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_AUTHORS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int id = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i).getString("name").equals(name)) {
                            id = jsonArray.getJSONObject(i).getInt("id");
                            break;
                        }
                    }

                    new GetBooksFromAuthor("" + id).execute();
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

    private void hideDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideDialog();
    }

    private class GetBooksFromAuthor extends AsyncTask<Void, Void, Void> {

        private String authorId;

        GetBooksFromAuthor(String authorId) {

            this.authorId = authorId;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://ec2-52-39-232-168.us-west-2.compute.amazonaws.com/api/author/" + authorId + "/books", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        String[] titles = new String[jsonArray.length()];
                        String author = jsonArray.getJSONObject(0).getString("author");
                        String[] imageUrls = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            titles[i] = jsonObject.getString("title");
                            imageUrls[i] = "http://ec2-52-39-232-168.us-west-2.compute.amazonaws.com/files/books/" + jsonObject.getString("cover");
                        }

                        Intent intent = new Intent(getActivity(), BookListFromAuthor.class);
                        intent.putExtra("titles", titles);
                        intent.putExtra("authors", author);
                        intent.putExtra("imageUrls", imageUrls);
                        startActivity(intent);

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

        @Override
        protected void onPostExecute(Void v) {
            hideDialog();
        }
    }


}
