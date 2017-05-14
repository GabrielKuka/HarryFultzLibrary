package com.libraryhf.libraryharryfultz.app.ListViews;


public class ListViewModel {

    private String title, author, imageUrl;

    public ListViewModel(String bookTitle, String bookAuthor, String imageUrl) {
        this.title = bookTitle;
        this.author = bookAuthor;
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
