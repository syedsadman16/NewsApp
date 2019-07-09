package com.example.infohub;


public class ListViewDetails {
    String link;
    String title;
    String picture;

    public ListViewDetails(String title, String picture, String link){
        this.title = title;
        this.picture = picture;
        this.link = link;
    }


    public String getPicture() {
        return picture;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() { return link; }

}
