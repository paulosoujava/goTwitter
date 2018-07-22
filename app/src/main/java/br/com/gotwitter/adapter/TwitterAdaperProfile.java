package br.com.gotwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.gotwitter.R;
import br.com.gotwitter.holder.TwitterHolder;
import br.com.gotwitter.model.ProfileTwitter;
import br.com.gotwitter.model.TwitterWithProfile;
import de.hdodenhof.circleimageview.CircleImageView;

public class TwitterAdaperProfile extends RecyclerView.Adapter<TwitterHolder> {

    private final List<TwitterWithProfile> twitters;

    public TwitterAdaperProfile(List<TwitterWithProfile> twitters) {
        this.twitters = twitters;
      }

    @Override
    public TwitterHolder onCreateViewHolder(ViewGroup parent, int viewType) {

  return new TwitterHolder(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_adapter_profiles, parent, false));
    }

    @Override
    public void onBindViewHolder(TwitterHolder holder, int position) {

        showImage(twitters.get(position).getProfile_image_url(),holder.image ,null);
        showImage(twitters.get(position).getProfile_banner_url(),null, holder.backdrop );

        holder.author.setText(twitters.get(position).getName());
        holder.nickname.setText(twitters.get(position).getScreen_name());
        holder.twitter.setText(twitters.get(position).getText());


    }

    @Override
    public int getItemCount() {
        return twitters != null ? twitters.size() : 0;
    }

    private void showImage(String url, CircleImageView circleImageView, ImageView imageView) {
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.no_photo)
                .into(circleImageView == null ? imageView : circleImageView);
    }



}
