package com.libraryhf.libraryharryfultz.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.Fragments.Tab1Books;
import com.libraryhf.libraryharryfultz.activity.Fragments.Tab2Authors;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ArrayList<String> authorsList, booksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChangeStatusBarColor.changeColor(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        Bundle b = this.getIntent().getExtras();
        try {
            authorsList = b.getStringArrayList("authorsResults");
            booksList = b.getStringArrayList("booksResults");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Bundle b = new Bundle();

            switch (position) {
                case 0:

                    Tab1Books tab1 = new Tab1Books();
                    b.putStringArrayList("books", booksList);
                    tab1.setArguments(b);

                    return tab1;

                case 1:
                    Tab2Authors tab2 = new Tab2Authors();
                    b.putStringArrayList("authors", authorsList);
                    tab2.setArguments(b);

                    return tab2;

                default:
                    return null;

            }
        }

        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Libra";
                case 1:
                    return "Autorë";
            }
            return null;
        }
    }
}
