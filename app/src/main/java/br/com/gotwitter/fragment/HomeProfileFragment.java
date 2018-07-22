package br.com.gotwitter.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import static br.com.gotwitter.util.Const.FIST_ACCESS;
import static br.com.gotwitter.util.Util.alert;


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

        //primeiro acesso necessario internet
        if (Prefs.getInteger(getActivity(), FIST_ACCESS) == -1 && Connection.checkConnection(getActivity(), getActivity().findViewById(R.id.drawer_layout))) {
            progress.setVisibility(View.VISIBLE);
            myHome();

            // 2 acesso pega da list prefs e sincroniza em 2 plano se houver conexao
        } else if (Prefs.getInteger(getActivity(), FIST_ACCESS) > 0) {

            twitterList = TwitterWithProfile.getList(getActivity());
            initRecycler();
            if (Connection.checkConnection(getActivity(), getActivity().findViewById(R.id.drawer_layout)))
                //sincronizar dados
                myHome();

        } else {
            Util.alert(getActivity(), getString(R.string.first_connection));
        }


    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TwitterAdaperProfile(twitterList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void myHome() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Status> statuses = null;


                twitterList = new ArrayList<>();

                try {

                    TwitterFactory tf = new TwitterFactory(MyInstance.getInstance(getActivity()).build());
                    Twitter twitter = tf.getInstance();
                    statuses = twitter.getHomeTimeline();

                    try {

                        twitter.getOAuthRequestToken();
                        alert(getActivity(), getString(R.string.access_not));

                    } catch (IllegalStateException ie) {

                        if (!twitter.getAuthorization().isEnabled()) {
                            alert(getActivity(), getString(R.string.key_invalid));

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

                            updatePrefs(twitterList.size(), new Gson().toJson(twitterList));

                        }
                    }
                } catch (TwitterException te) {
                    alert(getActivity(), getString(R.string.our_fail) + te.getMessage());

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
