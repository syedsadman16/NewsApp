package com.example.infohub;


import android.app.ProgressDialog;
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
    ContentBackgroundTask task;
    ArrayAdapter adapter;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  database = this.openOrCreateDatabase("NewsDB", MODE_PRIVATE, null);
        //  database.execSQL("CREATE TABLE IF NOT EXISTS events (name VARCHAR, address VARCHAR, id INTEGER PRIMARY KEY)");

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stories);
        listView.setAdapter(adapter);

        task = new ContentBackgroundTask();
        try {
            String s = task.execute("https://newsapi.org/v2/top-headlines?country=us&category=technology&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                try {
                    summaries.get(position);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Summary")
                            .setMessage(summaries.get(position))
                            .setPositiveButton("Close", null).show();

                } catch (Exception e) {

                    String link = links.get(position);
                    task.downloadSummary(link);
                    adapter.notifyDataSetChanged();

                }

                return true;
            }
        });

    }


    public void refresh(View v){
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
    }


/*
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
*/

    public class  ContentBackgroundTask extends AsyncTask<String, Void, String>{

        String title;
        String address = "";
        ProgressDialog progress = new ProgressDialog(MainActivity.this);


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

                try {

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");

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

                            stories.add(title);
                           links.add(address);

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

            progress.setMessage("Downloaded summary ");
            progress.show();

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

                        JSONArray j2Array = new JSONArray(s1);

                        for (int j = 0; j < j2Array.length(); j++) {
                            JSONObject object2 = j2Array.getJSONObject(j);
                            s2 += object2.getString("sentence");
                        }

                        summaries.add(s2);
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

