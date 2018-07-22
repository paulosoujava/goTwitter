package br.com.gotwitter.service;

import android.content.Context;

import br.com.gotwitter.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MyInstance {
    private static  ConfigurationBuilder cb;
    private  static TwitterFactory tf;

    public  static ConfigurationBuilder  getInstance(Context c) {
         cb = new ConfigurationBuilder();
        return cb.setDebugEnabled(false)
                .setOAuthConsumerKey(c.getResources().getString(R.string.CONSUMER_KEY))
                .setOAuthConsumerSecret(c.getResources().getString(R.string.CONSUMER_SECRET))
                .setOAuthAccessToken(c.getResources().getString(R.string.ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(c.getResources().getString(R.string.CONSUMER_SECRET_TOKEN));


    }
}
