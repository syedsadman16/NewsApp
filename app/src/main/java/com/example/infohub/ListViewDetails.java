package com.example.infohub;


public class ListViewDetails {
    String title;
    String picture;

    public ListViewDetails(String title, String picture){
        this.title = title;
        this.picture = picture;
    }


    public String getPicture() {
        return picture;
    }

    public String getTitle() {
        return title;
    }

}
