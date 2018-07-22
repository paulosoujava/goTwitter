package br.com.gotwitter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.gotwitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class TwitterHolder extends RecyclerView.ViewHolder {


    public CircleImageView image;
    public ImageView backdrop;

    public TextView author;
    public TextView nickname;
    public TextView twitter;
    public TextView create;

    public TwitterHolder(View itemView) {

        super(itemView);
        image =  itemView.findViewById(R.id.profile_image);
        author =  itemView.findViewById(R.id.author);
        nickname =  itemView.findViewById(R.id.nick);
        twitter =  itemView.findViewById(R.id.twitter);
        create =  itemView.findViewById(R.id.data_create);
        backdrop =  itemView.findViewById(R.id.backdrop);
    }
}
