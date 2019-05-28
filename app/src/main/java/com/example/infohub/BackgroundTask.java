package com.example.infohub;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.R.*;

public class  BackgroundTask extends AsyncTask<String, Void, String> {

    String title;
    String address = "";
    String jsonArrayName = "";
    String jsonArrayValue = "";
    ListView trendingList;
    ArrayList<String> Stories = new ArrayList<>();
    ArrayList<String> Links = new ArrayList<>();
    ArrayList<String> Summaries = new ArrayList<>();



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
                JSONArray jsonArray = jsonObject.getJSONArray(jsonArrayName);

                for (int i = 0; i < jsonArray.length() / 2; i++) {

                    JSONObject content = jsonArray.getJSONObject(i);
                    if (!content.isNull("url")) {

                        title = content.getString(jsonArrayValue);
                        address = content.getString("url");

                        Stories.add(title);
                        Links.add(address);

                    }

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

        try {

            if (exists("https://api.smmry.com/&SM_API_KEY=EF29A1A24B&SM_LENGTH=4&SM_URL=" + address)) {

                url = new URL("https://api.smmry.com/&SM_API_KEY=EF29A1A24B&SM_LENGTH=4&SM_URL=" + address);
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

                    JSONObject jObject = new JSONObject(summ);

                    String s1 = "";
                    String s2 = "";

                    s1 = jObject.getString("sm_api_content");
                    Log.i("Summary", s1);
                    Summaries.add(s1);

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

