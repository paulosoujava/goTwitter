package br.com.gotwitter.fragment;


import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
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
import br.com.gotwitter.activity.MainActivity;
import br.com.gotwitter.activity.TwitterActivity;
import br.com.gotwitter.adapter.TwitterAdaper;
import br.com.gotwitter.data.Prefs;
import br.com.gotwitter.data.ProfileDB;
import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.model.Twitter;
import br.com.gotwitter.model.TwitterWithProfile;
import br.com.gotwitter.util.Connection;
import br.com.gotwitter.util.Const;
import br.com.gotwitter.util.Util;

import static br.com.gotwitter.util.Const.*;


public class MyProfileFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TwitterAdaper mAdapter;
    private String mAccessToken;
    private FrameLayout progress;
    private ProfileDB db;

    private ProfileTwitter mProfileTwitter = new ProfileTwitter();
    private List<Twitter> twitterList = new ArrayList<>();

    public MyProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = getActivity().findViewById(R.id.recycler);
        progress =  getActivity().findViewById(R.id.progress);

        db = new ProfileDB(getActivity());
        progress.setVisibility(View.VISIBLE);



        if (Connection.checkConnection(getActivity())) {
            if (Prefs.getInteger(getActivity(), SIZE_LIST_TIMELINE) < 0) {
                progress.setVisibility(View.VISIBLE);
                //get web
                getCredentials();
                initRecycler();
            } else {
                    //from SQLite
                    mProfileTwitter = db.getProfile();
                    initRecycler();
                   getCredentials();
            }
        } else {
            if (Prefs.getInteger(getActivity(), SIZE_LIST_TIMELINE) < 0) {
                Util.alert(getActivity(),"Desculpe mas não houve uma sincronização por falta de conexão com a internet");
            }else{
                //from SQLite
                mProfileTwitter = db.getProfile();
                initRecycler();
            }
        }


    }

    private void initRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterAdaper(mProfileTwitter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
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
                            Toast.makeText(getActivity(), R.string.ops_error_url, Toast.LENGTH_LONG).show();
                            return;
                        }
                        mAccessToken = result.get("access_token").getAsString();
                        getDataProfile();
                    }
                });
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
                            Toast.makeText(getActivity(),  R.string.ops_error_url, Toast.LENGTH_LONG).show();
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
                                db.saveTwitter(twitterList);

                        }
                        // publishe(user.get("id").getAsString());
                        mProfileTwitter.setmList(twitterList);

                        if (Prefs.getInteger(getActivity(), SIZE_LIST_TIMELINE) < 0) {
                            updatePrefs(mProfileTwitter.getmList().size(), new Gson().toJson(mProfileTwitter));
                        } else {
                            if (Prefs.getInteger(getActivity(), SIZE_LIST_TIMELINE) != mProfileTwitter.getmList().size())
                                updatePrefs(mProfileTwitter.getmList().size(), new Gson().toJson(mProfileTwitter));
                        }

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

    private void updatePrefs(int size, String list) {
        Prefs.setInteger(getActivity(), SIZE_LIST_TIMELINE, size);
        Prefs.setString(getActivity(), ProfileTwitter.KEY_PROFILE_TWITTER, list);

    }


}
