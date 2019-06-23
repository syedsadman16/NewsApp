package com.example.infohub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CategoryActivity extends AppCompatActivity {

    //Class functionality in TopicActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
    }

    //Custom search for a topic
    public void editClick(View v){
        final EditText seatch = (EditText) findViewById(R.id.searchEditText);

        seatch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    String topic = seatch.getText().toString();
                    Intent i = new Intent(getApplicationContext(), TopicActivity.class);
                    i.putExtra("topic", topic);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });
        }


    //Loaded in TopicActivity.java
    public void topicResult(View view){

        Button btn = (Button) view;
        String option = btn.getTag().toString();
        Intent intent;

        switch(option){
            case "Technology":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "Technology");
                Log.i("Intent", "topic");
                startActivity(intent);
                finish();
                return;
                //break;
            case "General":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "Business");
                startActivity(intent);
                finish();
                return;
            case "Health":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "Health");
                startActivity(intent);
                finish();
                return;
            case "Science":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "Science");
                startActivity(intent);
                finish();
                return;
            case "Sports":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "Sports");
                startActivity(intent);
                finish();
                return;
            case "Entertainment":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "Entertainment");
                startActivity(intent);
                finish();
                return;

        }

    }



}
