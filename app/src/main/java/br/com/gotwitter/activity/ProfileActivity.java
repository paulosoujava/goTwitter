package br.com.gotwitter.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.com.gotwitter.R;
import br.com.gotwitter.model.ProfileTwitter;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView screen_name;
    private TextView name;
    private TextView location;
    private TextView description;
    private TextView followers_count;
    private TextView friends_count;
    private TextView statuses_count;
    private CircleImageView profile_image_url;
    private ImageView profile_banner_url;
    private ProfileTwitter profileTwitter;
    private CardView containersite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        if (intent != null) {
            profileTwitter = intent.getParcelableExtra(ProfileTwitter.KEY_PROFILE_TWITTER);
            initComponentsId();

        } else {
            Toast.makeText(this, R.string.ops_error_data_intent, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initComponentsId() {
        name = findViewById(R.id.name);
        location = findViewById(R.id.location);
        description = findViewById(R.id.description);
        followers_count = findViewById(R.id.followers_count);
        friends_count = findViewById(R.id.friends_count);
        statuses_count = findViewById(R.id.statuses_count);
        profile_image_url = findViewById(R.id.profile_image_url);
        screen_name = findViewById(R.id.screen_name);
        profile_banner_url = findViewById(R.id.profile_banner_url);

        containersite = findViewById(R.id.has_site);


        if (profileTwitter != null) {
            //data
            location.setText(profileTwitter.getLocation());
            description.setText(profileTwitter.getDescription());
            name.setText(profileTwitter.getName());
            screen_name.setText("@" + profileTwitter.getScreen_name());
            //counters
            followers_count.setText(profileTwitter.getFollowers_count());
            friends_count.setText(profileTwitter.getFriends_count());
            statuses_count.setText(profileTwitter.getStatuses_count());

            //images
            showImage(profileTwitter.getProfile_banner_url(), null, profile_banner_url);
            showImage(profileTwitter.getProfile_image_url(), profile_image_url, null);

            if (profileTwitter.getUrl().isEmpty()) {
                containersite.removeAllViews();
            }


        }
    }

    private void goToWebSite() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(profileTwitter.getUrl()));
        startActivity(i);
    }

    private void showImage(String url, CircleImageView circleImageView, ImageView imageView) {
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.twitter_splash_img)
                .into(circleImageView == null ? imageView : circleImageView);
    }


    public void action(View view) {
        if (view.getId() == R.id.site) {
            goToWebSite();
        } else if (view.getId() == R.id.back) {
            finish();
        }
    }


}
