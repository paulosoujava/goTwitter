package br.com.gotwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.gotwitter.R;
import br.com.gotwitter.holder.TwitterHolder;
import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.model.Twitter;

public class TwitterAdaper extends RecyclerView.Adapter<TwitterHolder> {

    private final ProfileTwitter twitters;

    public TwitterAdaper(ProfileTwitter twitters) {
        this.twitters = twitters;

    }

    @Override
    public TwitterHolder onCreateViewHolder(ViewGroup parent, int viewType) {

  return new TwitterHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(TwitterHolder holder, int position) {
        Picasso.get()
                .load(twitters.getProfile_image_url())
                .placeholder(R.drawable.no_photo)
                .into(holder.image);
        holder.twitter.setText(twitters.getmList().get(position).getTwitter_txt());
        holder.create.setText(twitters.getmList().get(position).getData_create());
        holder.author.setText(String.format("%s \n\t%s", twitters.getName(), twitters.getScreen_name()));

    }

    @Override
    public int getItemCount() {
        return twitters.getmList() != null ? twitters.getmList().size() : 0;
    }


}
