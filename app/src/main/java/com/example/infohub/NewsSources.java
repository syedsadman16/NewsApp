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
    BackgroundTask task;
    CustomAdapter adapter;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_sources);

        filterList = (EditText) findViewById(R.id.filterText);
        setTitle("News Sources");

        task = new BackgroundTask();
        try {
          task.jsonArrayName = "sources";
          task.jsonArrayValue = "name";
          task.execute("https://newsapi.org/v2/sources?language=en&country=us&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();
          listView = findViewById(R.id.listView);
          adapter = new CustomAdapter(this, R.layout.list_view_layout, task.details);
          listView.setAdapter(adapter);
          adapter.notifyDataSetChanged();

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
                //Get the links from BackgroundTask and open WebView
                intent.putExtra("URL", task.Links.get(position));
                startActivity(intent);
            }
        });

    }


}
