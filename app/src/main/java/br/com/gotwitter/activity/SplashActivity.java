package br.com.gotwitter.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import br.com.gotwitter.R;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


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
