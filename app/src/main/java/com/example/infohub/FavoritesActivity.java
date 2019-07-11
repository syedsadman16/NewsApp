package com.example.infohub;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    ListView favoritesList;
    ProgressBar loading;
    CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesList = findViewById(R.id.favorites_list);
        loading = (ProgressBar) findViewById(R.id.loadingAnimation);

        final DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        final Cursor cursor = databaseHelper.getFavorites();
        final ArrayList<ListViewDetails> details = new ArrayList<>();

        //Load the objects with saved values
        if(!cursor.moveToNext()){
            Toast.makeText(getApplicationContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
        }
        while (cursor.moveToNext()){
            details.add(new ListViewDetails(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            ));

        }
        //Add articles to views
        adapter = new CustomAdapter(this, R.layout.list_view_layout, details);
        favoritesList.setAdapter(adapter);
        favoritesList.setEmptyView(loading);
        adapter.notifyDataSetChanged();

        //handle list view clicks
        favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = details.get(position).getLink();
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", link);
                startActivity(intent);
            }
        });

        //Deleting favorites
        favoritesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                final Boolean[] result = {false};
                new AlertDialog.Builder(FavoritesActivity.this)
                        .setTitle("Delete?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Retrieve the ID of the row by getting the news object at this position
                                final Cursor cursor2 = databaseHelper.getID(details.get(pos));

                                //By default if there is no column
                                int favoriteId = -1;
                                while (cursor2.moveToNext()) {
                                    //Refers to the first column that is being retrieved
                                    favoriteId = cursor2.getInt(0);
                                }
                                //If it retrieves an ID, then it will not be -1 so execute statements
                                if (favoriteId > -1) {
                                    //delete() will return # of rows deleted so if it does delete, execute block
                                    if (databaseHelper.delete(favoriteId) > 0) {
                                        //remove it from the actual list view
                                        adapter.remove(details.get(pos));
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();


                return true;
            }
        });

    }
}
