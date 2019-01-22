package com.example.infohub;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.infohub.MainActivity.ContentBackgroundTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* BUGS:
*
* Crashes when opening app with emulator - Hit RUN button
* MainActivity is broken, Only HomeActivity works
*
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        database = this.openOrCreateDatabase("NewsDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS trending (name VARCHAR, address VARCHAR, id INTEGER PRIMARY KEY)");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        trendingList = findViewById(R.id.trendingList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, homeStories);
        trendingList.setAdapter(adapter);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        task = new BackgroundTask();
        try {
            String s = task.execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
        // updateContent();

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

                    //
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Summary")
                            .setMessage(homeSummaries.get(position))
                            .setPositiveButton("Close", null).show();
                } catch (Exception e) {

                    ProgressDialog progress = new ProgressDialog(HomeActivity.this);
                    progress.setMessage("Downloaded summary " + "'" + homeStories.get(position) + "'." +  " Click anywhere to exit");
                    progress.show();

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

        if (id == R.id.Home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            task.cancel(true);
            startActivity(i);
        } else if (id == R.id.Buisness) {

        } else if (id == R.id.Technology) {

        } else if (id == R.id.Health) {

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

    @Override
    protected String doInBackground (String...urls){
        URL url;
        HttpURLConnection connection;
        String result = "";

        try {

            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char c = (char) data;
                result += c;
                data = reader.read();
            }

            Log.i("Result", result);



            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("articles");

                //  database.execSQL("DELETE FROM trending");

                //inside this array articles we have many objects containing string author
                for (int i = 0; i < jsonArray.length() / 2; i++) {
                    JSONObject content = jsonArray.getJSONObject(i);
                    if (!content.isNull("url")) {
                        title = content.getString("title");
                        address = content.getString("url");
                        Log.i("Title", title);

                        if (title.contains("'")) {
                            title = title.replace(title, "Article name unavailable. Click for a suprise");
                            Log.i("Broken_String", title);
                        }
                        homeStories.add(title);
                        homeLinks.add(address);

                        // database.execSQL("INSERT INTO trending (name, address) VALUES ('" + title + "','" + address + "')");
/*
                        //GET THE SUMMARY OF EACH ARTICLE
                        String summ = "";

                        if (exists("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address)) {
                            url = new URL("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address);
                            try {

                                connection = (HttpURLConnection) url.openConnection();
                                 in = connection.getInputStream();
                                 reader = new InputStreamReader(in);
                                data = reader.read();

                                while (data != -1) {
                                    char c = (char) data;
                                    summ += c;
                                    data = reader.read();
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }


                            JSONArray jArray = new JSONArray(summ);
                            JSONObject jObject = null;

                            String s1 = "";
                            String s2 = "";

                            jObject = jArray.getJSONObject(0);
                            s1 = jObject.getString("summary");
                            //  Log.i("Sum", s1);

                            JSONArray j2Array = new JSONArray(s1);
                            for (int j = 0; j < j2Array.length(); j++) {
                                JSONObject object2 = j2Array.getJSONObject(j);
                                s2 += object2.getString("sentence");
                            }
                            homeSummaries.add(s2);

                            Log.i("Sum", s2);


                        }
*/
                    }

                    adapter.notifyDataSetChanged();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

     /*
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

          //  database.execSQL("DELETE FROM trending");

            //inside this array articles we have many objects containing string author
            for (int i = 0; i < jsonArray.length() / 2; i++) {
                JSONObject content = jsonArray.getJSONObject(i);
                if (!content.isNull("url")) {
                     title = content.getString("title");
                    address = content.getString("url");
                    Log.i("Title", title);

                    if (title.contains("'")) {
                        title = title.replace(title, "Article name unavailable. Click for a suprise");
                        Log.i("Broken_String", title);
                    }

                   // database.execSQL("INSERT INTO trending (name, address) VALUES ('" + title + "','" + address + "')");
                }

                //GET THE SUMMARY OF EACH ARTICLE
                String summ = "";

                if (exists("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address)) {
                    url = new URL("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address);
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                        in = connection.getInputStream();
                        reader = new InputStreamReader(in);
                        data = reader.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }


                    while (data != -1) {
                        char c = (char) data;
                        summ += c;
                        data = reader.read();
                    }

                    if (summ != null) {
                        JSONArray jArray = new JSONArray(summ);
                        JSONObject jObject = null;

                        String s1 = "";
                        String s2 = "";

                        jObject = jArray.getJSONObject(0);
                        s1 = jObject.getString("summary");
                        Log.i("Sum", s1);

                        JSONArray j2Array = new JSONArray(s1);
                        for (int j = 0; j < j2Array.length(); j++) {
                            JSONObject object2 = j2Array.getJSONObject(j);
                            s2 += object2.getString("sentence");
                        }
                        homeSummaries.add(s2);
                        Log.i("Sum", s2);


                    }

                }

            }
*/
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
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
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

        try {
            if (exists("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + address)) {
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
                    Log.i("Sum", s1);

                    JSONArray j2Array = new JSONArray(s1);
                    for (int j = 0; j < j2Array.length(); j++) {
                        JSONObject object2 = j2Array.getJSONObject(j);
                        s2 += object2.getString("sentence");
                    }
                    homeSummaries.add(s2);
                    Log.i("Sum", s2);
                    adapter.notifyDataSetChanged();

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


    }

}

}



