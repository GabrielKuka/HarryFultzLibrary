package com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetCategories;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetRandomBooks;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.ShowSpecificBooks;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.Fragments.NewsFeed;
import com.libraryhf.libraryharryfultz.activity.Recommended;
import com.libraryhf.libraryharryfultz.app.AppController;
import com.libraryhf.libraryharryfultz.helper.UserData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.Request.Method.GET;
import static com.libraryhf.libraryharryfultz.R.id.bookAuthorCardId;
import static com.libraryhf.libraryharryfultz.R.id.bookImageCardId;
import static com.libraryhf.libraryharryfultz.R.id.bookTitleCardId;
import static com.libraryhf.libraryharryfultz.app.AppConfig.URL_FETCH_CATEGORIES;


public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    private String[] categories;
    private AppCompatActivity a;
    private ArrayList<ProgramModel> program;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_RANDOM_BOOK_SECTION = 2;
    private static final int TYPE_RECENT_BOOK_SECTION = 3;
    private Fragment fragment;
    private boolean reload;
    private Dialog loadingDialog;
    private GetRandomBooks getRandomBooks;
    private int lastPosition = -1;
    private UserData userData;


    public NewsFeedAdapter(Context context, ArrayList<ProgramModel> program, AppCompatActivity ac, Fragment f, boolean r) {
        this.a = ac;
        this.context = context;
        this.program = program;
        this.fragment = f;
        this.reload = r;
        this.userData = new UserData(ac);
        getRandomBooks = new GetRandomBooks();
        getRandomBooks.execute();
        new GetNumberOfCategories().execute();
    }

    private class GetNumberOfCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            StringRequest stringRequest = new StringRequest(GET, URL_FETCH_CATEGORIES, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        categories = new String[jsonArray.length() + 1];
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
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder implements TapListener {

        ImageView bookImage;
        TextView bookTitle, bookAuthor;
        RelativeLayout parent_layout;
        String bookImageUrl, titulli, autori;
        Zoomy.Builder zoomer;

        MenuViewHolder(View itemView) {
            super(itemView);

            bookImage = (ImageView) itemView.findViewById(bookImageCardId);
            bookTitle = (TextView) itemView.findViewById(bookTitleCardId);
            bookAuthor = (TextView) itemView.findViewById(bookAuthorCardId);
            parent_layout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);

            loadingDialog = new Dialog(a);
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loadingDialog.setContentView(R.layout.loading_layout);
        }

        void setMenuDetail(ProgramModel model, final int position) {

            // Set the values
            bookTitle.setText(model.getTitle());
            bookAuthor.setText(model.getMessage());

            // Set text fonts
            bookTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf"));
            bookAuthor.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));

            titulli = model.getTitle();
            autori = model.getMessage();

            Picasso.with(context).load(model.getImageUrl()).into(bookImage);
            bookImageUrl = model.getImageUrl();

            zoomer = new Zoomy.Builder(a).target(bookImage);
            zoomer.tapListener(this);
            zoomer.register();

        }

        @Override
        public void onTap(View v) {
            switch (v.getId()) {
                case R.id.bookImageCardId:
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        new GetBookInfo(a, bookTitle.getText().toString(), loadingDialog).execute();
                    } else {
                        new GetBookInfo(a, bookTitle.getText().toString()).execute();
                    }
                    break;
            }
        }
    }

    public class ViewHeader extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout linearLayout;
        private CardView cardView;
        private TextView categoryTitle;

        ViewHeader(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutCategoryId);
            linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        void setHeaderDetail() {

            if (linearLayout.getChildCount() == 0) {
                addCardView(-1, "Të gjithë");
                GetCategories getCategories = new GetCategories(this);
                getCategories.execute();
            }
        }

        public void addCardView(int id, String title) {
            if (id != -1)
                categories[id] = title;

            cardView = new CardView(context);

            LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(450, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                cardViewLayoutParams.setMargins(8, 8, 8, 8);
                cardView.setContentPadding(35, 35, 35, 35);
            } else {
                cardView.setContentPadding(20, 20, 20, 20);
            }
            cardView.setLayoutParams(cardViewLayoutParams);
            cardView.setRadius(10);
            cardView.setClickable(true);
            cardView.setCardBackgroundColor(Color.parseColor("#23487c"));
            cardView.setOnClickListener(this);
            cardView.setId(View.generateViewId());
            cardView.setTag("" + id);
            cardView.setScaleX(0);
            cardView.setScaleY(0);
            cardView.animate().setDuration(600).scaleX(1).scaleY(1).start();

            categoryTitle = new TextView(context);

            LinearLayout.LayoutParams categoryTitleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            categoryTitle.setLayoutParams(categoryTitleLayoutParams);
            categoryTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            categoryTitle.setGravity(Gravity.CENTER);
            categoryTitle.setText(title);
            categoryTitle.setTextSize(20);
            categoryTitle.setTextColor(Color.parseColor("#FFFFFF"));
            categoryTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"));
            cardView.addView(categoryTitle);

            linearLayout.addView(cardView);
        }

        @Override
        public void onClick(View view) {


            if (view.getTag().equals("-1")) {
                fragment.getActivity().setTitle("Të gjithë");
                ((NewsFeed) fragment).fetchAllBooksByHeader();
            } else {
                try {
                    fragment.getActivity().setTitle(categories[Integer.valueOf(view.getTag().toString())]);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    MyDynamicToast.errorMessage(AppController.getInstance(), "Null");
                }
                new ShowSpecificBooks(view.getTag().toString(), fragment).execute();
            }
        }

    }

    private class RandomBookHeader extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private Button seeAllButton;

        RandomBookHeader(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutRandomBookId);
            seeAllButton = (Button) itemView.findViewById(R.id.seeAllRecommendedBooksId);
        }

        void setRandomBookHeaderDetail() {

            seeAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(a, Recommended.class);
                    i.putStringArrayListExtra("titles", getRandomBooks.getRandomTitlesList());
                    i.putStringArrayListExtra("authors", getRandomBooks.getRandomAuthorsList());
                    i.putStringArrayListExtra("imageUrls", getRandomBooks.getRandomImageUrlsList());
                    a.startActivity(i);
                }
            });

            if (linearLayout != null && linearLayout.getChildCount() == 0) {
                for (int i = 0; i < getRandomBooks.getRandomTitlesList().size(); i++) {
                    try {
                        addRandomBook(getRandomBooks.getRandomTitlesList().get(i), getRandomBooks.getRandomAuthorsList().get(i), getRandomBooks.getRandomImageUrlsList().get(i));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        void addRandomBook(String randomTitle, String randomAuthor, String randomUrl) {
            LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(480, 600);
            LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(250, 250);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final CardView cardView = new CardView(a);
            cardViewLayoutParams.setMarginEnd(35);
            cardViewLayoutParams.topMargin = 20;
            cardView.setLayoutParams(cardViewLayoutParams);
            cardView.setContentPadding(0, 0, 0, 0);
            cardView.setTag("" + randomTitle);
            cardView.setScaleX(0);
            cardView.animate().setDuration(900).scaleX(1).start();
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetBookInfo(a, cardView.getTag().toString(), loadingDialog).execute();
                }
            });


            LinearLayout innerCardLayout = new LinearLayout(a);
            innerCardLayout.setWeightSum(7);
            innerCardLayout.setLayoutParams(innerLayoutParams);
            innerCardLayout.setOrientation(LinearLayout.VERTICAL);
            innerCardLayout.setPadding(5, 0, 5, 5);
            innerCardLayout.setGravity(Gravity.CENTER);

            CircleImageView circleImageView = new CircleImageView(a);
            imgParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            imgParams.weight = 2.0f;
            imgParams.bottomMargin = 20;
            circleImageView.setLayoutParams(imgParams);
            Picasso.with(context).load(randomUrl).into(circleImageView);

            TextView title = new TextView(a);
            titleParams.gravity = Gravity.CENTER_HORIZONTAL;
            titleParams.weight = 5.0f;
            title.setLayoutParams(titleParams);
            title.setText(randomTitle);
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            innerCardLayout.addView(circleImageView);
            innerCardLayout.addView(title);

            cardView.addView(innerCardLayout);
            linearLayout.addView(cardView);
        }

    }

    private class RecentBookSection extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private Button seeAllButton;

        RecentBookSection(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutRecentBookId);
        }

        void setRecentBookSectionDetail() {

            if (linearLayout.getChildCount() == 0) {
                for (int i = 0; i < userData.getRecentTitles().size(); i++) {
                    try {
                        addRecentBook(userData.getRecentTitles().get(i), userData.getRecentAuthors().get(i), userData.getRecentImageUrls().get(i));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        void addRecentBook(String title, String author, String imageUrl) {
            LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(480, 600);
            LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(250, 250);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final CardView cardView = new CardView(a);
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
                    new GetBookInfo(a, cardView.getTag().toString(), loadingDialog).execute();
                }
            });


            LinearLayout innerCardLayout = new LinearLayout(a);
            innerCardLayout.setWeightSum(7);
            innerCardLayout.setLayoutParams(innerLayoutParams);
            innerCardLayout.setOrientation(LinearLayout.VERTICAL);
            innerCardLayout.setPadding(5, 0, 5, 5);
            innerCardLayout.setGravity(Gravity.CENTER);

            CircleImageView circleImageView = new CircleImageView(a);
            imgParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            imgParams.weight = 2.0f;
            imgParams.bottomMargin = 20;
            circleImageView.setLayoutParams(imgParams);
            Picasso.with(context).load(imageUrl).into(circleImageView);

            TextView bookTitle = new TextView(a);
            titleParams.gravity = Gravity.CENTER_HORIZONTAL;
            titleParams.weight = 5.0f;
            bookTitle.setLayoutParams(titleParams);
            bookTitle.setText(title);
            bookTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            innerCardLayout.addView(circleImageView);
            innerCardLayout.addView(bookTitle);

            cardView.addView(innerCardLayout);
            linearLayout.addView(cardView);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_RECENT_BOOK_SECTION) {
            View v = LayoutInflater.from(context).inflate(R.layout.dashboard_recent_book_header, parent, false);
            return new RecentBookSection(v);
        } else if (viewType == TYPE_RANDOM_BOOK_SECTION) {
            View v = LayoutInflater.from(context).inflate(R.layout.dashboard_random_book_header, parent, false);
            return new RandomBookHeader(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(context).inflate(R.layout.newsfeed_item, parent, false);
            reload = true;
            return new MenuViewHolder(v);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.dashboard_header, parent, false);
            return new ViewHeader(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case TYPE_RECENT_BOOK_SECTION:
                ((RecentBookSection) holder).setRecentBookSectionDetail();
                break;
            case TYPE_HEADER:
                ((ViewHeader) holder).setHeaderDetail();
                break;
            case TYPE_RANDOM_BOOK_SECTION:
                ((RandomBookHeader) holder).setRandomBookHeaderDetail();
                break;
            default:
                ProgramModel menuModel = program.get(position);
                setScaleAnimation(holder.itemView, position);
                ((MenuViewHolder) holder).setMenuDetail(menuModel, position);
                break;
        }

    }


    @Override
    public int getItemCount() {
        return program.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (userData.getRecentTitles().size() < 3) {
            if (position == 3) {
                return TYPE_RANDOM_BOOK_SECTION;
            } else if (position == 0) {
                return TYPE_HEADER;
            } else {
                return TYPE_ITEM;
            }

        } else {
            if (position == 3) {
                return TYPE_RANDOM_BOOK_SECTION;
            } else if (position == 2) {
                return TYPE_RECENT_BOOK_SECTION;
            } else if (position == 0) {
                return TYPE_HEADER;
            } else {
                return TYPE_ITEM;
            }
        }
    }

    private void setScaleAnimation(View view, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, view.getWidth() / 2, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

}
