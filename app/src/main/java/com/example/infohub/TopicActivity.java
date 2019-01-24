package com.example.infohub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TopicActivity extends AppCompatActivity {

    TopicBackgroundTask task;
    SQLiteDatabase database;
    ListView listView;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> summaries = new ArrayList<>();
    ArrayAdapter adapter;
    String result = "";
    Intent intent;
    TextView updatetopicName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        listView = findViewById(R.id.topicListView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        task = new TopicBackgroundTask();

        intent = getIntent();
        String topic = intent.getStringExtra("topic");
        try {
            String topi = "technology";
            task.execute("https://newsapi.org/v2/top-headlines?country=us&category="+ topi +"&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();
            updatetopicName = findViewById(R.id.updatetopicName);
            updatetopicName.setText(topic);

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
                    new AlertDialog.Builder(TopicActivity.this)
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



    public class  TopicBackgroundTask extends AsyncTask<String, Void, String> {

        String title;
        String address = "";
        ProgressDialog progress = new ProgressDialog(TopicActivity.this);

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

                            titles.add(title);
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
            progress.dismiss();
        }

    }

}

