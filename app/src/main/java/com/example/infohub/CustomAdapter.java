package com.example.infohub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_LONG;

public class CustomAdapter extends ArrayAdapter<ListViewDetails> {

    private Context mContext;
    int mResource;

    //Constructor will take in custom list and array of ListViewDetails objects
    public CustomAdapter(Context context, int resource, ArrayList<ListViewDetails> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    //responsible for getting the view and attaching it to the listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final DatabaseHelper helper = new DatabaseHelper(getContext());

        //For each listview object created, get the title and picture URL
        final String title = getItem(position).getTitle();
        final String picture = getItem(position).getPicture();
        final String link = getItem(position).getLink();

        //Inflate the view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        //Get the views from list_view_layout
        TextView articleTitle = (TextView) convertView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        Button button = (Button) convertView.findViewById(R.id.saveBtn);

        //Setup
        articleTitle.setText(title);
        Picasso.get().load(picture).resize(imageView.getWidth(),200).centerCrop().into(imageView);

        //Favorites
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helper.addFavorite(new ListViewDetails(title, picture, link))) {
                    Toast.makeText(getContext(), "Added to favorites!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Unexpected error, couldn't save", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

}
