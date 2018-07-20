package br.com.gotwitter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import br.com.gotwitter.R;

public class TwitterActivity extends AppCompatActivity {

    private TextView txt_twitter;
    private Button publish;
    private static  final  String URL= "https://api.twitter.com/1.1/statuses/update.json";
    //private static  final String  URL_TOKEN = "https://api.twitter.com/oauth2/token";
    private static  final String  URL_TOKEN = "https://api.twitter.com/oauth/access_token";
    private String mToken;
    private ProgressBar progressBar;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        txt_twitter = findViewById(R.id.txt_twitter);
        progressBar = findViewById(R.id.progress);
        imageView = findViewById(R.id.img_progress);
        publish = findViewById(R.id.btn);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt =  txt_twitter.getText().toString();
                if( !txt.isEmpty()){

                    Intent it = getIntent();
                    if( it != null ){
                        initTwitter();
                        showProgress(true);
                        //getCredentials(txt);
                        composerTwitter(txt);


                    }else{
                        Toast.makeText(TwitterActivity.this, R.string.ops_error_data_intent, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    public void goHome(View view) {
        finish();
    }

    private void showProgress( boolean is_show ){
        publish.setVisibility(is_show? View.GONE : View.VISIBLE);
        progressBar.setVisibility(is_show? View.VISIBLE : View.GONE);
        imageView.setVisibility(is_show? View.VISIBLE: View.GONE);
    }

    private void getCredentials(final String  txt) {
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
                            Log.d("LOG", e.getCause()+""+ e.getMessage() );
                            Toast.makeText(TwitterActivity.this, R.string.ops_error_url, Toast.LENGTH_LONG).show();
                            showProgress(false);
                            return;
                        }

                        Log.d("LOG", result.toString());
                        mToken = result.get("access_token").getAsString();
                        postOnTwitter(txt);


                    }
                });
    }
    private void postOnTwitter(String str){
        JsonObject json = new JsonObject();
        json.addProperty("status", str);

        Ion.with(this)
                .load(URL)
                .setHeader("Authorization", "Bearer " + mToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if( e != null ){
                            Log.d("LOG", e.getCause()+""+ e.getMessage() );
                            showProgress(false);
                            return;
                        }else{
                            Log.d("LOG", result.toString() );
                            showProgress(false);
                            Toast.makeText(TwitterActivity.this, R.string.ok_msg, Toast.LENGTH_SHORT).show();
                            txt_twitter.setText("");
                        }

                    }
                });
    }


    private void composerTwitter(String str ){

        TweetComposer.Builder builder = new TweetComposer.Builder(this)

                .text(str);
        builder.show();
        showProgress(false);
    }
    private void initTwitter(){

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.CONSUMER_KEY), getResources().getString(R.string.CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);

    }
}
