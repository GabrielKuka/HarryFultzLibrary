package com.libraryhf.libraryharryfultz.activity.Fragments;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
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

import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.R;

public class Tab1Books extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> adapter;
    private ListViewCompat listViewCompat;
    private Dialog dialog;
    private TextView noData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.search_results_books_tab, container, false);

        Bundle b = getArguments();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, b.getStringArrayList("books"));
        adapter.notifyDataSetChanged();

        noData = (TextView) v.findViewById(R.id.noBooksTextViewId);

        listViewCompat = (ListViewCompat) v.findViewById(R.id.bookResulstId);
        try {
            listViewCompat.setAdapter(adapter);
            listViewCompat.setOnItemClickListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (b.getStringArrayList("books").size() == 0) {
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        new GetBookInfo(getActivity(), listViewCompat.getItemAtPosition(i).toString(), dialog).execute();
    }

    private void hideDialog() {
        if (dialog.isShowing())
            dialog.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideDialog();
    }
}
