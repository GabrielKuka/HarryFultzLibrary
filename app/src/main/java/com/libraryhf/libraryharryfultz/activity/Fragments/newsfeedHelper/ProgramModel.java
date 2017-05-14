package com.libraryhf.libraryharryfultz.activity.Fragments.newsfeedHelper;

public class ProgramModel {

    private String title;
    private String message;
    private int image;
    private String imageUrl;


    public ProgramModel(String title, String message, String imageUrl) {
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;

    }

    public ProgramModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
}
