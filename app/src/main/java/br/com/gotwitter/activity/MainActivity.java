package br.com.gotwitter.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import br.com.gotwitter.R;
import br.com.gotwitter.data.Prefs;
import br.com.gotwitter.data.ProfileDB;
import br.com.gotwitter.fragment.HomeProfileFragment;
import br.com.gotwitter.fragment.MyProfileFragment;
import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.util.Connection;
import br.com.gotwitter.util.Util;

import static br.com.gotwitter.util.Const.FIST_ACCESS;
import static br.com.gotwitter.util.Const.URL_TIMELINE;
import static br.com.gotwitter.util.Const.URL_TOKEN;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private String mAccessToken;
    private ProfileTwitter mProfileTwitter = new ProfileTwitter();
    private FloatingActionButton fab;

    private ProfileDB db;


    private AdvanceDrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        replace(new HomeProfileFragment());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initComponetsDrawer(toolbar);

        db = new ProfileDB(this);

        if (Prefs.getInteger(this, FIST_ACCESS) == -1 && Connection.checkConnection(this, drawer)) {
            init();
        } else if (Prefs.getInteger(this, FIST_ACCESS) > 0) {
            init();
        } else {
            Util.alert(this, getString(R.string.first_connection));
        }

    }

    private void init() {
        if (Connection.checkConnection(this, drawer)) {
            getCredentials();
        } else {
            mProfileTwitter = db.getProfile();

            //sincronize os dados
            if (Connection.checkConnection(this))
                getCredentials();

        }

        if (Connection.checkConnection(this)) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, TwitterActivity.class));
                }
            });
        }else{
            fab.setVisibility(View.GONE);
        }

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
                startActivity(new Intent(MainActivity.this, TwitterActivity.class));

                return true;
            case R.id.navigation_home:
                replace(new HomeProfileFragment());

                return true;
            case R.id.navigation_timeline:
                replace(new MyProfileFragment());

                return true;
            case R.id.navigation_info:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));

                return true;

            case R.id.navigation_profile:
                Intent itProfile = new Intent(MainActivity.this, ProfileActivity.class);
                itProfile.putExtra(ProfileTwitter.KEY_PROFILE_TWITTER, mProfileTwitter);
                startActivity(itProfile);
                return true;

            case R.id.navigation_timeline_sqlite:
                MyProfileFragment fr = new MyProfileFragment();
                fr.setOffline(true);
                replace(fr);
                return true;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void replace(Fragment fr) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fr)
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .commit();

    }

}
