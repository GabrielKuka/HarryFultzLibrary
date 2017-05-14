package com.libraryhf.libraryharryfultz.app;


import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.libraryhf.libraryharryfultz.BackgroundProcesses.GetBookInfo;
import com.libraryhf.libraryharryfultz.R;
import com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper.ProgramModel;
import com.libraryhf.libraryharryfultz.activity.UserProfile;
import com.libraryhf.libraryharryfultz.helper.UserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileNewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity activity;
    private ArrayList<ProgramModel> program;
    private LayoutInflater inflater;
    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;


    public ProfileNewsFeedAdapter(AppCompatActivity activity, ArrayList<ProgramModel> program) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.program = program;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements TapListener {

        // TextView title, author;
        ImageView bookImage;
        String bookImageUrl, titulli, autori;
        Dialog loadingDialog;
        Zoomy.Builder zoomer;

        MyViewHolder(View v) {
            super(v);
            bookImage = (ImageView) v.findViewById(R.id.bookImageBorrowCardId);

            loadingDialog = new Dialog(activity);
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loadingDialog.setContentView(R.layout.loading_layout);

        }

        void setMenuDetail(ProgramModel model, final int position) {

            titulli = model.getTitle();
            autori = model.getMessage();

            Picasso.with(activity).load(model.getImageUrl()).into(bookImage);
            bookImageUrl = model.getImageUrl();

            zoomer = new Zoomy.Builder(activity).target(bookImage);
            zoomer.tapListener(this);
            zoomer.register();

        }


        @Override
        public void onTap(View v) {
            switch (v.getId()) {
                case R.id.bookImageBorrowCardId:
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        new GetBookInfo(activity, titulli, loadingDialog).execute();
                    } else {
                        new GetBookInfo(activity, titulli).execute();
                    }
                    break;
            }
        }
    }

    private class ViewHeader extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView userEmail, birthday, userClass;
        private UserData userData;
        private Button requestsButton, borrowsButton, deniedButton;
        private CircleImageView prfImage;

        ViewHeader(View itemView) {
            super(itemView);
            userData = new UserData(activity);
            userEmail = (TextView) itemView.findViewById(R.id.emailPrfId);
            birthday = (TextView) itemView.findViewById(R.id.birthdayId);
            userClass = (TextView) itemView.findViewById(R.id.userClassId);

            requestsButton = (Button) itemView.findViewById(R.id.requestsButtonId);
            borrowsButton = (Button) itemView.findViewById(R.id.borrowsButtonId);
            deniedButton = (Button) itemView.findViewById(R.id.deniedButtonId);

            prfImage = (CircleImageView) itemView.findViewById(R.id.userImageId);
            Picasso.with(activity).load(userData.getUserProfileImage()).into(prfImage);

            requestsButton.setOnClickListener(this);
            borrowsButton.setOnClickListener(this);
            deniedButton.setOnClickListener(this);

            try {
                switch (UserProfile.getBookTypes()) {
                    case "requested":
                        requestsButton.setTypeface(Typeface.create(requestsButton.getTypeface(), Typeface.BOLD));
                        break;
                    case "borrowed":
                        borrowsButton.setTypeface(Typeface.create(borrowsButton.getTypeface(), Typeface.BOLD));
                        break;
                    case "denied":
                        deniedButton.setTypeface(Typeface.create(deniedButton.getTypeface(), Typeface.BOLD));
                        break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                requestsButton.setTypeface(Typeface.create(requestsButton.getTypeface(), Typeface.BOLD));
            }

        }

        void setHeaderDetail() {
            userEmail.setText("Email: " + userData.getEmail());
            birthday.setText("Ditëlindja: " + userData.getBirthday());
            userClass.setText("Klasa: " + userData.getUserClass());

            setFont(userEmail);
            setFont(birthday);
            setFont(userClass);

        }

        private void setFont(TextView textView) {
            textView.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Light.ttf"));
        }

        private void setButtonFont(Button b) {
            b.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf"));
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.requestsButtonId:
                    // get the requested books

                    requestsButton.setTypeface(Typeface.create(requestsButton.getTypeface(), Typeface.BOLD));
                    setButtonFont(borrowsButton);
                    setButtonFont(deniedButton);

                    UserProfile.setBookTypes("requested");
                    ((UserProfile) activity).checkTypeBooks("requested");
                    break;
                case R.id.borrowsButtonId:
                    // get the borrowed books

                    borrowsButton.setTypeface(Typeface.create(borrowsButton.getTypeface(), Typeface.BOLD));
                    setButtonFont(requestsButton);
                    setButtonFont(deniedButton);

                    UserProfile.setBookTypes("borrowed");
                    ((UserProfile) activity).checkTypeBooks("borrowed");
                    break;
                case R.id.deniedButtonId:
                    // get the denied books

                    deniedButton.setTypeface(Typeface.create(deniedButton.getTypeface(), Typeface.BOLD));
                    setButtonFont(requestsButton);
                    setButtonFont(borrowsButton);
                    UserProfile.setBookTypes("denied");
                    ((UserProfile) activity).checkTypeBooks("denied");
                    break;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View v = LayoutInflater.from(activity).inflate(R.layout.profile_cardview_item, parent, false);
            return new MyViewHolder(v);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.profile_newsfeed_header, parent, false);
            return new ViewHeader(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHeader) {
            ((ViewHeader) holder).setHeaderDetail();
        } else if (holder instanceof MyViewHolder) {
            ProgramModel menuModel = program.get(position);
            ((MyViewHolder) holder).setMenuDetail(menuModel, position);
        }
    }

    @Override
    public int getItemCount() {
        if (program.isEmpty()) {
            return program.size() + 1;
        } else {
            return program.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return HEADER_TYPE;

        return ITEM_TYPE;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


}
