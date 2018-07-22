package br.com.gotwitter.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.gotwitter.R;
import br.com.gotwitter.adapter.TwitterAdaperProfile;
import br.com.gotwitter.data.Prefs;
import br.com.gotwitter.model.TwitterWithProfile;
import br.com.gotwitter.service.MyInstance;
import br.com.gotwitter.util.Connection;
import br.com.gotwitter.util.Const;
import br.com.gotwitter.util.Util;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import static br.com.gotwitter.util.Util.*;


public class HomeProfileFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TwitterAdaperProfile mAdapter;
    private List<TwitterWithProfile> twitterList;
    private FrameLayout progress;


    public HomeProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = getActivity().findViewById(R.id.recycler);
        progress = getActivity().findViewById(R.id.progress);

        if (Connection.checkConnection(getActivity())) {

            if (Prefs.getInteger(getActivity(), Const.SIZE_LIST_TIMELINE_HOME) < 0) {
                progress.setVisibility(View.VISIBLE);
                myHome(true);
            } else {
                twitterList = TwitterWithProfile.getList(getActivity());
                initRecycler();
                myHome(false);
            }
        } else {
            if (Prefs.getInteger(getActivity(), Const.SIZE_LIST_TIMELINE_HOME) > 0) {
                twitterList = TwitterWithProfile.getList(getActivity());
                initRecycler();
            } else {
                alert(getActivity(),"Desculpe mas não houve uma sincronização por falta de conexão com a internet");
            }
        }

    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterAdaperProfile(twitterList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void myHome(final boolean is_list_cache) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Status> statuses = null;


                twitterList = new ArrayList<>();

                try {

                    TwitterFactory tf = new TwitterFactory(MyInstance.getInstance(getActivity()).build());
                    twitter4j.Twitter twitter = tf.getInstance();
                    statuses = twitter.getHomeTimeline();

                    try {

                        twitter.getOAuthRequestToken();
                        alert(getActivity(),"Ops , Acesso Negado.");

                    } catch (IllegalStateException ie) {

                        if (!twitter.getAuthorization().isEnabled()) {
                            alert(getActivity(),"Ops, OAuth Consumer key/secret inválido.");

                        } else {
                            for (Status status : statuses) {
                                TwitterWithProfile t = new TwitterWithProfile();
                                t.setText(status.getText());
                                t.setName(status.getUser().getName());
                                t.setScreen_name(status.getUser().getScreenName());
                                t.setProfile_image_url(status.getUser().getProfileImageURL());
                                t.setProfile_banner_url(status.getUser().getProfileBannerMobileURL());
                                t.setLocation(status.getUser().getLocation());
                                twitterList.add(t);
                            }

                            if (Prefs.getInteger(getActivity(), Const.SIZE_LIST_TIMELINE_HOME) < 0) {
                                updatePrefs(twitterList.size(), new Gson().toJson(twitterList));
                            } else {
                                if (Prefs.getInteger(getActivity(), Const.SIZE_LIST_TIMELINE_HOME) != twitterList.size())
                                    updatePrefs(twitterList.size(), new Gson().toJson(twitterList));
                            }
                        }
                    }
                } catch (TwitterException te) {
                    alert(getActivity(),"Falha ao obter a timeline: " + te.getMessage());

                }
            }
        }).start();

    }

    private void updatePrefs(int size, String list) {
        Prefs.setInteger(getActivity(), Const.SIZE_LIST_TIMELINE_HOME, size);
        Prefs.setString(getActivity(), TwitterWithProfile.TWITTER_KEY, list);
        updateMyUI();
    }

    private void updateMyUI() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                initRecycler();
                progress.setVisibility(View.GONE);
            }

        });
    }

}
