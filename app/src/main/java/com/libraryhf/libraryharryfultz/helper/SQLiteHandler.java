package com.libraryhf.libraryharryfultz.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_GENDER = "userGender";
    private static final String KEY_BIRTHDAY = "userBirthday";
    private static final String KEY_CLASS = "userClass";
    private static final String KEY_PROFILE_IMAGE = "profileImage";

    // Recent Books Table
    private static final String TABLE_RECENT_BOOKS = "recentBooks";

    // Recent Books table column names
    private static final String RECENT_TITLE = "title";
    private static final String RECENT_AUTHOR = "author";
    private static final String RECENT_IMAGE_URL = "imageUrl";


    // SQL queries
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_UID + " TEXT UNIQUE,"
            + KEY_GENDER + " TEXT,"
            + KEY_BIRTHDAY + " TEXT,"
            + KEY_CLASS + " TEXT,"
            + KEY_PROFILE_IMAGE + " TEXT);";

    private static final String DROP_LOGIN_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    private static final String CREATE_RECENT_TABLE = "CREATE TABLE " + TABLE_RECENT_BOOKS + "("
            + RECENT_TITLE + " TEXT,"
            + RECENT_AUTHOR + " TEXT,"
            + RECENT_IMAGE_URL + " TEXT);";
    private static final String DROP_RECENT_TABLE = "DROP TABLE IF EXISTS " + TABLE_RECENT_BOOKS;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_RECENT_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(DROP_LOGIN_TABLE);
        db.execSQL(DROP_RECENT_TABLE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, int userId, String gender, String birthday, String userClass, String profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_UID, userId);
        values.put(KEY_GENDER, gender);
        values.put(KEY_BIRTHDAY, birthday);
        values.put(KEY_CLASS, userClass);
        values.put(KEY_PROFILE_IMAGE, profileImage);

        long id = db.insert(TABLE_USER, null, values);
        db.close();
        Log.d(TAG, "New user inserted");
    }

    /**
     * Getting user data from database
     */
    HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("gender", cursor.getString(4));
            user.put("birthday", cursor.getString(5));
            user.put("userClass", cursor.getString(6));
            user.put("prfImage", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    /**
     * Re crate database Delete all tables and create them again
     */
    void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_RECENT_BOOKS, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void addRecentBook(String title, String author, String imageUrl) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(RECENT_TITLE, title);
        contentValues.put(RECENT_AUTHOR, author);
        contentValues.put(RECENT_IMAGE_URL, imageUrl);

        long id = db.insert(TABLE_RECENT_BOOKS, null, contentValues);
        db.close();
        Log.d("RecentBookStatus", title + " has been added.");
    }


    ArrayList<String> getRecentTitles() {

        ArrayList<String> recentTitles = new ArrayList<>();

        String selectQuery = "SELECT " + RECENT_TITLE + " FROM " + TABLE_RECENT_BOOKS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            if (i > 0) {
                c.moveToNext();
            }
            recentTitles.add(i, c.getString(c.getColumnIndex(RECENT_TITLE)));
        }
        c.close();
        db.close();

        return recentTitles;
    }

    ArrayList<String> getRecentAuthors() {
        ArrayList<String> recentAuthors = new ArrayList<>();
        String selectQuery = "SELECT " + RECENT_AUTHOR + " FROM " + TABLE_RECENT_BOOKS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            if (i > 0) {
                c.moveToNext();
            }
            recentAuthors.add(i, c.getString(c.getColumnIndex(RECENT_AUTHOR)));
        }

        c.close();
        db.close();

        return recentAuthors;

    }

    ArrayList<String> getRecentImageUrls() {
        ArrayList<String> recentUrls = new ArrayList<>();
        String selectQuery = "SELECT " + RECENT_IMAGE_URL + " FROM " + TABLE_RECENT_BOOKS;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            if (i > 0) {
                c.moveToNext();
            }
            recentUrls.add(i, c.getString(c.getColumnIndex(RECENT_IMAGE_URL)));
        }
        c.close();
        db.close();

        return recentUrls;
    }

    public boolean ifRecentBookExists(String title) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT " + RECENT_TITLE + " FROM " + TABLE_RECENT_BOOKS + " WHERE " + RECENT_TITLE + " = \"" + title + "\";";
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        if (c.getCount() <= 0) {
            c.close();
            return false;
        }
        c.close();
        db.close();
        return true;
    }

}