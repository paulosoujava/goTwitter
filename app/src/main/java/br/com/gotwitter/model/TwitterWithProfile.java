package br.com.gotwitter.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.gotwitter.data.Prefs;

public class TwitterWithProfile extends ProfileTwitter {
    public static final  String TWITTER_KEY = "TwitterWithProfile";


    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public static List<TwitterWithProfile> getList(Context ctx) {
        Gson gson = new Gson();
        String json = Prefs.getString(ctx , TwitterWithProfile.TWITTER_KEY);
        Type type = new TypeToken<List<TwitterWithProfile>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
