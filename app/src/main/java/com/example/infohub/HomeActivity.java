package com.example.infohub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


/* TODO
See commit logs
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView trendingList;
    CustomAdapter adapter;
    BackgroundTask task;
    ProgressBar loading;
    ListViewDetails ListViewDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Side panel
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Init views
        trendingList = findViewById(R.id.favorites_list);
        loading = (ProgressBar) findViewById(R.id.loadingAnimation);

        //Declare new instance of background class
        task = new BackgroundTask();

        try {
            //Json array name inside object
            task.jsonArrayName = "articles";
            //target array element
            task.jsonArrayValue = "title";
            //Do the background stuff
            task.execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=5040cea2678445de93e1a6862c5aeeb3").get();

            //set adapter to populate list with the article titles
            adapter = new CustomAdapter(this, R.layout.list_view_layout,task.details);

            //Set list to adapter
            trendingList.setAdapter(adapter);
            //When list is empty
            trendingList.setEmptyView(loading);
            //Update listView
            adapter.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
        }

        //handle list view clicks
        trendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get link from news object in this position
                String link = task.details.get(position).getLink();
                //Pass it to intent and launch web
                Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                intent.putExtra("URL", link);
                startActivity(intent);
            }
        });

        //Long clicks for summaries
        trendingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    if(task.Summaries.get(position) == "") {
                        Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_SHORT).show();
                        String link = task.details.get(position).getLink();
                        task.downloadSummary(link);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Summary")
                                .setMessage(task.Summaries.get(position))
                                .setPositiveButton("Close", null).show();
                    }

                return true;
            }
        });

    }


    //For dropdown in title bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Navigation side panel exit
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Side panel clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //id's are assigned in res/menu/activity_home_drawer.xml
        int id = item.getItemId();

        if (id == R.id.categories) {
            Intent cat = new Intent(getApplicationContext(), CategoryActivity.class);
            Log.i("Cat", "Clicked");
            task.cancel(true);
            startActivity(cat);
        }
        else if (id == R.id.sources) {
            Intent source = new Intent(getApplicationContext(), NewsSources.class);
            task.cancel(true);
            startActivity(source);
        }
        else if (id == R.id.home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
        else if (id == R.id.favorites) {
            Intent fav = new Intent(getApplicationContext(), FavoritesActivity.class);
            startActivity(fav);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}



