package com.example.infohub;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ListViewDetails> {

    private Context mContext;
    int mResource;

    //Constructor will take in custom list and array of ListViewDetails objects
    public CustomAdapter(Context context, int resource, ArrayList<ListViewDetails> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    //responsible for getting the view and attaching it to the listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //For each item downloaded, get the title and picture URL
        String title = getItem(position).getTitle();
        String picture = getItem(position).getPicture();

        //Initiate a new object for each set of title and picture
        ListViewDetails details = new ListViewDetails(title,picture);

        //Inflate the view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        //Get the views from list_view_layout
        TextView articleTitle = (TextView) convertView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

        //Setup
        articleTitle.setText(title);
        Picasso.get().load(picture).into(imageView);

        return convertView;
    }
}
