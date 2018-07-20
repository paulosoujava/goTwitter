package br.com.gotwitter.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.gotwitter.R;
import br.com.gotwitter.adapter.TwitterAdaper;
import br.com.gotwitter.data.ProfileDB;
import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.model.Twitter;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private RecyclerView mRecyclerView;
    private TwitterAdaper mAdapter;
    private String mAccessToken;
    private  ProfileTwitter mProfileTwitter = new ProfileTwitter();
    private List<Twitter> twitterList = new ArrayList<>();
    private FrameLayout progress;
    private ProfileDB db;
    private static  final int  LIMIT_TWITTER_IN_BD = 10;
    private static  final String  URL_TOKEN = "https://api.twitter.com/oauth2/token";
    private static  final String  URL_TIMELINE = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    private NetworkInfo networkInfo;
    private AdvanceDrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        drawer =  findViewById(R.id.drawer_layout);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        initComponetsDrawer(toolbar);


        //SQLITE
        db = new ProfileDB(this);
        //ADPATER WITH RECICLER
        initRecycler();

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotToTwitter();
            }
        });

    }



    private void checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getCredentials();
        } else {
            //no has connection so pick from BD
            mProfileTwitter = db.getProfile();
            initRecycler();
            Toast.makeText(getApplicationContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void initRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterAdaper(mProfileTwitter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void getCredentials() {
        Ion.with(this)
                .load(URL_TOKEN)
                .basicAuthentication(getResources().getString(R.string.CONSUMER_KEY),
                        getResources().getString(R.string.CONSUMER_SECRET))
                .setBodyParameter("grant_type", "client_credentials")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, R.string.ops_error_url, Toast.LENGTH_LONG).show();
                            return;
                        }
                        mAccessToken = result.get("access_token").getAsString();
                        Log.d("LOG", mAccessToken);
                        getDataProfile();
                    }
                });
    }
    private  void publishe( String id){
        JsonObject json = new JsonObject();
        json.addProperty("status", "bar");
        String screen_name = getResources().getString(R.string.SCREEN_NAME);
       // String url = "https://api.twitter.com/1.1/statuses/update.json?screen_name="+screen_name+"&oauth_consumer_key="+getResources().getString(R.string.CONSUMER_KEY)+"&oauth_consumer_secret="+ getResources().getString(R.string.CONSUMER_SECRET)+"&oauth_token=565577067-0E2ej0CfKyBlSVl6f5GMMLyEuMZBPG6HGZPpO35P&oauth_token_secret=PiYPEUfaqmCgCuGm7TFc8sm2MFM8Q99AZk0YfW8V8nyv0 )";
        //String Url = "oauth_token=6253282-eWudHldSbIaelX7swmsiHImEL4KinwaGloHANdrY&oauth_token_secret=2EEfA6BG3ly3sR3RjE0IBSnlQu4ZrUzPiYKmrkVU&user_id=6253282&screen_name=twitterap";

        json.addProperty("method", "POST");
        json.addProperty("uri", "https://api.twitter.com/1.1/statuses/update.json");
        json.addProperty("json", "true");
        /*
{
 "uri": "https://api.twitter.com/1.1/statuses/update.json",
 "method": "POST",
 "headers": {
  "Content-Type": "application/x-www-form-urlencoded"
 },
 "json": true,
 "body": "status=Test%20status",
 "oauth": {
  "token": "token",
  "token_secret": "token_secret",
  "consumer_key": "consumer",
  "consumer_secret": "consumer_secret"
 }
}

 */

        Ion.with(this)
                .load("https://api.twitter.com/1.1/statuses/update.json")
                .basicAuthentication("token", "AAAAAAAAAAAAAAAAAAAAAKI47wAAAAAAhbeJbBlBFGgw7I5Qgo3hVscAsnk%3DqG2Havc7dueR9RIN7gSY4apr8Szd3UTJTmvN9m1uGc7BT2qpeL")
                .basicAuthentication("token_secret", "PiYPEUfaqmCgCuGm7TFc8sm2MFM8Q99AZk0YfW8V8nyv0")
                .basicAuthentication("consumer_key", "gw4EsWhlvGJk6D8l2npUMMj1L")
                .basicAuthentication("consumer_secret", "ftvEIrD8SuYhYAU29iOWUMksT8Z1xRFNPNkfnzOeE5lIPokMxI")
                .setBodyParameter("status", "Oiiiii")

                //.basicAuthentication("access_token", "AAAAAAAAAAAAAAAAAAAAAKI47wAAAAAAhbeJbBlBFGgw7I5Qgo3hVscAsnk%3DqG2Havc7dueR9RIN7gSY4apr8Szd3UTJTmvN9m1uGc7BT2qpeL")
                //.setBodyParameter("oauth_consumer_key", getResources().getString(R.string.CONSUMER_KEY))
                //.setBodyParameter("oauth_consumer_secret", getResources().getString(R.string.CONSUMER_SECRET))
                //.setBodyParameter("request_token", "565577067-0E2ej0CfKyBlSVl6f5GMMLyEuMZBPG6HGZPpO35P")
                //.setBodyParameter("oauth_token_secret", "PiYPEUfaqmCgCuGm7TFc8sm2MFM8Q99AZk0YfW8V8nyv0")
                //.setBodyParameter("access_token", "AAAAAAAAAAAAAAAAAAAAAKI47wAAAAAAhbeJbBlBFGgw7I5Qgo3hVscAsnk%3DqG2Havc7dueR9RIN7gSY4apr8Szd3UTJTmvN9m1uGc7BT2qpeL")
                //.setBodyParameter("list_id", id)
                //.setBodyParameter("screen_name", screen_name)

                //.setBodyParameter("mode", "public")
                //.setBodyParameter("description", "Sei la")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if( e!= null )
                            Log.d("LOG", e.getCause()+" "+e.getMessage()+" "+e.getStackTrace() );

                        if( result!= null )
                            Log.d("LOG", result.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mAdapter != null ){
            checkConnection();
        }
    }

    private void getDataProfile() {
        String screen_name = getResources().getString(R.string.SCREEN_NAME);
        String url = URL_TIMELINE+"?screen_name=" + screen_name + "&count=20";
        Ion.with(this)
                .load(url)
                .setHeader("Authorization", "Bearer " + mAccessToken)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this,  R.string.ops_error_url, Toast.LENGTH_LONG).show();
                            return;
                        }

                        //data user
                        JsonObject user = result.get(0).getAsJsonObject().get("user").getAsJsonObject();
                        mProfileTwitter.setId(user.get("id").getAsString());

                        mProfileTwitter.setDescription(user.get("description").getAsString());
                        mProfileTwitter.setFollowers_count(user.get("followers_count").getAsString());
                        mProfileTwitter.setFriends_count(user.get("friends_count").getAsString());
                        mProfileTwitter.setLocation(user.get("location").getAsString());
                        mProfileTwitter.setName(user.get("name").getAsString());
                        mProfileTwitter.setProfile_banner_url(user.get("profile_banner_url").getAsString());
                        mProfileTwitter.setProfile_image_url(user.get("profile_image_url").getAsString());
                        mProfileTwitter.setScreen_name(user.get("screen_name").getAsString());
                        mProfileTwitter.setStatuses_count(user.get("statuses_count").getAsString());
                        mProfileTwitter.setUrl(user.get("url").getAsString());

                        //save in database SQLLite
                        db.saveProfile(mProfileTwitter);
                        twitterList.clear();
                        for (int i = 0; i < result.size(); i++) {
                            Twitter t = new Twitter(
                                    result.get(i).getAsJsonObject().get("text").getAsString(),
                                    convertionDate(result.get(i).getAsJsonObject().get("created_at").getAsString())
                            );
                            t.setId(result.get(i).getAsJsonObject().get("id").getAsString());
                            twitterList.add(t);

                            //save in database SQLLite
                            if( i <  LIMIT_TWITTER_IN_BD)
                                db.saveTwitter(t);

                        }
                        publishe(user.get("id").getAsString());
                        mProfileTwitter.setmList(twitterList);
                        progress.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    private String convertionDate(String dateStr) {
        String formatedDate = null;
        java.util.Date fecha = new java.util.Date(dateStr);
        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        Date date;
        try {
            date = formatter.parse(fecha.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            formatedDate = cal.get(Calendar.DATE) + "/" +
                            (cal.get(Calendar.MONTH) + 1) +
                            "/" + cal.get(Calendar.YEAR)+" " +
                            cal.get(Calendar.HOUR_OF_DAY) +": "+
                            cal.get(Calendar.MINUTE) +": "+
                            cal.get(Calendar.SECOND) ;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return formatedDate;
    }


    private  void gotToTwitter(){
        if(networkInfo != null && networkInfo.isConnected()){
            startActivity(new Intent(MainActivity.this, TwitterActivity.class));
        }else{
            Toast.makeText(getApplicationContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
        }
    }


    //DRAWER
    private void initComponetsDrawer(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);
        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener( this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.navigation_twittar:
                gotToTwitter();

                return true;
            case R.id.navigation_info:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));

                return true;

            case R.id.navigation_profile:
                Intent itProfile = new Intent(MainActivity.this, ProfileActivity.class);
                itProfile.putExtra(ProfileTwitter.KEY_PROFILE_TWITTER, mProfileTwitter);
                startActivity(itProfile);

                return true;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }



}
