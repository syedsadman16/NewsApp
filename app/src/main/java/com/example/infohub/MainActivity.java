package com.example.infohub;


import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.concurrent.ExecutionException;

/** Temporary Issues Fix:
 * Clean and Rebuild Project
 * Sync With Gradle
 * Add attribution link
 */
public class MainActivity extends AppCompatActivity {
    //FIRST SET INTERNET PERMISSIONS

    SQLiteDatabase database;
    ListView listView;
    ArrayList<String> stories = new ArrayList<>();
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> summaries = new ArrayList<>();
    ArrayAdapter adapter;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = this.openOrCreateDatabase("NewsDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS events (name VARCHAR, address VARCHAR, id INTEGER PRIMARY KEY)");

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stories);
        listView.setAdapter(adapter);

        ContentBackgroundTask task = new ContentBackgroundTask();
        task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        updateContent();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", links.get(position));
                startActivity(intent);
            }
        });



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (summaries.get(position) != null) {
                    summaries.get(position);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Quick Summary")
                            .setMessage(summaries.get(position))
                            .setPositiveButton("Close", null).show();

                    Log.i("LongPress", summaries.get(position));

                }
                return true;
            }
        });

    }


    public void refresh(View v){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
    }



    public void updateContent(){
        Cursor cursor = database.rawQuery("SELECT * FROM events", null);

        //Pull from database
        int nameIndex = cursor.getColumnIndex("name");
        int addressIndex = cursor.getColumnIndex("address");

        if(cursor.moveToFirst()){
            stories.clear();
            links.clear();
            Log.i("name", cursor.getString(nameIndex));
            Log.i("addy", cursor.getString(addressIndex));
        }

        do {
            stories.add(cursor.getString(nameIndex));
            links.add(cursor.getString(addressIndex));
        } while (cursor.moveToNext());

        adapter.notifyDataSetChanged();
    }


    public class  ContentBackgroundTask extends AsyncTask<String, Void, String>{


            @Override
            protected String doInBackground (String...urls) {
                URL url;
                HttpURLConnection connection;
                String result = "";

                try {
                    //Get all the data for the article ID's
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

                    JSONArray jsonArray = new JSONArray(result);
                    database.execSQL("DELETE FROM events");

                    for (int i = 0; i < 16; i++) {
                        String index = jsonArray.getString(i);

                        String article = "";
                        url = new URL("https://hacker-news.firebaseio.com/v0/item/" + index + ".json?print=pretty");
                        connection = (HttpURLConnection) url.openConnection();
                        in = connection.getInputStream();
                        reader = new InputStreamReader(in);
                        data = reader.read();

                        while (data != -1) {
                            char c = (char) data;
                            article += c;
                            data = reader.read();
                        }

                        JSONObject jsonObject = new JSONObject(article);

                        if (!jsonObject.isNull("url")) {


                            String title = jsonObject.getString("title");
                            String u = jsonObject.getString("url");

                            database.execSQL("INSERT INTO events (name, address) VALUES ('" + title + "','" + u + "');");

                            //GET THE SUMMARY OF EACH ARTICLE
                            String summ = "";

                            try {
                                url = new URL("https://www.summarizebot.com/api/summarize?apiKey=31241703bbcd4c8999e1a588f4c67931&size=30&keywords=10&fragments=15&url=" + u);
                                connection = (HttpURLConnection) url.openConnection();
                                in = connection.getInputStream();
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }

                            reader = new InputStreamReader(in);
                            data = reader.read();

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

                                if (s2 != null) {
                                    summaries.add(s2);
                                    Log.i("Sum", s2);
                                } else {
                                    summaries.add("Summary not available");
                                }

                            }

                        }

                    }
                    return result;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "ARTICLE NOT FOUND");
                    return null;
                }

            }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            updateContent();
        }

    }

}

