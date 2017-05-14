package com.libraryhf.libraryharryfultz.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.app.ListViews.BookListViewAdapter;
import com.libraryhf.libraryharryfultz.app.ListViews.ListViewModel;

public class Recommended extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListViewCompat listViewCompat;
    private Dialog dialog;
    private ListViewModel[] modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommended_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ChangeStatusBarColor.changeColor(this);

        modelItems = new ListViewModel[getIntent().getStringArrayListExtra("titles").size()];

        for (int i = 0; i < getIntent().getStringArrayListExtra("titles").size(); i++) {
            modelItems[i] = new ListViewModel(getIntent().getStringArrayListExtra("titles").get(i), getIntent().getStringArrayListExtra("authors").get(i), getIntent().getStringArrayListExtra("imageUrls").get(i));
        }

        BookListViewAdapter ad = new BookListViewAdapter(this, modelItems);

        ad.notifyDataSetChanged();

        listViewCompat = (ListViewCompat) findViewById(R.id.recommendedBookListId);
        listViewCompat.setAdapter(ad);
        listViewCompat.setOnItemClickListener(this);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        new GetBookInfo(this, modelItems[i].getTitle(), dialog).execute();
    }
}
