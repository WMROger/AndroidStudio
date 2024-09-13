package com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.LoginActivity;
import com.example.navigationdrawer.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView SignupLoginTxt = findViewById(R.id.SignUpLoginTxt);
        TextView SignupTermsnCond = findViewById(R.id.Signup_Terms_Conditions);
        Button btnDone = findViewById(R.id.btnDone);

        //Done button function
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, TermsConditionsActivity.class);
            startActivity(intent);
            finish();
        });

        //Login button/text function
        SignupLoginTxt.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        SignupLoginTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change color when touched (hovered)
                        SignupLoginTxt.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Reset color when touch released
                        SignupLoginTxt.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
                return false;
            }
        });


    }
}