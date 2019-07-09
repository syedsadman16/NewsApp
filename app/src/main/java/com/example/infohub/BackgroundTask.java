package com.example.infohub;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.*;

public class  BackgroundTask extends AsyncTask<String, Void, String> {
    public int position;
    String title;
    String address = "";
    String imageURL = "";
    String jsonArrayName = "";
    String jsonArrayValue = "";
    ArrayList<String> Stories = new ArrayList<>();
    ArrayList<String> Links = new ArrayList<>();
    ArrayList<String> Summaries = new ArrayList<>();
    ArrayList<ListViewDetails> details = new ArrayList<>();




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground (String...urls){
        URL url;
        HttpURLConnection connection;

        /* Use StringBuilder instead of String to concatenate data. For strings, each concat requires
         * a new string to be created and allocates memory each time.
         * With StringBuilder, the data is mutable - sequence of chars will be added to an updated
         * array so that memory is allocated only when it exceeds buffer
         */
        StringBuilder result = new StringBuilder();


        try {

            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            // connection.setRequestMethod("GET");
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char c = (char) data;
                result.append(c);
                data = reader.read();
            }

            try {

                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                JSONArray jsonArray = jsonObject.getJSONArray(jsonArrayName);

                for (int i = 0; i < jsonArray.length() / 2; i++) {

                    JSONObject content = jsonArray.getJSONObject(i);
                    if (!content.isNull("url")) {

                        title = content.getString(jsonArrayValue);
                        address = content.getString("url");
                        if(!content.isNull("urlToImage")) {
                            imageURL = content.getString("urlToImage");
                        } else {
                            imageURL = "https://fastmac.org/wp-content/uploads/2018/04/The-News-App-840x400.jpg";
                        }

                        Log.i("URL", imageURL);
                        //Create object and add it to array so it doesn't over-write every time
                        ListViewDetails listViewDetails = new ListViewDetails(title,imageURL,address);
                        details.add(listViewDetails);
                        Summaries.add("");

                    }

                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return String.valueOf(result);

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



    public void downloadSummary(final String address){
        //Run on seperate thread instead of main thread
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                URL url;
                HttpURLConnection connection;
                int data = 0;
                StringBuilder summ = new StringBuilder();

                try {

                    if (exists("https://api.smmry.com/&SM_API_KEY=EF29A1A24B&SM_LENGTH=4&SM_URL=" + address)) {

                         url = new URL("https://api.smmry.com/&SM_API_KEY=EF29A1A24B&SM_LENGTH=4&SM_URL=" + address);
                        try {
                            connection = (HttpURLConnection) url.openConnection();
                            Log.i("SmmryLink","https://api.smmry.com/&SM_LENGTH=4&SM_API_KEY=EF29A1A24B&SM_LENGTH=4&SM_URL=" + address );
                            InputStream in = connection.getInputStream();
                            InputStreamReader reader = new InputStreamReader(in);
                            data = reader.read();

                            while (data != -1) {
                                char c = (char) data;
                                summ.append(c);
                                data = reader.read();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        if (summ != null) {

                            JSONObject jObject = new JSONObject(String.valueOf(summ));

                            String s1 = "";

                            s1 = jObject.getString("sm_api_content");
                            Log.i("Summary", s1);
                            Summaries.set(position, s1);

                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }


    @Override
    protected void onPostExecute(String result) {
        //Fill up array with empty strings so articles can be put in any position
        for(int i=0; i<Links.size(); i++){
            Summaries.add("");
        }
        super.onPostExecute(result);

    }
}

