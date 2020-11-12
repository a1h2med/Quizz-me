package com.example.quizz_me;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {
    private TextView appLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appLogo = findViewById(R.id.logo);
//        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
//        appLogo.setTypeface(typeface);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        appLogo.setAnimation(animation);
//        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//        startActivity(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }).start();
    }
}