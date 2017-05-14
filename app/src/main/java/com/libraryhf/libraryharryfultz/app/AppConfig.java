package com.libraryhf.libraryharryfultz.app;


public class AppConfig {

    public static String BASE_URL = "http://librarycms.000webhostapp.com";

    public static String BASE_URL_POST = BASE_URL + "/mobile";

    public static String BASE_URL_GET = BASE_URL + "/api";

    public static String BASE_USER_URL = BASE_URL_GET + "/user";

    public static String URL_LOGIN = BASE_URL_POST + "/library/login";

    public static String URL_FETCH_CATEGORIES = BASE_URL_GET + "/all/categories";

    public static String URL_FETCH_BOOKS = BASE_URL_GET + "/all/books";

    public static String URL_FETCH_AUTHORS = BASE_URL_GET + "/all/authors";

    public static String URL_CHANGE_PASSWORD = BASE_URL_POST + "/password/update";

    public static String IMAGE_BASE_URL = BASE_URL + "/files/books/";

    public static String PROFILE_IMAGE_URL =  BASE_URL + "/files/profile/";

}
