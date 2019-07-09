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
    ListViewDetails listViewDetails;
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

                final Boolean[] result = {false};
                new AlertDialog.Builder(FavoritesActivity.this)
                        .setTitle("Delete?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result[0] = true;
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();

                if(result[0]=true) {
                    Cursor cursor2 = databaseHelper.getID(details.get(position));
                    Log.i("Delete", details.get(position).link);
                    Log.i("Delete", "Inside result=true");

                    int favoriteId = -1;
                    while (cursor2.moveToNext()) {
                        favoriteId = cursor2.getInt(0);
                        Log.i("Delete", "Inside cursor.movenext");
                    }
                    if (favoriteId > -1) {
                        Log.i("Delete", "inside favorideid>-1");
                        if (databaseHelper.delete(favoriteId) > 0) {
                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            Log.i("Delete", "It works!");
                            adapter.remove(details.get(position));
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                return true;
            }
        });

    }
}
