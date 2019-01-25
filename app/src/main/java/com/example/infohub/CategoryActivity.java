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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
    }


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



    public void topicResult(View view){

        Button btn = (Button) view;
        String option = btn.getTag().toString();
        Intent intent;

        switch(option){
            case "Technology":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "technology");
                Log.i("Intent", "topic");
                startActivity(intent);
                finish();
                return;
                //break;
            case "General":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "general");
                startActivity(intent);
                finish();
                return;
            case "Health":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "health");
                startActivity(intent);
                finish();
                return;
            case "Science":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "science");
                startActivity(intent);
                finish();
                return;
            case "Sports":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "sports");
                startActivity(intent);
                finish();
                return;
            case "Entertainment":
                intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("topic", "entertainment");
                startActivity(intent);
                finish();
                return;

        }

    }



}
