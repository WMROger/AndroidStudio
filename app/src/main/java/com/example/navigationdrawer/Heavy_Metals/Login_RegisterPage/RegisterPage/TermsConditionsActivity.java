package com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.RegisterPage.ProfileCreation.ProfileCreation;
import com.example.navigationdrawer.R;

public class TermsConditionsActivity extends AppCompatActivity {
    //initialize variables
    CheckBox tncConfirmation;
    Button tncIagree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms_conditions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //assign variables
        tncConfirmation = findViewById(R.id.tncConfirmation);
        tncIagree = findViewById(R.id.tncIagree);

        //check if checkbox is checked
        tncConfirmation.setOnCheckedChangeListener((buttonView, isChecked) -> {

            tncIagree.setEnabled(isChecked);
        });

        //button to go next
        tncIagree.setOnClickListener(view -> {
            Intent intent = new Intent(TermsConditionsActivity.this, ProfileCreation.class);
           startActivity(intent);
        });
    }
}