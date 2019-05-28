package com.example.infohub;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/* TODO
* Find a way to show dialog
* Implement new summary API
* Weather, stocks
* Allow users to save articles
* Fix image render on CategoryActivity
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SQLiteDatabase database;
    ListView trendingList;
    ArrayAdapter adapter;
    BackgroundTask task;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       //When setting up databases
       // database = this.openOrCreateDatabase("NewsDB", MODE_PRIVATE, null);
       // database.execSQL("CREATE TABLE IF NOT EXISTS trending (name VARCHAR, address VARCHAR, id INTEGER PRIMARY KEY)");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*This is dangerous, allows UI to be updated from background thread
        *Only purpose of this is to get summary of each article once user requests
        *Used for testing until I can find a better way
        */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Side panel
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Declare new instance of background class
        task = new BackgroundTask();

        try {
            //Json array name inside object
            task.jsonArrayName = "articles";
            //target array element
            task.jsonArrayValue = "title";
            //Do the background stuff
            task.execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();
            //Setup the listviews and adapter
            trendingList = findViewById(R.id.trendingList);
            //Set adapter to stories array in the background task
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, task.Stories);
            trendingList.setAdapter(adapter);
            //Update listView
            adapter.notifyDataSetChanged();
            //updateWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //handle list view clicks
        trendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", task.Links.get(position));
                startActivity(intent);
            }
        });

        //Long clicks for summaries
        trendingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {

                    task.Summaries.get(position);

                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Summary")
                            .setMessage(task.Summaries.get(position))
                            .setPositiveButton("Close", null).show();

                } catch (Exception e) {

                    // database.execSQL("INSERT INTO trending (name, address) VALUES ('" + title + "','" + address + "')");
                    String link = task.Links.get(position);
                    task.downloadSummary(link);
                    adapter.notifyDataSetChanged();

                }
                return true;
            }
        });

    }


    //For dropdown in title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Navigation side panel exit
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Side panel clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //id's are assigned in res/menu/activity_home_drawer.xml
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent cat = new Intent(getApplicationContext(), CategoryActivity.class);
            Log.i("Cat", "Clicked");
            task.cancel(true);
            startActivity(cat);
        }
        else if (id == R.id.sources) {
            Intent source = new Intent(getApplicationContext(), NewsSources.class);
            task.cancel(true);
            startActivity(source);
        }
        else if (id == R.id.home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
        else if (id == R.id.favorites) {
            Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

/*
    public void updateContent(){
        Cursor cursor = database.rawQuery("SELECT * FROM trending", null);

        //Pull from database
        int nameIndex = cursor.getColumnIndex("name");
        int addressIndex = cursor.getColumnIndex("address");

        if(cursor.moveToFirst()){
            homeStories.clear();
            homeLinks.clear();
            Log.i("name", cursor.getString(nameIndex));
            Log.i("address", cursor.getString(addressIndex));
        }

        if(cursor.getCount() > 0) {
            do {
                homeStories.add(cursor.getString(nameIndex));
                homeLinks.add(cursor.getString(addressIndex));
            } while (cursor.moveToNext());
        } else {
            Log.e("Cursor", "Cursor is Empty");
        }

        adapter.notifyDataSetChanged();
    }
*/

/*
**When enabling weather options into app
*
    public void updateWeather(){

    URL url;
    HttpURLConnection connection;
    String weatherData = "";
    try {
        //add url
        url = new URL("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22");
       // url = new URL("https://api.openweathermap.org/data/2.5/forecast?q=detroit&appid=1f02d456968e25491936d3c826d78b88");
        //open connection
        connection = (HttpURLConnection) url.openConnection();
        //get input stream and reader
        InputStream in = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(in);
        //set up read data
        int data = reader.read();
        //get all characters
        while(data != -1){
            char convert = (char) data;
            weatherData += convert;
            data = reader.read();
        }

            TextView text = findViewById(R.id.weathertest);
            //convert weatherData contents to json
            JSONObject weatherJSON = new JSONObject(weatherData);
            //locate "weather" object identify it as array from json
            JSONArray dataArray = weatherJSON.getJSONArray("weather");
            //Go into array and pull data
            for(int i = 0; i < dataArray.length(); i++) {
                //turn incoming data into json
                JSONObject incoming = dataArray.getJSONObject(i);
                //put values into variables
                String main = incoming.getString("main");
                text.setText(main);
                Log.i("Main:", main);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //Unknown city handler
            Toast.makeText(getApplicationContext(), "Undefined location. Try again", Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

}
*/


}



