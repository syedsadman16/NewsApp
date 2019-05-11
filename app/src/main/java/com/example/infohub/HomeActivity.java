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
* Fix lag
* Weather, stocks
* Allow users to save articles
* Fix image render on CategoryActivity
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog progressDialog;
    Context ctx = this;

    SQLiteDatabase database;
    ListView trendingList;
    ArrayList<String> homeStories = new ArrayList<>();
    ArrayList<String> homeLinks = new ArrayList<>();
    ArrayList<String> homeSummaries = new ArrayList<>();
    ArrayAdapter adapter;
    BackgroundTask task;
    String result = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       //When setting up databases
       // database = this.openOrCreateDatabase("NewsDB", MODE_PRIVATE, null);
       // database.execSQL("CREATE TABLE IF NOT EXISTS trending (name VARCHAR, address VARCHAR, id INTEGER PRIMARY KEY)");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        trendingList = findViewById(R.id.trendingList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, homeStories);
        trendingList.setAdapter(adapter);


        task = new BackgroundTask();
        try {
            task.execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();
            //updateWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Update listView
        adapter.notifyDataSetChanged();

        trendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", homeLinks.get(position));
                startActivity(intent);
            }
        });

        trendingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                try {

                    homeSummaries.get(position);

                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Summary")
                            .setMessage(homeSummaries.get(position))
                            .setPositiveButton("Close", null).show();

                } catch (Exception e) {

                    // database.execSQL("INSERT INTO trending (name, address) VALUES ('" + title + "','" + address + "')");

                    String link = homeLinks.get(position);
                    task.downloadSummary(link);
                    adapter.notifyDataSetChanged();

                }
                return true;
            }
        });


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent cat = new Intent(getApplicationContext(), CategoryActivity.class);
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

public class  BackgroundTask extends AsyncTask<String, Void, String> {

    String title;
    String address = "";
   // ProgressDialog progress = new ProgressDialog(HomeActivity.this);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground (String...urls){
        URL url;
        HttpURLConnection connection;
        String result = "";

        try {

            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
           // connection.setRequestMethod("GET");
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char c = (char) data;
                result += c;
                data = reader.read();
            }

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("articles");

                for (int i = 0; i < jsonArray.length() / 2; i++) {

                    JSONObject content = jsonArray.getJSONObject(i);
                    if (!content.isNull("url")) {

                        title = content.getString("title");
                        address = content.getString("url");
                        Log.i("Title", title);

                        homeStories.add(title);
                        homeLinks.add(address);

                    }
                    adapter.notifyDataSetChanged();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "ARTICLE NOT FOUND");
            return null;
        }

    }


    public boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public void downloadSummary(String address){


        URL url;
        HttpURLConnection connection;
        int data = 0;
        String summ = "";

        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);


        try {

            if (exists("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address)) {

                progressDialog.show();

                url = new URL("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address);
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream in = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    data = reader.read();

                    while (data != -1) {
                        char c = (char) data;
                        summ += c;
                        data = reader.read();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

                if (summ != null) {
                    JSONArray jArray = new JSONArray(summ);
                    JSONObject jObject = null;

                    String s1 = "";
                    String s2 = "";

                    jObject = jArray.getJSONObject(0);
                    s1 = jObject.getString("summary");

                    JSONArray j2Array = new JSONArray(s1);

                    for (int j = 0; j < j2Array.length(); j++) {
                        JSONObject object2 = j2Array.getJSONObject(j);
                        s2 += object2.getString("sentence");
                    }

                    homeSummaries.add(s2);
                    adapter.notifyDataSetChanged();
                }

            }

            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}

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



