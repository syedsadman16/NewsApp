package com.example.infohub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsSources extends AppCompatActivity {

    SQLiteDatabase database;
    ListView listView;
    EditText filterList;
    ArrayList<String> articleNames = new ArrayList<>();
    ArrayList<String> articleLinks = new ArrayList<>();
    SourceBackgroundTask task;
    ArrayAdapter adapter;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_sources);

        filterList = (EditText) findViewById(R.id.filterText);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, articleNames);
        listView.setAdapter(adapter);

        task = new SourceBackgroundTask();
        try {
          task.execute("https://newsapi.org/v2/sources?language=en&country=us&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setTextFilterEnabled(true);
        filterList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", articleLinks.get(position));
                startActivity(intent);
            }
        });

    }


    public class  SourceBackgroundTask extends AsyncTask<String, Void, String> {

        String names;
        String links = "";

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

                //IN OBJECT -> SOURCES ARRAY -> OBJECT -> GET NAME AND URL
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("sources");

                    for (int i = 0; i < jsonArray.length() / 2; i++) {

                        JSONObject content = jsonArray.getJSONObject(i);

                            names = content.getString("name");
                            links = content.getString("url");
                            Log.i("Title", names);

                            articleNames.add(names);
                            articleLinks.add(links);

                        }
                        adapter.notifyDataSetChanged();


                return result;

            } catch (Exception e) {
                e.printStackTrace();
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
