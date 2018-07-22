package br.com.gotwitter.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import br.com.gotwitter.fragment.HomeProfileFragment;
import br.com.gotwitter.fragment.MyProfileFragment;
import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.model.Twitter;
import br.com.gotwitter.util.Connection;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView mRecyclerView;
    private TwitterAdaper mAdapter;
    private String mAccessToken;
    private ProfileTwitter mProfileTwitter = new ProfileTwitter();
    private List<Twitter> twitterList = new ArrayList<>();
    private FrameLayout progress;
    private ProfileDB db;
    private static final int LIMIT_TWITTER_IN_BD = 10;
    private static final String URL_TOKEN = "https://api.twitter.com/oauth2/token";
    private static final String URL_TIMELINE = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    private NetworkInfo networkInfo;
    private AdvanceDrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        replace(new HomeProfileFragment());
        initComponetsDrawer(toolbar);
        db = new ProfileDB(this);

        if( Connection.checkConnection(this) ){
            getCredentials();
        }else{
            mProfileTwitter = db.getProfile();
         }

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotToTwitter();
            }
        });

    }


    private void initRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterAdaper(mProfileTwitter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void myHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Status> statuses = null;


                try {

                    ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setDebugEnabled(false)
                            .setOAuthConsumerKey(getResources().getString(R.string.CONSUMER_KEY))
                            .setOAuthConsumerSecret(getResources().getString(R.string.CONSUMER_SECRET))
                            .setOAuthAccessToken(getResources().getString(R.string.ACCESS_TOKEN))
                            .setOAuthAccessTokenSecret(getResources().getString(R.string.CONSUMER_SECRET_TOKEN));
                    TwitterFactory tf = new TwitterFactory(cb.build());
                    twitter4j.Twitter twitter = tf.getInstance();
                    statuses = twitter.getHomeTimeline();
                    Log.d("LOG", "Showing home timeline.");
                    try {
                        // Lança IllegalStateException se o token de acesso estiver disponível
                        twitter.getOAuthRequestToken();
                        // Se não ocorrer significa que o acesso a conta não foi permitida
                        Log.d("LOG", "Acesso Negado.");
                    } catch (IllegalStateException ie) {
                        // Verifica se possui autorização
                        if (!twitter.getAuthorization().isEnabled()) {
                            Log.d("LOG", "OAuth Consumer key/secret inválido.");
                        } else {
                            for (Status status : statuses) {
                                //profileImageUrlHttps
                                //screenName
                                //text

                                Log.d("LOG", "Foto : " + status.getUser().getBiggerProfileImageURL());
                                Log.d("LOG", "getLocation : " + status.getUser().getLocation());
                                Log.d("LOG", "getName : " + status.getUser().getName());
                                Log.d("LOG", "getScreenName : " + status.getUser().getScreenName());
                                Log.d("LOG", "getText :" + status.getText());
                            }
                        }
                    }
                } catch (TwitterException te) {
                    Log.d("LOG", "Falha ao obter a timeline: " + te.getMessage());
                }
            }
        }).start();

    }

    private void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "Teste para Twitter" + new Date();
                try {
                    ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setDebugEnabled(false)
                            .setOAuthConsumerKey(getResources().getString(R.string.CONSUMER_KEY))
                            .setOAuthConsumerSecret(getResources().getString(R.string.CONSUMER_SECRET))
                            .setOAuthAccessToken(getResources().getString(R.string.ACCESS_TOKEN))
                            .setOAuthAccessTokenSecret(getResources().getString(R.string.CONSUMER_SECRET_TOKEN));
                    TwitterFactory tf = new TwitterFactory(cb.build());
                    twitter4j.Twitter twitter = tf.getInstance();
                    try {
                        // Lança IllegalStateException se o token de acesso estiver disponível
                        twitter.getOAuthRequestToken();
                        // Se não ocorrer significa que o acesso a conta não foi permitida
                        Log.d("LOG", "Acesso Negado.");
                    } catch (IllegalStateException ie) {
                        // Verifica se possui autorização
                        if (!twitter.getAuthorization().isEnabled()) {
                            Log.d("LOG", "OAuth Consumer key/secret inválido.");
                        } else {
                            Status status = twitter.updateStatus(message);
                            Log.d("LOG", "Tweet publicado! [" + status.getText() + "].");
                        }
                    }
                } catch (TwitterException te) {
                    Log.d("LOG", "Falha ao obter a timeline: " + te.getMessage());
                }
            }
        }).start();

    }
    private void publishe(String id) {
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
                        if (e != null)
                            Log.d("LOG", e.getCause() + " " + e.getMessage() + " " + e.getStackTrace());

                        if (result != null)
                            Log.d("LOG", result.toString());
                    }
                });
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
                        getDataProfile();
                    }
                });
    }

    private void getDataProfile() {
        String screen_name = getResources().getString(R.string.SCREEN_NAME);
        String url = URL_TIMELINE + "?screen_name=" + screen_name + "&count=20";
        Ion.with(this)
                .load(url)
                .setHeader("Authorization", "Bearer " + mAccessToken)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null) {
                            Toast.makeText(MainActivity.this, R.string.ops_error_url, Toast.LENGTH_LONG).show();
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

                    }
                });
    }

    private void gotToTwitter() {
        if (Connection.checkConnection(this)) {
            startActivity(new Intent(MainActivity.this, TwitterActivity.class));
        } else {
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            case R.id.navigation_home:
                replace( new HomeProfileFragment());

                return true;
            case R.id.navigation_timeline:
                replace( new MyProfileFragment());

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

    private void replace(Fragment fr){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fr)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .commit();

    }

}
