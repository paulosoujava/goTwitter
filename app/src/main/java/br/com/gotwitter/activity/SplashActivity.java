package br.com.gotwitter.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.com.gotwitter.R;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandle;
    private TextView by;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        by = findViewById(R.id.title_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                by,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);

        scaleDown.setRepeatCount(3);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

        mHandle = new Handler();
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMain();
            }
        }, 2000);
    }

    private void goToMain() {
        Intent intent = new Intent(SplashActivity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
    }
}
