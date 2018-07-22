package br.com.gotwitter.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Date;

import br.com.gotwitter.R;
import br.com.gotwitter.service.MyInstance;
import br.com.gotwitter.util.Util;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterActivity extends AppCompatActivity {

    private TextView txt_twitter;
    private Button publish;
    private ProgressBar progressBar;
    private ImageView imageView;
    MediaPlayer objPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        txt_twitter = findViewById(R.id.txt_twitter);
        progressBar = findViewById(R.id.progress);
        imageView = findViewById(R.id.img_progress);
        publish = findViewById(R.id.btn);
        objPlayer = MediaPlayer.create(this,R.raw.twitter);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt =  txt_twitter.getText().toString();
                if( !txt.isEmpty()){
                    Intent it = getIntent();
                    if( it != null ){
                        showProgress(true);
                        publish(txt);
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


    private void publish(final String message ) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    TwitterFactory tf = new TwitterFactory(MyInstance.getInstance(getApplicationContext()).build());
                    twitter4j.Twitter twitter = tf.getInstance();
                    try {
                        twitter.getOAuthRequestToken();
                        updateMyUI( false, "Acesso Negado.");
                    } catch (IllegalStateException ie) {

                        if (!twitter.getAuthorization().isEnabled()) {
                            updateMyUI( false, "OAuth Consumer key/secret inválido");
                        } else {
                            twitter.updateStatus(message);
                            updateMyUI( true, "weet publicado!");
                         }
                    }
                } catch (TwitterException te) {
                    updateMyUI( false, "Falha ao obter a timeline:");

                }
            }
        }).start();

    }
    private void updateMyUI(final boolean is_ok, final String msg ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( is_ok){
                    txt_twitter.setText("");
                    showProgress(false);
                    objPlayer.start();
                }
                    Util.alert(TwitterActivity.this,  msg);
            }

        });
    }
}
