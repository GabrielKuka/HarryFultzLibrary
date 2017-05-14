package com.libraryhf.libraryharryfultz.app.ListViews;


import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.libraryhf.libraryharryfultz.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookListViewAdapter extends ArrayAdapter<ListViewModel> {

    private ListViewModel[] modelItems;
    private AppCompatActivity ac;

    public BookListViewAdapter(AppCompatActivity a, ListViewModel[] resource) {
        super(a, R.layout.list_view_item, resource);
        this.ac = a;
        this.modelItems = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (ac).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_item, parent, false);

        TextView bookTitle = (TextView) convertView.findViewById(R.id.titleTextItemId);
        TextView bookAuthor = (TextView) convertView.findViewById(R.id.authorTextItemId);

        bookTitle.setTypeface(Typeface.createFromAsset(ac.getAssets(), "fonts/Roboto-Regular.ttf"));
        bookAuthor.setTypeface(Typeface.createFromAsset(ac.getAssets(), "fonts/Roboto-Light.ttf"));

        bookTitle.setText(modelItems[position].getTitle());
        bookAuthor.setText(modelItems[position].getAuthor());

        CircleImageView bookImage = (CircleImageView) convertView.findViewById(R.id.circleBookCoverId);
        animateCircleImageView(bookImage);
        Picasso.with(ac).load(modelItems[position].getImageUrl()).into(bookImage);

        return convertView;
    }

    private void animateCircleImageView(CircleImageView bookImage) {
        bookImage.setScaleX(0);
        bookImage.setScaleY(0);
        bookImage.animate().setDuration(900).scaleX(1).scaleY(1).start();
    }

}
