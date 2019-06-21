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

    BackgroundTask task;
    SQLiteDatabase database;
    ListView listView;
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> summaries = new ArrayList<>();
    ArrayAdapter adapter;
    Intent intent;
    TextView updatetopicName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        task = new BackgroundTask();
        task.jsonArrayName = "articles";
        task.jsonArrayValue = "title";

        //Load topic from CategoryActivity
        intent = getIntent();
        String topic = intent.getStringExtra("topic");
        try {
            task.execute("https://newsapi.org/v2/top-headlines?country=us&category="+ topic +"&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();
            listView = findViewById(R.id.topicListView);
            //Set listview with data from DownloadTask
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, task.Stories);
            listView.setAdapter(adapter);
            updatetopicName = findViewById(R.id.updatetopicName);
            updatetopicName.setText(topic);
            //Update list
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", task.Links.get(position));
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    if(task.Summaries.get(position) == "") {
                        String link = task.Links.get(position);
                        task.position = position;
                        task.downloadSummary(link);
                        adapter.notifyDataSetChanged();
                    } //else if(task.Links.get(position) ) {

                    //}
                    else {
                        new AlertDialog.Builder(TopicActivity.this)
                                .setTitle("Summary")
                                .setMessage(task.Summaries.get(position))
                                .setPositiveButton("Close", null).show();
                    }


                } catch (Exception e) {


                }

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        task.cancel(true);
        intent = new Intent(getApplicationContext(), CategoryActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}

