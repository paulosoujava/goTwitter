package br.com.gotwitter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.gotwitter.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class TwitterHolder extends RecyclerView.ViewHolder {


    public CircleImageView image;
    public TextView author;
    public TextView twitter;
    public TextView create;

    public TwitterHolder(View itemView) {

        super(itemView);
        image =  itemView.findViewById(R.id.profile_image);
        author =  itemView.findViewById(R.id.author);
        twitter =  itemView.findViewById(R.id.twitter);
        create =  itemView.findViewById(R.id.data_create);
    }
}
