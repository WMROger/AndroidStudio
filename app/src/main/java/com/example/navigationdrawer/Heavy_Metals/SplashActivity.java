package com.example.navigationdrawer.Heavy_Metals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.AuthenticationActivity;
import com.example.navigationdrawer.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //STATUS BAR COLOR TO SET BELOW
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.custom_orange));
        // Start MainActivity after a delay (e.g., 2 seconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish();  // Finish SplashActivity so it doesn't appear on back stack
            }
        }, 2000); // 2000ms delay (2 seconds)
    }
}
