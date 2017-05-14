package com.libraryhf.libraryharryfultz.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.hsalf.smilerating.SmileRating;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.BorrowBook;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.CheckBookBorrowState;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.DownloadImageBook;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.FetchBooks;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetAuthors;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetSimilarBooks;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.app.AppConfig;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.app.ChangeStatusBarColor;
import com.libraryhf.libraryharryfultz.helper.ConnectivityState;
import com.libraryhf.libraryharryfultz.helper.SQLiteHandler;
import com.libraryhf.libraryharryfultz.helper.UserData;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;
import br.com.mauker.materialsearchview.MaterialSearchView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BookActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, MaterialSearchView.OnQueryTextListener, View.OnClickListener, SmileRating.OnRatingSelectedListener {

    private MaterialSearchView materialSearchView;
    private FetchBooks fetchBooks;
    private ExpandableTextView bookDescription;
    public Button borrowButton;
    private UserData userData;
    private String userId;
    private Dialog dialog;
    private GetAuthors getAuthors;
    private TextView timeLeftText, daysLeftText;
    private CardView timeLeftCardView;
    private SmileRating smileRating;
    private SQLiteHandler db;

    /**
     * Activity methods
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        toolbarLayout.setTitle(getIntent().getStringExtra("title"));

        ChangeStatusBarColor.changeColor(this);

        initializeDatabaseActions();

        initializeExtraViews();

        initializeBookInfo();

        initializeBackgroundTasks();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (new CheckBookBorrowState(this, userId).isCancelled()) {
            new CheckBookBorrowState(this, userId).execute(); // <= Kontrollon nëse është bërë kërkesë për librin ose jo
        }
        if (fetchBooks.isCancelled()) {
            fetchBooks.execute();
        }

        if (dialog != null)
            dialog.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        if (materialSearchView != null && !materialSearchView.isOpen()) {
            this.finish();
        } else if (materialSearchView != null) {
            materialSearchView.closeSearch();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:

                if (materialSearchView != null) materialSearchView.openSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public AppCompatActivity getActivity() {
        return this;
    }

    /**
     * Initial tasks to run the activity
     */

    private void initializeBookInfo() {
        TextView bookTitle = (TextView) findViewById(R.id.bookTitleId);
        TextView bookAuthor = (TextView) findViewById(R.id.bookAuthorId);
        TextView bookCopies = (TextView) findViewById(R.id.bookCopiesId);
        TextView bookLanguage = (TextView) findViewById(R.id.bookLanguageId);
        timeLeftCardView = (CardView) findViewById(R.id.timeLeftCardViewId);
        timeLeftText = (TextView) findViewById(R.id.timeLeftTextId);
        daysLeftText = (TextView) findViewById(R.id.daysLeftId);


        bookDescription = (ExpandableTextView) findViewById(R.id.bookDescriptionId);
        bookDescription.setInterpolator(new OvershootInterpolator());
        bookDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookDescription.isExpanded())
                    bookDescription.collapse();
                else
                    bookDescription.expand();
            }
        });

        bookTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf"));
        bookAuthor.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        bookLanguage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        timeLeftText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        bookDescription.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
        daysLeftText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

        bookTitle.setText(getIntent().getStringExtra("title"));
        bookAuthor.setText("Autori: " + getIntent().getStringExtra("author"));
        bookLanguage.setText("Gjuha: " + getIntent().getStringExtra("language"));
        bookDescription.setText(getIntent().getStringExtra("description"));
        bookCopies.setText("Kopje të lira: " + getIntent().getIntExtra("copies", 0));

    }

    private void initializeExtraViews() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading_layout);

        borrowButton = (Button) findViewById(R.id.borrowButtonId);
        borrowButton.setOnClickListener(this);

        ImageView bookCover = (ImageView) findViewById(R.id.bookCoverId);
        new DownloadImageBook(bookCover, this)
                .execute(getIntent().getStringExtra("bookUrl"));

        smileRating = (SmileRating) findViewById(R.id.smile_rating);
        smileRating.setOnRatingSelectedListener(this);
    }

    private void initializeDatabaseActions() {
        db = new SQLiteHandler(getApplicationContext());
        userData = new UserData(this);
        userId = userData.getUserId();
        addRecentBookToMemory();
    }

    private void initializeBackgroundTasks() {
        GetBookCategory getBookCategory = new GetBookCategory();
        getBookCategory.execute();

        new CheckBookBorrowState(this, userId).execute(); // <= Kontrollon nëse është bërë kërkesë për librin ose jo

        fetchBooks = new FetchBooks(this);
        fetchBooks.execute();

        getAuthors = new GetAuthors();
        getAuthors.execute();
    }


    /**
     * SearchView methods
     */

    public void initializeSearchView() {
        materialSearchView = (MaterialSearchView) findViewById(R.id.searchView);
        materialSearchView.addSuggestions(fetchBooks.getBookTitles());
        materialSearchView.setOnItemClickListener(this);
        materialSearchView.setOnQueryTextListener(this);
        materialSearchView.adjustTintAlpha((float) 0.9);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        materialSearchView.closeSearch();

        if (new ConnectivityState(this).isConnected()) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                GetBookInfo getBookInfo = new GetBookInfo(getActivity(), materialSearchView.getSuggestionAtPosition(i), dialog);
                getBookInfo.execute();
            } else {
                GetBookInfo getBookInfo = new GetBookInfo(getActivity(), materialSearchView.getSuggestionAtPosition(i));
                getBookInfo.execute();
            }
        } else {
            MyDynamicToast.errorMessage(AppController.getInstance(), "Nuk jeni i lidhur me internet");
        }

    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        materialSearchView.closeSearch();

        if (new ConnectivityState(this).isConnected()) {


            int bookNumbers = fetchBooks.getBookTitles().length;
            int authorNumbers = getAuthors.getBookAuthors().length;

            boolean emptyBookArray = true;
            boolean emptyAuthorArray = true;

            ArrayList<String> resultBookArray = new ArrayList<>();
            ArrayList<String> resultAuthorArray = new ArrayList<>();

            Intent resultIntent = new Intent(this, SearchResults.class);


            for (int i = 0; i < bookNumbers; i++) {
                if (fetchBooks.getBookTitles()[i].contains(s)) {
                    emptyBookArray = false;
                    resultBookArray.add(fetchBooks.getBookTitles()[i]);
                }
            }

            for (int i = 0; i < authorNumbers; i++) {
                if (getAuthors.getBookAuthors()[i].contains(s)) {
                    emptyAuthorArray = false;
                    resultAuthorArray.add(getAuthors.getBookAuthors()[i]);
                }
            }

            if (!emptyBookArray || !emptyAuthorArray) {

                Bundle b = new Bundle();

                b.putStringArrayList("booksResults", resultBookArray);
                b.putStringArrayList("authorsResults", resultAuthorArray);

                resultIntent.putExtras(b);
                startActivity(resultIntent);


            } else {
                Alerter.create(this)
                        .setBackgroundColor(R.color.colorAccent)
                        .setText("Nuk ka rezultate")
                        .enableIconPulse(true)
                        .setDuration(1000)
                        .show();
            }
        } else {
            MyDynamicToast.errorMessage(AppController.getInstance(), "Nuk jeni i lidhur me internet");
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        return false;
    }


    /**
     * Extra section methods
     */

    private void addRecentBookToMemory() {
        if (!db.ifRecentBookExists(getIntent().getStringExtra("title")))
            db.addRecentBook(getIntent().getStringExtra("title"), getIntent().getStringExtra("author"), getIntent().getStringExtra("bookUrl"));
    }

    public void addSimilarBooksSection(ArrayList<String> titles, ArrayList<String> authors, ArrayList<String> imageUrls) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutSimilarBookId);

        if (linearLayout.getChildCount() == 0) {
            for (int i = 0; i < titles.size(); i++) {
                addSimilarBook(linearLayout, titles.get(i), authors.get(i), imageUrls.get(i));
            }
        }

    }

    private void addSimilarBook(LinearLayout linearLayout, String title, String author, String imageUrl) {

        final Dialog loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setContentView(R.layout.loading_layout);

        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(480, 600);
        LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(250, 250);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final CardView cardView = new CardView(this);
        cardViewLayoutParams.setMarginEnd(35);
        cardViewLayoutParams.topMargin = 20;
        cardView.setLayoutParams(cardViewLayoutParams);
        cardView.setContentPadding(0, 0, 0, 0);
        cardView.setTag("" + title);
        cardView.setScaleX(0);
        cardView.animate().setDuration(900).scaleX(1).start();
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetBookInfo(getActivity(), cardView.getTag().toString(), loadingDialog).execute();
            }
        });


        LinearLayout innerCardLayout = new LinearLayout(this);
        innerCardLayout.setWeightSum(7);
        innerCardLayout.setLayoutParams(innerLayoutParams);
        innerCardLayout.setOrientation(LinearLayout.VERTICAL);
        innerCardLayout.setPadding(5, 0, 5, 5);
        innerCardLayout.setGravity(Gravity.CENTER);

        CircleImageView circleImageView = new CircleImageView(this);
        imgParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        imgParams.weight = 2.0f;
        imgParams.bottomMargin = 20;
        circleImageView.setLayoutParams(imgParams);
        Picasso.with(getApplicationContext()).load(imageUrl).into(circleImageView);

        TextView cardTitle = new TextView(this);
        titleParams.gravity = Gravity.CENTER_HORIZONTAL;
        titleParams.weight = 5.0f;
        cardTitle.setLayoutParams(titleParams);
        cardTitle.setText(title);
        cardTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        innerCardLayout.addView(circleImageView);
        innerCardLayout.addView(cardTitle);

        cardView.addView(innerCardLayout);
        linearLayout.addView(cardView);
    }


    /**
     * Other methods
     */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.borrowButtonId:

                if (getIntent().getIntExtra("copies", 0) == 0) {
                    // Nuk ka kopje te lira
                    Alerter.create(this)
                            .setText("Nuk ka kopje të lira.")
                            .setDuration(1000)
                            .show();
                } else {
                    borrowBook();
                }
                break;
        }
    }

    public void changeTextButton(final String text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                borrowButton.setText(text);
            }
        });
    }

    public void changeTimeLeftText(final String timeLeft, final String daysLeft) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timeLeft.equals("")) {
                    timeLeftCardView.setVisibility(View.GONE);
                } else {
                    timeLeftCardView.setVisibility(View.VISIBLE);
                    timeLeftText.setText(timeLeft);
                    daysLeftText.setText(daysLeft);
                }


            }
        });
    }

    public void borrowBook() {

        String borrowButtonText = borrowButton.getText().toString();

        switch (borrowButtonText) {
            case "Huazo këtë libër":
                // dergo kerkese per huazim

                new LovelyStandardDialog(this)
                        .setButtonsColorRes(R.color.colorPrimary)
                        .setMessage("Huazo këtë libër?")
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new BorrowBook(getIntent().getStringExtra("bookId"), userId, getActivity()).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;

            case "Anullo kërkesën":
                // anullo kerkesen
                new LovelyStandardDialog(this)
                        .setButtonsColorRes(R.color.colorPrimary)
                        .setMessage("Anullo kërkesën për këtë libër?")
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new BorrowBook(getIntent().getStringExtra("bookId"), userId, getActivity()).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;

            case "Kërkesa nuk është pranuar":

//                // ridergo kerkesen
//                new LovelyStandardDialog(this)
//                        .setButtonsColorRes(R.color.colorPrimary)
//                        .setMessage(R.string.bookRequestResend)
//                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // dergo kerkese per huazim
//                                new BorrowBook(getIntent().getStringExtra("bookId"), userId, getActivity()).execute();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null)
//                        .show();

                break;
        }

    }

    @Override
    public void onRatingSelected(int level, boolean reselected) {
        if (!reselected) {
            switch (level) {
                case SmileRating.BAD:
                    MyDynamicToast.informationMessage(AppController.getInstance(), "BAD");
                    break;
                case SmileRating.GOOD:
                    MyDynamicToast.informationMessage(AppController.getInstance(), "GOOD");
                    break;
                case SmileRating.GREAT:
                    MyDynamicToast.informationMessage(AppController.getInstance(), "GREAT");
                    break;
                case SmileRating.OKAY:
                    MyDynamicToast.informationMessage(AppController.getInstance(), "OKAY");
                    break;
                case SmileRating.TERRIBLE:
                    MyDynamicToast.informationMessage(AppController.getInstance(), "TERRIBLE");
                    break;
            }
        }
    }

    private class GetBookCategory extends AsyncTask<Void, Void, Void> {

        private String categoryId = "" + -1;

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_FETCH_CATEGORIES, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            checkBookAtSpecificCategory(jsonArray.getJSONObject(i).getInt("id") + "");
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
            return null;
        }

        private void checkBookAtSpecificCategory(final String id) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.BASE_URL_GET + "/category/" + id + "/books", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String loopingTitles = jsonArray.getJSONObject(i).getString("title");
                            if (getIntent().getStringExtra("title").equals(loopingTitles)) {
                                categoryId = id;
                                GetSimilarBooks getSimilarBooks = new GetSimilarBooks(getActivity(), categoryId, getIntent().getStringExtra("bookId"));
                                getSimilarBooks.execute();

                            }
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
}
