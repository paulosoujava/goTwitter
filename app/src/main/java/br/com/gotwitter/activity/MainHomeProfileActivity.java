package br.com.gotwitter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.gotwitter.R;
import br.com.gotwitter.adapter.TwitterAdaperProfile;
import br.com.gotwitter.model.TwitterWithProfile;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MainHomeProfileActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private TwitterAdaperProfile mAdapter;
    private List<TwitterWithProfile> twitterList;
    private FrameLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        myHome();
    }

    private void initRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterAdaperProfile(twitterList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void myHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Status> statuses = null;
                twitterList = new ArrayList<>();

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
                                TwitterWithProfile t = new TwitterWithProfile();
                                t.setText(status.getText());
                                t.setName(status.getUser().getName());
                                t.setScreen_name(status.getUser().getScreenName());
                                t.setProfile_image_url(status.getUser().getBiggerProfileImageURL());
                                t.setProfile_banner_url(status.getUser().getBiggerProfileImageURL());
                                t.setLocation(status.getUser().getLocation());

                                twitterList.add(t);
                            }
                            initRecycler();

                        }
                    }
                } catch (TwitterException te) {
                    Log.d("LOG", "Falha ao obter a timeline: " + te.getMessage());
                }
            }
        }).start();

    }


}
